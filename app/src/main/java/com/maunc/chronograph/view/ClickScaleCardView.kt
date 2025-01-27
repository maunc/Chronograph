package com.maunc.chronograph.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener

class ClickScaleCardView(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    private var scaleOutAnim: Animator = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 1f, 0.95f),
        PropertyValuesHolder.ofFloat("scaleY", 1f, 0.95f)
    )

    private var scaleInAnim: Animator = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 0.95f, 1f, 1.1f, 1f),
        PropertyValuesHolder.ofFloat("scaleY", 0.95f, 1f, 1.1f, 1f),
    )

    private var onClickListener: OnClickScaleViewListener? = null
    private var rawX = 0
    private var rawY = 0

    init {
        scaleOutAnim.duration = 60
        scaleOutAnim.interpolator = LinearInterpolator()
        scaleInAnim.duration = 60
        scaleInAnim.interpolator = LinearInterpolator()
        scaleInAnim.addListener(onEnd = {
            if (isTouchPointInView(
                    this@ClickScaleCardView,
                    rawX,
                    rawY
                ) && onClickListener != null
            ) {
                onClickListener?.onClick()
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                post {
                    scaleOutAnim.start()
                }
            }

            MotionEvent.ACTION_UP -> {
                rawX = event.rawX.toInt()
                rawY = event.rawY.toInt()
                post {
                    scaleOutAnim.end()
                    scaleInAnim.start()
                }
            }

            MotionEvent.ACTION_CANCEL -> {

            }
        }
        return true
    }

    private fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.measuredWidth
        val bottom = top + view.measuredHeight
        return y in top..bottom && x in left..right
    }

    fun interface OnClickScaleViewListener {
        fun onClick()
    }

    fun setOnClickScaleViewListener(onClick: () -> Unit = {}) {
        onClickListener = OnClickScaleViewListener { onClick() }
    }
}