import org.junit.Test
import java.util.*

/**
 * Created by Eric Tsang on 1/13/2016.
 */
class FastAccessLinkedListTest1
{
    fun compare(list1:List<*>,list2:List<*>)
    {
        assert(list1.size == list2.size)
        for(i in list1.indices)
        {
            assert(list1[i] == list2[i])
        }
    }

    @Test
    fun addAllTest()
    {
        val list = FastAccessLinkedList(arrayListOf(1,2,4,5,3,3,2,2,5,3))
        println("$list, size: ${list.size}")
        assert(list == listOf(1,2,4,5,3,3,2,2,5,3))
        compare(list,listOf(1,2,4,5,3,3,2,2,5,3))
    }

    @Test
    fun accessTest()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        assert(list[5] == 5)
        assert(list[1] == 1)
        assert(list[8] == 8)
        assert(list[0] == 0)
        assert(list[9] != 90)
        assert(list[19] == 19)
        println("$list, size: ${list.size}")
        assert(list == listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        compare(list,listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
    }

    @Test
    fun removeAtTest1()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        list.removeAt(5)
        list.removeAt(10)
        list.removeAt(8)
        list.removeAt(19)
        list.removeAt(0)
        list.removeAt(24)
        println("$list, size: ${list.size}")
        compare(list,listOf(1,2,3,4,6,7,8,10,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28))
        assert(list == listOf(1,2,3,4,6,7,8,10,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28))
    }

    @Test
    fun removeAtTest2()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        list.removeAt(10)
        list.removeAt(10)
        list.removeAt(10)
        list.removeAt(10)
        list.removeAt(10)
        println("$list, size: ${list.size}")
        assert(list[10] == 15)
        assert(list[9] == 9)
        compare(list,listOf(0,1,2,3,4,5,6,7,8,9,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        assert(list == listOf(0,1,2,3,4,5,6,7,8,9,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
    }

    @Test
    fun removeAtTest3()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        list.removeAt(10)
        list.removeAt(11)
        list.removeAt(12)
        list.removeAt(13)
        list.removeAt(14)
        println("$list, size: ${list.size}")
        assert(list[10] == 11)
        assert(list[9] == 9)
        compare(list,listOf(0,1,2,3,4,5,6,7,8,9,11,13,15,17,19,20,21,22,23,24,25,26,27,28,29))
        assert(list == listOf(0,1,2,3,4,5,6,7,8,9,11,13,15,17,19,20,21,22,23,24,25,26,27,28,29))
    }

    @Test
    fun removeAtTest4()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        list.removeAt(14)
        list.removeAt(13)
        list.removeAt(12)
        list.removeAt(11)
        list.removeAt(10)
        println("$list, size: ${list.size}")
        assert(list[10] == 15)
        assert(list[9] == 9)
        compare(list,listOf(0,1,2,3,4,5,6,7,8,9,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        assert(list == listOf(0,1,2,3,4,5,6,7,8,9,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
    }

    @Test
    fun addTest1()
    {
        val list = FastAccessLinkedList<Int>()
        list.add(0,0)
        list.add(0,1)
        list.add(0,2)
        list.add(0,3)
        list.add(0,4)
        println("$list, size: ${list.size}")
        assert(list == listOf(4,3,2,1,0))
        compare(list,listOf(4,3,2,1,0))
    }

    @Test
    fun addTest2()
    {
        val list = FastAccessLinkedList<Int>()
        list.add(0,0)
        list.add(1,1)
        list.add(2,2)
        list.add(3,3)
        list.add(4,4)
        println("$list, size: ${list.size}")
        assert(list == listOf(0,1,2,3,4))
        compare(list,listOf(0,1,2,3,4))
    }

    @Test
    fun removeTest()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,19,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        list.removeFirstOccurrence(5)
        list.removeLastOccurrence(10)
        list.removeFirstOccurrence(8)
        list.removeLastOccurrence(19)
        list.removeFirstOccurrence(0)
        list.removeLastOccurrence(29)
        println("$list, size: ${list.size}")
        assert(list == listOf(1,2,3,4,6,7,9,19,11,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28))
        compare(list,listOf(1,2,3,4,6,7,9,19,11,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28))
    }

    @Test
    fun concurrentModificationException()
    {
        var thrown = false
        try
        {
            val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5))
            val it = list.listIterator()
            list.add(6)
            it.next()
        }
        catch(ex:Exception)
        {
            ex.printStackTrace()
            thrown = true
        }
        assert(thrown)
    }

    @Test
    fun addFirstAddLastTest()
    {
        val list = FastAccessLinkedList<Int>()
        list.addLast(0)
        list.addFirst(1)
        list.addLast(2)
        list.addFirst(3)
        list.addLast(4)
        list.addFirst(5)
        list.addLast(6)
        assert(list == listOf(5,3,1,0,2,4,6))
    }

    @Test
    fun pollFirstPollLastTest()
    {
        val list = FastAccessLinkedList(listOf(5,3,1,null,2,null,6))
        assert(list.pollFirst() == 5)
        assert(list.pollLast() == 6)
        assert(list.pollFirst() == 3)
        assert(list.pollFirst() == 1)
        assert(list.pollFirst() == null)
        assert(list.pollLast() == null)
        assert(list.pollLast() == 2)
        assert(list.pollFirst() == null)
        assert(list.pollLast() == null)
        assert(list == emptyList<Int?>())
    }

    @Test
    fun removeFirstRemoveLastTest()
    {
        val list = FastAccessLinkedList(listOf(5,3,1,null,2,null,6))
        assert(list.removeFirst() == 5)
        assert(list.removeLast() == 6)
        assert(list.removeFirst() == 3)
        assert(list.removeFirst() == 1)
        assert(list.removeFirst() == null)
        assert(list.removeLast() == null)
        assert(list.removeLast() == 2)
        assert(list == emptyList<Int?>())
    }

    @Test
    fun offerFirstOfferLastTest()
    {
        val list = FastAccessLinkedList<Int>()
        assert(list.offerLast(0))
        assert(list.offerFirst(1))
        assert(list.offerLast(2))
        assert(list.offerFirst(3))
        assert(list.offerLast(4))
        assert(list.offerFirst(5))
        assert(list.offerLast(6))
        assert(list == listOf(5,3,1,0,2,4,6))
    }

    @Test
    fun getFirstThrowsExceptionWhenEmptyTest()
    {
        var exceptionThrown = false
        try
        {
            val list = FastAccessLinkedList<Int?>()
            list.first
        }
        catch (ex:NoSuchElementException)
        {
            exceptionThrown = true
        }
        assert(exceptionThrown,{"no exception was thrown"})
    }

    @Test
    fun getLastThrowsExceptionWhenEmptyTest()
    {
        var exceptionThrown = false
        try
        {
            val list = FastAccessLinkedList<Int?>()
            list.last
        }
        catch (ex:NoSuchElementException)
        {
            exceptionThrown = true
        }
        assert(exceptionThrown,{"no exception was thrown"})
    }

    @Test
    fun removeFirstThrowsExceptionWhenEmptyTest()
    {
        var exceptionThrown = false
        try
        {
            val list = FastAccessLinkedList<Int?>()
            list.removeFirst()
        }
        catch (ex:NoSuchElementException)
        {
            exceptionThrown = true
        }
        assert(exceptionThrown,{"no exception was thrown"})
    }

    @Test
    fun removeLastThrowsExceptionWhenEmptyTest()
    {
        var exceptionThrown = false
        try
        {
            val list = FastAccessLinkedList<Int?>()
            list.removeLast()
        }
        catch (ex:NoSuchElementException)
        {
            exceptionThrown = true
        }
        assert(exceptionThrown,{"no exception was thrown"})
    }

    @Test
    fun getFirstGetLastTest()
    {
        val list = FastAccessLinkedList<Int?>()
        list.addFirst(null)
        assert(list.first == null)
        list.addFirst(0)
        assert(list.first == 0)
        assert(list.last == null)
        list.addLast(0)
        assert(list.last == 0)
        assert(list.size == 3)
    }

    @Test
    fun peekFirstPeekLastTest()
    {
        val list = FastAccessLinkedList<Int?>()
        assert(list.peekFirst() == null)
        assert(list.peekLast() == null)
        list.addFirst(null)
        assert(list.peekFirst() == null)
        list.addFirst(0)
        assert(list.peekFirst() == 0)
        assert(list.peekLast() == null)
        list.addLast(0)
        assert(list.peekLast() == 0)
        assert(list.size == 3)
    }
}
