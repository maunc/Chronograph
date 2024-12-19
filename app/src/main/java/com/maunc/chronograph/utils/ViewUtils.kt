@file:JvmName(name = "ViewUtils")

package com.maunc.chronograph.utils

import android.view.View

@JvmName("setGone")
fun View.GONE() {
    this.visibility = View.GONE
}

@JvmName("setVisible")
fun View.VISIBLE() {
    this.visibility = View.VISIBLE
}