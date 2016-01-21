import org.junit.Test

/**
 * Created by Eric Tsang on 1/13/2016.
 */

val IntRange.size:Int get() = last-first+1

class FastAccessLinkedListTest3
{
    val iterations = 0..10000
    val elements = 0..10000
    val accessed = (elements.first+elements.size/2-5000).toInt()..(elements.first+elements.size/2+5000).toInt()
    val falist = FastAccessLinkedList<Int>()
    val llist = LinkedList<Int>()

    init
    {
        falist.addAll(elements)
        llist.addAll(elements)
    }

    @Test
    fun linkedListRandomlyAccessingElementsNearTheMiddleTest()
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
    fun fastAccessLinkedListRandomlyAccessingElementsNearTheMiddleTest()
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
    fun linkedListTestRandomlyAccessingElementsEverywhere()
    {
        print("accesses: ")
        for(i in iterations)
        {
            val index = (elements.start+Math.random()*(elements.endInclusive-elements.start)).toInt()
            print(if (i == 0) llist[index] else ", ${llist[index]}")
        }
        println()
    }

    @Test
    fun fastAccessLinkedListRandomlyAccessingElementsEverywhereTest()
    {
        print("accesses: ")
        for(i in iterations)
        {
            val index = (elements.start+Math.random()*(elements.endInclusive-elements.start)).toInt()
            print(if (i == 0) falist[index] else ", ${falist[index]}")
        }
        println()
    }

    @Test
    fun linkedListSequentiallyAccessingTheWithoutIteratorTest()
    {
        val stb = StringBuilder()
        stb.append("accesses: ")
        for(index in llist.indices) stb.append(if (index == 0) llist[index] else ", ${llist[index]}")
        println(stb)
    }

    @Test
    fun fastAccessLinkedListSequentiallyAccessingTheWithoutIteratorTest()
    {
        val stb = StringBuilder()
        stb.append("accesses: ")
        for(index in falist.indices) stb.append(if (index == 0) falist[index] else ", ${falist[index]}")
        println(stb)
    }
}
