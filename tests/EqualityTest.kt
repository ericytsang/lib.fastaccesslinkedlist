
import org.junit.Test

/**
 * Created by Eric Tsang on 1/13/2016.
 */
class EqualityTest
{
    @Test
    fun equalityWithNullTest()
    {
        val o:Any? = null
        assert(o == null)
        assert(o != "hello")
    }

    @Test
    fun equalityWithNonNullTest()
    {
        val o:Any? = "hello"
        assert(o == "hello")
        assert(o != null)
        assert(o != "hi")
    }
}
