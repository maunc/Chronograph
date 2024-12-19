package com.maunc.chronograph.bean

data class TimeData(
    var index: Int,//名次
    var time: String,//使用时间
    var gapTime: String,//与上一名时间差距
)
