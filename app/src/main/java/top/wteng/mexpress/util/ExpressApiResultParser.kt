package top.wteng.mexpress.util

import org.json.JSONArray
import org.json.JSONObject

object ExpressApiResultParser {
    fun getTraceFromJson(jsonStr: String): MutableMap<String, Any> {
        val trace = mutableMapOf<String, Any>()
        var json = jsonStr
        if (json == "") {
            json = "{}"
        }
        val jsonObj = JSONObject(json)
        println(jsonObj)
        with(trace) {
            put("eBusinessId", jsonObj.optString("EBusinessId", ""))
            put("orderCode", jsonObj.optString("OrderCode", ""))
            put("shipperCode", jsonObj.optString("ShipperCode", ""))
            put("state", jsonObj.optInt("State", 0))
            put("success", jsonObj.optBoolean("Success", false))
            put("logisticCode", jsonObj.optString("LogisticCode", ""))
            put("fullTrace", traceParse(jsonObj.optString("Traces", "[]")))
        }
//        val fullTrace = jsonObj.optString("Traces", "")
        return trace
    }

    fun traceParse(traceStr: String): MutableList<Map<String, String>> {
        val fullTrace = mutableListOf<Map<String, String>>()
//        val fullTrace = mutableMapOf<String, String>()
        val fullTraceArr = JSONArray(traceStr)
        when(fullTraceArr.length()) {
            0 -> return fullTrace
        }
        for (i in (0 until fullTraceArr.length())) {
            val curTraceStr = fullTraceArr.get(i).toString()
            val curTraceObj = JSONObject(curTraceStr)
            fullTrace.add(mapOf("acceptTime" to curTraceObj.optString("AcceptTime", ""),
                                "acceptStation" to curTraceObj.optString("AcceptStation", "")))
        }
//        fullTraceArr.
        return fullTrace
    }
}