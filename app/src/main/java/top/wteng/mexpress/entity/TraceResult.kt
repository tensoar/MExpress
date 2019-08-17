package top.wteng.mexpress.entity

data class TraceResultItem(
    var eBusinessId: String = "",
    var orderCode: String = "",
    var shipperCode: String = "",
    var state: Int = 0,
    var success: Boolean = false,
    var logisticCode: String = "",
    var acceptTime: String = "",
    var acceptStation: String = ""
)