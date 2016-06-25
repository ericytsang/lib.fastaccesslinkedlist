package com.github.ericytsang.lib.fastaccesslinkedlist

import java.util.*
import com.github.ericytsang.lib.cache.Cache
import com.github.ericytsang.lib.cache.LruEvictionPolicy

class FastAccessLinkedList<E>(elements:Iterable<E> = emptyList(),numCachedNodes:Int = 5):AbstractSequentialList<E>()
{
    private val nodeCache = Cache<Int,INode<E>>(LruEvictionPolicy<Int>(),numCachedNodes)

    private val nullNode = object:INode<E>
    {
        override var prev:INode<E>
            get() = throw IllegalStateException()
            set(value) = throw IllegalStateException()
        override var data:E
            get() = throw IllegalStateException()
            set(value) = throw IllegalStateException()
        override var next:INode<E>
            get() = throw IllegalStateException()
            set(value) = throw IllegalStateException()
    }

    private var firstNode:INode<E> = nullNode

    private var lastNode:INode<E> = nullNode

    override var size:Int = 0
        private set

    init
    {
        if (numCachedNodes < 1) throw IllegalArgumentException("numCachedNodes must be >= 1");
        addAll(elements);
    }

    /**
     * add this node to the cached nodes and remove previous (if any) to maintain its LRU ordering.
     */
    private fun cacheNode(index:Int,node:INode<E>)
    {
        if (node != firstNode && node != lastNode)
        {
            nodeCache[index] = node
        }
    }

    private fun node(index:Int):INode<E>
    {
        // resolve the node nearest the specified index will at least be
        // resolved to either the first or last node
        var nearestNode = if (index < size shr 1) IndexedNode(0,firstNode) else IndexedNode(lastIndex,lastNode)
        nodeCache.entries.forEach()
        {
            if (Math.abs(it.key-index) < Math.abs(nearestNode.index-index))
                nearestNode = IndexedNode(it.key,it.value)
        }

        // iterate through the list until we reach the requested node
        var node = nearestNode.node

        // println("operation: get($index), index distance: ${Math.abs(nodeIndex-index)}")

        if (nearestNode.index > index)
        {
             for (i in index+1..nearestNode.index) {node = node.prev}
        }
        else
        {
             for (i in nearestNode.index+1..index) {node = node.next}
        }

        cacheNode(index,node)

        return node
    }

    override fun listIterator(index:Int) = object:MutableListIterator<E>
    {
        private var lastReturned:INode<E> = nullNode
        private var next:INode<E> = nullNode
        private var nextIndex:Int = 0
        private var expectedModCount = modCount

        init
        {
            next = if (index == size) nullNode else node(index)
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
            next = next.next
            nextIndex++
            return lastReturned.data
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

            next = if (next === nullNode) lastNode else next.prev
            lastReturned = next
            nextIndex--
            return lastReturned.data
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
            nodeCache.remove(nextIndex)
            if (lastNext !== nullNode) cacheNode(nextIndex,lastNext)

            // update indices in node cache
            val newMapping = nodeCache.entries.associate()
            {
                if (it.key > nextIndex)
                    it.key-1 to it.value
                else
                    it.key to it.value
            }
            nodeCache.clear()
            nodeCache.putAll(newMapping)

            lastReturned = nullNode
            expectedModCount++
        }

        override fun set(element:E)
        {
            checkForComodification()
            lastReturned.data = element
        }

        override fun add(element:E)
        {
            checkForComodification()
            lastReturned = nullNode

            if (next === nullNode)
            {
                linkLast(element)
            }
            else
            {
                linkBefore(element,next)
            }

            // update indices in node cache
            val newMapping = nodeCache.entries.associate()
            {
                if (it.key >= nextIndex)
                    it.key+1 to it.value
                else
                    it.key to it.value
            }
            nodeCache.clear()
            nodeCache.putAll(newMapping)

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
        private fun unlink(outcast:INode<E>):E
        {
            val element = outcast.data
            val next = outcast.next
            val prev = outcast.prev

            if (prev === nullNode)
            {
                firstNode = next
            }
            else
            {
                prev.next = next
                outcast.prev = nullNode
            }

            if (next === nullNode)
            {
                lastNode = prev
            }
            else
            {
                next.prev = prev
                outcast.next = nullNode
            }

            size--
            modCount++
            return element
        }

        /**
         * adds [element] as last element of the list.
         */
        private fun linkLast(element:E)
        {
            val prev = lastNode
            val newNode = Node(prev,element,nullNode)
            lastNode = newNode
            if (prev === nullNode)
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
        private fun linkBefore(element:E,next:INode<E>)
        {
            val prev = next.prev
            val newNode = Node(prev,element,next)
            next.prev = newNode
            if (prev === nullNode)
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

    private interface INode<D>
    {
        var prev:INode<D>
        var data:D
        var next:INode<D>
    }

    private class Node<D>(override var prev:INode<D>,override var data:D,override var next:INode<D>):INode<D>

    private class IndexedNode<D>(var index:Int,var node:INode<D>)
    {
        override fun hashCode():Int = index
        override fun equals(other:Any?):Boolean = other?.hashCode() ?: index+1 == index
    }
}
