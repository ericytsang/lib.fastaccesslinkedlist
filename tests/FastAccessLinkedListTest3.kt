import org.junit.Test
import java.util.ArrayList

/**
 * Created by Eric Tsang on 1/13/2016.
 */
class FastAccessLinkedListTest3
{
    val falist = FastAccessLinkedList<Int>()
    val llist = LinkedList<Int>()
    val alist = ArrayList<Int>(10001)
    val range = 0..100

    init
    {
        falist.addAll(0..10000)
        llist.addAll(0..10000)
        alist.addAll(0..10000)
    }

    fun compare(list1:List<*>,list2:List<*>)
    {
        assert(list1.size == list2.size)
        for(i in list1.indices)
        {
            assert(list1[i] == list2[i])
        }
    }

    @Test
    fun accessTest1()
    {
        print("indices: ")
        for(i in range)
        {
            val index = (Math.random()*alist.lastIndex).toInt()
            assert(llist[index] == index)
            print(if (i == 0) index else ", $index")
        }
        println()
        for(i in range) assert(llist == alist)
        for(i in range) compare(llist,alist)
    }

    @Test
    fun accessTest2()
    {
        print("indices: ")
        for(i in range)
        {
            val index = (Math.random()*alist.lastIndex).toInt()
            assert(llist[index] == index)
            print(if (i == 0) index else ", $index")
        }
        println()
        for(i in range) assert(falist == alist)
        for(i in range) compare(falist,alist)
    }
}
