import org.junit.Test

/**
 * Created by Eric Tsang on 1/13/2016.
 */
class ObjectTest
{
    fun obj():Interface = object:Interface{}

    interface Interface

    @Test
    fun getMultipleObjects()
    {
        assert(obj() !== obj())
    }
}
