package top.wteng.mexpress.entity

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

data class ExpressRecorder(
    @Column(unique = true)
    var id: Long = 0,

    @Column(unique = true)
    var number: String,
    var note: String = "",
    var company: String = "",
    var companyCode: String = "",
    var status: Int = -1,
    var lastTime: String = "",
    var lastTrace: String = "",
    var fullTrace: String = "[]"
): LitePalSupport()