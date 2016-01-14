import org.junit.Test
import java.util.ArrayList

/**
 * Created by Eric Tsang on 1/13/2016.
 */
class FastAccessLinkedListTest3
{
    val iterations = 0..10000
    val elements = 0..100000
    val accessed = 45000..55000
    val falist = FastAccessLinkedList<Int>()
    val llist = LinkedList<Int>()
    val alist = ArrayList<Int>()

    init
    {
        falist.addAll(elements)
        llist.addAll(elements)
        alist.addAll(elements)
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
        print("accesses: ")
        for(i in iterations)
        {
            val index = (accessed.start+Math.random()*(accessed.endInclusive-accessed.start)).toInt()
            print(if (i == 0) llist[index] else ", ${llist[index]}")
        }
        println()
    }

    @Test
    fun accessTest2()
    {
        print("accesses: ")
        for(i in iterations)
        {
            val index = (accessed.start+Math.random()*(accessed.endInclusive-accessed.start)).toInt()
            print(if (i == 0) falist[index] else ", ${falist[index]}")
        }
        println()
    }

    @Test
    fun accessTest3()
    {
        compare(llist,alist)
    }

    @Test
    fun accessTest4()
    {
        compare(falist,alist)
    }
}
