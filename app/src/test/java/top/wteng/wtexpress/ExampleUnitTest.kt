package top.wteng.wtexpress

import org.junit.Test

import org.junit.Assert.*
import top.wteng.wtexpress.util.OrderTraceUtil

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val expressTrace = OrderTraceUtil.getTraceMap("STO", "3720510705418")
        println(expressTrace)
    }
}
