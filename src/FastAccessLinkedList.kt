import java.util.AbstractSequentialList
import java.util.ConcurrentModificationException
import java.util.Deque
import java.util.NoSuchElementException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Created by Eric Tsang on 1/13/2016.
 */

class FastAccessLinkedList<E>(elements:Collection<E> = emptyList(),numCachedNodes:Int = 10):AbstractSequentialList<E>(),Deque<E>
{
    /**
     * [LinkedBlockingQueue] ordered by LRU of [Pair]s of [Node]s and their
     * index in the [FastAccessLinkedList].
     */
    private val cachedNodes = LinkedBlockingQueue<IndexedNode<E>>(numCachedNodes)

    private var firstNode:Node<E>? = null

    private var lastNode:Node<E>? = null

    private val lock = ReentrantReadWriteLock()

    override var size:Int = 0
        protected set

    init
    {
        addAll(elements)
    }

    override fun addFirst(e:E) = lock.write {add(0,e)}
    override fun addLast(e:E) = lock.write {add(lastIndex,e)}

    override fun element():E = first
    override fun getLast():E = lock.read {last()}
    override fun getFirst():E = lock.read {first()}

    override fun peek():E? = peekFirst()
    override fun peekFirst():E? = lock.read {firstOrNull()}
    override fun peekLast():E? = lock.read {lastOrNull()}

    override fun remove():E = removeFirst()
    override fun removeFirst():E = lock.write {super.removeAt(0)}
    override fun removeLast():E = lock.write {super.removeAt(lastIndex)}

    override fun pop():E = removeFirst()
    override fun push(e:E) = addFirst(e)

    override fun offer(e:E):Boolean = offerLast(e)
    override fun offerFirst(e:E):Boolean = lock.write()
    {
        addFirst(e)
        return true
    }
    override fun offerLast(e:E):Boolean = lock.write()
    {
        addLast(e)
        return true
    }

    override fun poll():E? = pollFirst()
    override fun pollFirst():E? = lock.write()
    {
        val e = peekFirst()
        if (e != null) removeFirst()
        return firstNode?.data
    }
    override fun pollLast():E? = lock.write()
    {
        val e = peekLast()
        if (e != null) removeLast()
        return e
    }

    private fun removeFirstOccurrence(o:Any?,it:MutableIterator<E>):Boolean = lock.write()
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

    /**
     * removes [outcast] from the list.
     */
    private fun unlink(outcast:Node<E>):E
    {
        val element = outcast.data
        val next = outcast.next
        val prev = outcast.prev

        if (prev == null)
        {
            firstNode = next
        }
        else
        {
            prev.next = next
            outcast.prev = null
        }

        if (next == null)
        {
            lastNode = prev
        }
        else
        {
            next.prev = prev
            outcast.next = null
        }

        outcast.data = null
        size--
        modCount++
        return element as E
    }

    /**
     * adds [element] as last element of the list.
     */
    private fun linkLast(element:E)
    {
        val prev = lastNode
        val newNode = Node(prev,element,null)
        lastNode = newNode
        if (prev == null)
        {
            firstNode = newNode
        }
        else
        {
            prev.next = newNode
        }
        size++
        modCount++
    }

    /**
     * inserts [element] before [next].
     */
    private fun linkBefore(element:E,next:Node<E>)
    {
        val prev = next.prev
        val newNode = Node(prev,element,next)
        next.prev = newNode
        if (prev == null)
        {
            firstNode = newNode
        }
        else
        {
            prev.next = newNode
        }
        size++
        modCount++
    }

    private fun node(index:Int):Node<E> = lock.read()
    {
        if (index !in indices) throw IndexOutOfBoundsException()

        // resolve the node nearest the specified index will at least be
        // resolved to either the first or last node
        val nearestNode = cachedNodes
            .plus(IndexedNode(0,firstNode!!))
            .plus(IndexedNode(lastIndex,lastNode!!))
            .minBy {Math.abs(it.index-index)}!!

        // iterate through the list until we reach the requested node
        var (nodeIndex,node) = nearestNode
        debug("index distance: ${Math.abs(nodeIndex-index)}")

        if (nodeIndex > index)
        {
            for (i in index+1..nodeIndex) node = node.prev!!
        }
        else
        {
            for (i in nodeIndex+1..index) node = node.next!!
        }

        // add this node to the cached nodes and remove previous (if any) to
        // maintain its LRU ordering
        val resultNode = IndexedNode(index,node)
        if (nearestNode == resultNode)
        {
            cachedNodes.remove(resultNode)
        }
        if (cachedNodes.remainingCapacity() == 0)
        {
            cachedNodes.poll()
        }
        cachedNodes.offer(resultNode)

        return node
    }

    override fun descendingIterator() = object:MutableIterator<E>
    {
        val it = listIterator(size)
        override fun remove() = it.remove()
        override fun hasNext():Boolean = it.hasPrevious()
        override fun next():E = it.previous()
    }

    override fun listIterator(index:Int) = object:MutableListIterator<E>
    {
        private var lastReturned:Node<E>? = null
        private var next:Node<E>? = null
        private var nextIndex:Int = 0
        private var expectedModCount = modCount

        init
        {
            next = if (index == size) null else node(index)
            nextIndex = index
        }

        override fun hasNext():Boolean
        {
            return nextIndex < size
        }

        override fun next():E
        {
            checkForComodification()
            if (!hasNext())
            {
                throw NoSuchElementException()
            }

            lastReturned = next
            next = next!!.next
            nextIndex++
            return lastReturned!!.data as E
        }

        override fun hasPrevious():Boolean
        {
            return nextIndex > 0
        }

        override fun previous():E
        {
            checkForComodification()
            if (!hasPrevious())
            {
                throw NoSuchElementException()
            }

            next = if (next == null) lastNode else next!!.prev
            lastReturned = next
            nextIndex--
            return lastReturned!!.data as E
        }

        override fun nextIndex():Int
        {
            return nextIndex
        }

        override fun previousIndex():Int
        {
            return nextIndex-1
        }

        override fun remove()
        {
            checkForComodification()
            if (lastReturned == null)
            {
                throw IllegalStateException()
            }

            val lastNext = lastReturned!!.next
            unlink(lastReturned!!)
            if (next === lastReturned)
            {
                next = lastNext
            }
            else
            {
                nextIndex--
            }
            cachedNodes.remove(IndexedNode(nextIndex,lastReturned!!))
            cachedNodes.filter {it.index > nextIndex}. forEach {it.index--}
            lastReturned = null
            expectedModCount++
        }

        override fun set(element:E)
        {
            if (lastReturned == null)
            {
                throw IllegalStateException()
            }
            checkForComodification()
            lastReturned!!.data = element
        }

        override fun add(element:E)
        {
            checkForComodification()
            lastReturned = null
            if (next == null)
            {
                linkLast(element)
            }
            else
            {
                linkBefore(element,next!!)
            }
            cachedNodes.filter {it.index >= nextIndex}. forEach {it.index++}
            nextIndex++
            expectedModCount++
        }

        internal fun checkForComodification()
        {
            if (modCount != expectedModCount)
            {
                throw ConcurrentModificationException()
            }
        }
    }

    private data class Node<D>(var prev:Node<D>?,var data:D?,var next:Node<D>?)

    private data class IndexedNode<D>(var index:Int,var node:Node<D>)

    private fun debug(string:String) = Unit//println(string)
}
