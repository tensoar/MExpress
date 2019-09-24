package top.wteng.mexpress.util

import android.content.Context

class OrderTraceUtil {
    companion object {
        fun getTraceMap(context: Context, expCode: String, expNo: String): MutableMap<String, Any> {
            val orderTraceRequest = OrderTraceRequest(context)
            val result = orderTraceRequest.getOrderTracesByJson(expCode, expNo)
            return ExpressApiResultParser.getTraceFromJson(result)
        }
    }
}