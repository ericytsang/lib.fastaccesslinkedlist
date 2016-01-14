import java.util.AbstractSequentialList
import java.util.ConcurrentModificationException
import java.util.Deque
import java.util.WeakHashMap
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by Eric Tsang on 1/13/2016.
 */

open class FastAccessLinkedList<E>(elements:Collection<E> = emptyList(),numCachedNodes:Int = 10):AbstractSequentialList<E>(),Deque<E>
{
    /**
     * [LinkedBlockingQueue] ordered by LRU of [Pair]s of [Node]s and their
     * index in the [FastAccessLinkedList].
     */
    private val cachedNodes = LinkedBlockingQueue<IndexedNode<E>>(numCachedNodes)

    /**
     * holds a weak referee to all of this [FastAccessLinkedList]'s
     * [MutableListIterator]s.
     */
    private val listIterators = WeakHashMap<MutableListIterator<E>,MutableListIterator<E>>()

    private var firstNode:Node<E>? = null

    private var lastNode:Node<E>? = null

    override var size:Int = 0
        protected set

    init
    {
        addAll(elements)
    }

    override fun addFirst(e:E) = add(0,e)
    override fun addLast(e:E) = add(lastIndex,e)

    override fun element():E = first
    override fun getLast():E = last()
    override fun getFirst():E = first()

    override fun peek():E? = peekFirst()
    override fun peekFirst():E? = firstOrNull()
    override fun peekLast():E? = lastOrNull()

    override fun remove():E = removeFirst()
    override fun removeFirst():E = super.removeAt(0)
    override fun removeLast():E = super.removeAt(lastIndex)

    override fun pop():E = removeFirst()
    override fun push(e:E) = addFirst(e)

    override fun offer(e:E):Boolean = offerLast(e)
    override fun offerFirst(e:E):Boolean
    {
        addFirst(e)
        return true
    }

    override fun offerLast(e:E):Boolean
    {
        addLast(e)
        return true
    }

    override fun poll():E? = pollFirst()
    override fun pollFirst():E?
    {
        val e = peekFirst()
        if (e != null) removeFirst()
        return e
    }

    override fun pollLast():E?
    {
        val e = peekLast()
        if (e != null) removeLast()
        return e
    }

    fun removeFirstOccurrence(o:Any?,it:MutableIterator<E>):Boolean
    {
        while (it.hasNext())
        {
            if (o == it.next())
            {
                it.remove()
                return true
            }
        }
        return false
    }

    override fun removeFirstOccurrence(o:Any?):Boolean = removeFirstOccurrence(o,iterator())
    override fun removeLastOccurrence(o:Any?):Boolean = removeFirstOccurrence(o,descendingIterator())

    override fun descendingIterator() = object:MutableIterator<E>
    {
        val it = listIterator(size)
        override fun remove() = it.remove()
        override fun hasNext():Boolean = it.hasPrevious()
        override fun next():E = it.previous()
    }

    private fun node(index:Int):Node<E>? = synchronized(cachedNodes)
    {
        // fail fast if index is out of bounds
        if (index !in indices) return null

        // resolve the node nearest the specified index
        val nearestNode = run()
        {
            var bestCandidate:IndexedNode<E>? = null

            // try to resolve the nearest node from cached nodes
            cachedNodes
                // plus the firstNode and lastNode so that the bestCandidate
                // will be at least resolved to either the first or last node
                .plus(IndexedNode(0,firstNode!!))
                .plus(IndexedNode(lastIndex,lastNode!!))
                .forEach()
                {
                    if (bestCandidate == null || Math.abs(it.index-index) < Math.abs(bestCandidate!!.index-index))
                    {
                        bestCandidate = it
                    }
                }

            return@run bestCandidate!!
        }

        // iterate through the list until we reach the requested node
        var nodeIndex = nearestNode.index
        var node = nearestNode.node
        debug("index distance: ${Math.abs(nodeIndex - index)}")

        while (nodeIndex > index)
        {
            node = node.prev!!
            nodeIndex--
        }

        while (nodeIndex < index)
        {
            node = node.next!!
            nodeIndex++
        }

        // add this node to the cached nodes and remove previous (if any) to
        // maintain its LRU ordering
        val resultNode = IndexedNode(nodeIndex,node)
        cachedNodes.remove(resultNode)
        if (cachedNodes.remainingCapacity() == 0)
        {
            cachedNodes.poll()
        }
        cachedNodes.offer(resultNode)

        return node
    }

    override fun listIterator(index:Int) = object:MutableListIterator<E>
    {
        val mutex = Any()

        var nextIndex = index
        var nextNode:Node<E>? = node(nextIndex)
        var prevIndex = index-1
        var prevNode:Node<E>? = node(prevIndex)
        var subjectNode:Node<E>? = null

        init
        {
            // register self with list
            listIterators.put(this,this)
        }

        override fun add(element:E) = synchronized(mutex)
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()

            // insert the element into the list
            val newNode = Node(nextNode,prevNode,element)
            nextNode?.prev = newNode
            prevNode?.next = newNode
            size++

            // adjust indices of cached nodes
            cachedNodes.filter {it.index >= nextIndex}. forEach {it.index++}

            // update indices and node pointers
            nextIndex++
            prevNode = newNode
            prevIndex++

            // update first and last pointers
            if (prevIndex == 0) firstNode = prevNode
            if (prevIndex == lastIndex) lastNode = prevNode

            // make other list iterators associated with this list throw exceptions
            listIterators.values.removeAll {it !== this}

            return@synchronized Unit
        }

        override fun remove() = synchronized(mutex)
        {
            // fail fast if there is no node to remove or concurrent modification
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()
            val subjectNode = subjectNode ?: throw IllegalStateException("neither next nor previous have been called, or remove or add have been called after the last call to next or previous")
            val subjectIndex:Int

            // update node pointers & indices
            when
            {
                nextNode == subjectNode ->
                {
                    nextNode = subjectNode.next
                    subjectIndex = nextIndex++
                }
                prevNode == subjectNode ->
                {
                    prevNode = subjectNode.prev
                    subjectIndex = prevIndex--
                }
                else -> throw RuntimeException("else case executed")
            }

            // adjust indices of cached nodes
            cachedNodes.remove(IndexedNode(subjectIndex,subjectNode))
            cachedNodes.filter {it.index >= nextIndex}. forEach {it.index--}

            // remove the subject node
            nextNode?.prev = prevNode
            prevNode?.next = nextNode
            subjectNode.prev = null
            subjectNode.next = null
            if (prevNode == null) firstNode = nextNode
            if (nextNode == null) lastNode = prevNode
            this.subjectNode = null
            size--

            // update indices
            nextIndex--

            // make other list iterators associated with this list throw exceptions
            listIterators.values.removeAll {it !== this}

            return@synchronized Unit
        }

        override fun set(element:E)
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()
            subjectNode!!.data = element
        }

        override fun hasNext():Boolean
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()
            return nextNode != null
        }
        override fun hasPrevious():Boolean
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()
            return prevNode != null
        }

        override fun next():E = synchronized(mutex)
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()

            debug("next()")
            val result = nextNode!!.data
            subjectNode = nextNode
            prevNode = nextNode
            prevIndex = nextIndex
            nextNode = nextNode!!.next
            nextIndex++
            return result
        }

        override fun previous():E = synchronized(mutex)
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()

            debug("previous()")
            val result = prevNode!!.data
            subjectNode = prevNode
            nextNode = prevNode
            nextIndex = prevIndex
            prevNode = prevNode!!.prev
            prevIndex--
            return result
        }

        override fun nextIndex():Int
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()
            return nextIndex
        }
        override fun previousIndex():Int
        {
            if (!listIterators.containsKey(this)) throw ConcurrentModificationException()
            return prevIndex
        }
    }
}

private data class Node<D>(var next:Node<D>?,var prev:Node<D>?,var data:D)

private data class IndexedNode<D>(var index:Int,var node:Node<D>)

private fun debug(string:String)
{
    // println(string)
}
