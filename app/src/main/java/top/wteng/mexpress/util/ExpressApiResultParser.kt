package top.wteng.mexpress.util

import org.json.JSONArray
import org.json.JSONObject
import top.wteng.mexpress.entity.TraceResultItem

object ExpressApiResultParser {
    fun getTraceFromJson(jsonStr: String): MutableMap<String, Any> {
        val trace = mutableMapOf<String, Any>()
        var json = jsonStr
        if (json == "") {
            json = "{}"
        }
        val jsonObj = JSONObject(json)
        with(trace) {
            put("eBusinessId", jsonObj.optString("EBusinessId", ""))
            put("orderCode", jsonObj.optString("OrderCode", ""))
            put("shipperCode", jsonObj.optString("ShipperCode", ""))
            put("state", jsonObj.optInt("State", 0))
            put("success", jsonObj.optBoolean("Success", false))
            put("logisticCode", jsonObj.optString("LogisticCode", ""))
            put("fullTrace", traceParse(jsonObj.optString("Traces", "[]")))
            put("fullTraceStr", jsonObj.optString("Traces", "[]"))
        }
//        val fullTrace = jsonObj.optString("Traces", "")
        return trace
    }

    fun changeTraceMapToObj(traceMap: MutableMap<String, Any>): MutableList<TraceResultItem> {
        val traceItemList = mutableListOf<TraceResultItem>()
        val trace = traceMap["fullTrace"] as MutableList<MutableMap<String, String>>
        trace.forEach { item ->
            with(traceItemList) {
                add(TraceResultItem(eBusinessId = traceMap["eBusinessId"].toString(),
                    orderCode = traceMap["orderCode"].toString(),
                    shipperCode = traceMap["shipperCode"].toString(),
                    state = traceMap["state"].toString().toInt(),
                    success = traceMap["success"].toString().toBoolean(),
                    logisticCode = traceMap["logisticCode"].toString(),
                    acceptTime = item["acceptTime"].toString(),
                    acceptStation = item["acceptStation"].toString()))
            }
        }
        return traceItemList
    }

    fun traceParse(traceStr: String): MutableList<Map<String, String>> {
        val fullTrace = mutableListOf<Map<String, String>>()
//        val fullTrace = mutableMapOf<String, String>()
        val fullTraceArr = JSONArray(traceStr)
        if (fullTraceArr.length() == 0) {
            return fullTrace
        }
        for (i in (0 until fullTraceArr.length())) {
            val curTraceStr = fullTraceArr.get(i).toString()
            val curTraceObj = JSONObject(curTraceStr)
            fullTrace.add(mapOf("acceptTime" to curTraceObj.optString("AcceptTime", curTraceObj.optString("acceptTime", "")),
                                "acceptStation" to curTraceObj.optString("AcceptStation", curTraceObj.optString("acceptStation", ""))))
        }
//        fullTraceArr.
        fullTrace.reverse()
        return fullTrace
    }
}