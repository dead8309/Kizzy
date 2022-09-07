package com.my.kizzy.ui.screen.rpc.custom

data class Rpc(
    var name: String,
    var details:String?,
    var state: String?,
    var startTime:Long?,
    var StopTime: Long?,
    var status:String?,
    var button1: String?,
    var button2:String?,
    var button1Url: String?,
    var button2Url:String?,
    var largeImg:String?,
    var smallImg:String?,
    var type: Int?
)