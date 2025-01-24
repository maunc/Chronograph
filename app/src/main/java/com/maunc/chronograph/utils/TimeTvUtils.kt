package com.maunc.chronograph.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


@SuppressLint("DefaultLocale", "SimpleDateFormat")
object TimeTvUtils {


    @JvmStatic
    fun formatTime(time: Float): String {
        val minutes = (time / 60).toInt() // 转换为分钟
        val remainingSeconds = time % 60 // 计算剩余的秒数
        return String.format("%02d", minutes) + ":" + String.format("%05.2f", remainingSeconds)
    }

    @JvmStatic
    fun timeUnitMillion(lapSpeedMillions: Long): String { // int * 1000
        val minutes = TimeUnit.MILLISECONDS.toMinutes(lapSpeedMillions)
        val seconds =
            (TimeUnit.MILLISECONDS.toSeconds(lapSpeedMillions) - TimeUnit.MINUTES.toSeconds(minutes))
        val seconds2 = lapSpeedMillions / 1000.0f - (lapSpeedMillions / 1000).toInt()
        val minutesStr = String.format(Locale.getDefault(), "%02d", minutes)
        val secondStr = String.format(Locale.getDefault(), "%05.2f", (seconds + seconds2))
        return "$minutesStr:$secondStr"
    }

    @JvmStatic
    fun currentTime() {
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
        Log.e("ww", sdf.format(Date(currentTimeMillis)))
    }
}