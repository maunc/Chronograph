@file:JvmName(name = "ResUtils")

package com.maunc.chronograph.utils

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes

fun Application.getStringRes(@StringRes stringId: Int): String {
    return getString(stringId)
}

fun Context.getStringRes(@StringRes stringId: Int): String {
    return getString(stringId)
}