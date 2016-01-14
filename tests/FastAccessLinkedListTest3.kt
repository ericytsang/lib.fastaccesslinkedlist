import org.junit.Test
import java.util.ArrayList

/**
 * Created by Eric Tsang on 1/13/2016.
 */
class FastAccessLinkedListTest3
{
    val iterations = 0..10000
    val elements = 0..10000
    val accessed = 4500..5500
    val falist = FastAccessLinkedList<Int>()
    val llist = LinkedList<Int>()

    init
    {
        falist.addAll(elements)
        llist.addAll(elements)
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
        val stb = StringBuilder()
        stb.append("accesses: ")
        for(index in llist.indices) stb.append(if (index == 0) llist[index] else ", ${llist[index]}")
        println(stb)
    }

    @Test
    fun accessTest4()
    {
        val stb = StringBuilder()
        stb.append("accesses: ")
        for(index in falist.indices) stb.append(if (index == 0) falist[index] else ", ${falist[index]}")
        println(stb)
    }
}
