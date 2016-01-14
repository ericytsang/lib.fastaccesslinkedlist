import org.junit.Test

/**
 * Created by Eric Tsang on 1/13/2016.
 */
class FastAccessLinkedListTest1
{
    @Test
    fun addAllTest()
    {
        val list = FastAccessLinkedList(arrayListOf(1,2,4,5,3,3,2,2,5,3))
        println("$list, size: ${list.size}")
        assert(list == listOf(1,2,4,5,3,3,2,2,5,3))
    }

    @Test
    fun accessTest()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        println("inserted everything")
        assert(list[5] == 5)
        println("5")
        assert(list[1] == 1)
        println("1")
        assert(list[8] == 8)
        println("8")
        assert(list[0] == 0)
        println("0")
        assert(list[9] != 90)
        println("9")
        assert(list[19] == 19)
        println("19")
        println("$list, size: ${list.size}")
        assert(list == listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
    }

    @Test
    fun removeAtTest()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        println("inserted everything")
        list.removeAt(5)
        println("finished list.removeAt(5)")
        list.removeAt(10)
        println("finished list.removeAt(10)")
        list.removeAt(8)
        println("finished list.removeAt(8)")
        list.removeAt(19)
        println("finished list.removeAt(19)")
        list.removeAt(0)
        println("finished list.removeAt(0)")
        println("$list, size: ${list.size}")
        assert(list == listOf(1,2,3,4,6,7,8,10,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29))
    }

    @Test
    fun removeTest()
    {
        val list = FastAccessLinkedList(arrayListOf(0,1,2,3,4,5,6,7,8,9,19,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29))
        println("inserted everything")
        list.removeFirstOccurrence(5)
        list.removeLastOccurrence(10)
        list.removeFirstOccurrence(8)
        list.removeLastOccurrence(19)
        list.removeFirstOccurrence(0)
        list.removeLastOccurrence(29)
        println("$list, size: ${list.size}")
        assert(list == listOf(1,2,3,4,6,7,9,19,11,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28))
    }
}
