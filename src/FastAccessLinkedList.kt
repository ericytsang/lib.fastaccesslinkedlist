import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class FastAccessLinkedList<E>(elements:Iterable<E> = emptyList(),numCachedNodes:Int = 5):AbstractSequentialList<E>()
{
    /**
     * [LinkedBlockingQueue] ordered by LRU of [Pair]s of [Node]s and their
     * index in the [FastAccessLinkedList].
     */
    private val nodeCache = LinkedBlockingQueue<IndexedNode<E>>(numCachedNodes)

    private var firstNode:Node<E>? = null

    private var lastNode:Node<E>? = null

    override var size:Int = 0
        protected set

    init
    {
        if (numCachedNodes < 1) throw IllegalArgumentException("numCachedNodes must be >= 1");
        addAll(elements);
    }

    /**
     * add this node to the cached nodes and remove previous (if any) to maintain its LRU ordering.
     */
    private fun cacheNode(index:Int,node:Node<E>)
    {
        if (node != firstNode && node != lastNode)
        {
            val resultNode = IndexedNode(index,node)
            if (!nodeCache.removeAll {it == resultNode} &&
                nodeCache.remainingCapacity() == 0)
            {
                nodeCache.remove()
            }
            nodeCache.add(resultNode)
        }
    }

    private fun node(index:Int):Node<E>
    {
        // resolve the node nearest the specified index will at least be
        // resolved to either the first or last node
        val nearestNode = nodeCache
            .plus(IndexedNode(0,firstNode!!))
            .plus(IndexedNode(lastIndex,lastNode!!))
            .minBy {Math.abs(it.index-index)}!!

        // iterate through the list until we reach the requested node
        var (nodeIndex,node) = nearestNode

        // println("operation: get($index), index distance: ${Math.abs(nodeIndex-index)}")

        if (nodeIndex > index)
        {
            for (i in index+1..nodeIndex) node = node.prev!!
        }
        else
        {
            for (i in nodeIndex+1..index) node = node.next!!
        }

        cacheNode(index,node)

        return node
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
            val lastReturned = lastReturned ?: throw IllegalStateException()
            val lastNext = lastReturned.next

            unlink(lastReturned)
            if (next === lastReturned)
            {
                next = lastNext
            }
            else
            {
                nextIndex--
            }

            // remove removed node from cache while caching the node next to it
            nodeCache.remove(IndexedNode(nextIndex,lastReturned))
            if (lastNext != null) cacheNode(nextIndex,lastNext)

            // update indices in node cache
            nodeCache.forEach {if (it.index > nextIndex) it.index--}

            this.lastReturned = null
            expectedModCount++
        }

        override fun set(element:E)
        {
            checkForComodification()
            (lastReturned ?: throw IllegalStateException()).data = element
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

            // update indices in node cache
            nodeCache.forEach {if (it.index >= nextIndex) it.index++}

            nextIndex++
            expectedModCount++
        }

        private fun checkForComodification()
        {
            if (modCount != expectedModCount)
            {
                throw ConcurrentModificationException()
            }
        }

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
    }

    private data class Node<D>(var prev:Node<D>?,var data:D?,var next:Node<D>?)

    private data class IndexedNode<D>(var index:Int,var node:Node<D>)
}
