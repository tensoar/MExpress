package top.wteng.mexpress.util

class OrderTraceUtil {
    companion object {
        private val orderTraceRequest = OrderTraceRequest()
        fun getTraceMap(expCode: String, expNo: String): MutableMap<String, Any> {
            val result = orderTraceRequest.getOrderTracesByJson(expCode, expNo)
            return ExpressApiResultParser.getTraceFromJson(result)
        }
    }
}