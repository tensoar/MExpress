package top.wteng.mexpress.entity

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import top.wteng.mexpress.util.ExpressApiResultParser

data class ExpressRecorder(
    @Column(unique = true)
    var id: Long = 0,

    @Column(unique = true)
    var number: String? = null,
    var note: String? = null,
    var company: String? = null,
    var companyCode: String? = null,
    var state: Int = 0,
    var lastTime: String? = null,
    var lastTrace: String? =null,
    var fullTrace: String? = null
): LitePalSupport() {
    fun toTraceResultObjList(): MutableList<TraceResultItem>{
        val resultList = mutableListOf<TraceResultItem>()
        val trace = ExpressApiResultParser.traceParse(fullTrace!!)
        println("-------------")
        println(trace)
        if (trace.size == 0) {
            TraceResultItem(
                state = 0,
                success = true,
                acceptTime = "",
                acceptStation = "暂无轨迹"
            )
        }else {
            trace.forEach { item ->
                with(resultList) {
                    add(
                        TraceResultItem(
                            state = state,
                            success = true,
                            acceptTime = item["acceptTime"].toString(),
                            acceptStation = item["acceptStation"].toString()
                        )
                    )
                }
            }
        }
        return resultList
    }
}