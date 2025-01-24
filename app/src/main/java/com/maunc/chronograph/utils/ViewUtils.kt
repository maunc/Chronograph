@file:JvmName(name = "ViewUtils")

package com.maunc.chronograph.utils

import android.view.View

@JvmName("setGone")
fun View.gone() {
    this.visibility = View.GONE
}

@JvmName("setVisible")
fun View.visible() {
    this.visibility = View.VISIBLE
}