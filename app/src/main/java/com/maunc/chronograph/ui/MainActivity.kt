package com.maunc.chronograph.ui

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maunc.chronograph.R
import com.maunc.chronograph.adapter.TimeAdapter
import com.maunc.chronograph.data.TimeData
import com.maunc.chronograph.global.GlobalVar.DEF_TIME_TEXT
import com.maunc.chronograph.global.GlobalVar.DELAY_MILLS
import com.maunc.chronograph.global.GlobalVar.SPEED_NUM
import com.maunc.chronograph.utils.TimeTvUtils.formatTime
import com.maunc.chronograph.utils.gone
import com.maunc.chronograph.utils.visible
import com.maunc.chronograph.view.ClickScaleCardView
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity() {

    private val mainScope by lazy {
        MainScope()
    }

    private companion object {
        private class TimeHandler(looper: Looper) : Handler(looper)
    }

    private var timeRecyclerView: RecyclerView? = null
    private var playTimeBtu: ClickScaleCardView? = null
    private var controllerLayout: ConstraintLayout? = null
    private var controllerTimeBtu: ClickScaleCardView? = null
    private var stopTimeBtu: ClickScaleCardView? = null
    private var controllerImageView: AppCompatImageView? = null
    private var stopImageView: AppCompatImageView? = null
    private var textTimeTv: TextView? = null

    private var mHandler: TimeHandler? = null
    private var mTimeThread: HandlerThread? = null

    private var mTimeValue = 0f
    private var isStart = false
    private var rankDiffValue = 0f
    private var rankIndex = 0

    private val timeAdapter by lazy {
        TimeAdapter()
    }

    private val timeRuntime = object : Runnable {
        override fun run() {
            mHandler?.postDelayed(this, DELAY_MILLS)
            calculateTime()
        }
    }

    private fun calculateTime() {
        mTimeValue.plus(SPEED_NUM).let {
            mTimeValue = it
            runOnUiThread {
                textTimeTv?.text = formatTime(it)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initHandler()
        initView()
        initViewEvent()
    }

    private fun initHandler() {
        if (mTimeThread == null || mTimeThread!!.state == Thread.State.TERMINATED) {
            mTimeThread = HandlerThread("timeThread").also {
                it.start()
            }
        }
        mHandler?.removeCallbacksAndMessages(null) ?: kotlin.run {
            mTimeThread?.let {
                mHandler = TimeHandler(it.looper)
            }
        }
    }

    private fun initView() {
        playTimeBtu = findViewById(R.id.play_time)
        timeRecyclerView = findViewById(R.id.timing_rec)
        controllerLayout = findViewById(R.id.controller_layout)
        controllerTimeBtu = findViewById(R.id.controller_time)
        stopTimeBtu = findViewById(R.id.stop_time)
        controllerImageView = findViewById(R.id.controller_time_image)
        stopImageView = findViewById(R.id.stop_time_image)
        textTimeTv = findViewById(R.id.text_time)
        textTimeTv?.typeface = Typeface.createFromAsset(assets, "fonts/regular.otf")
        textTimeTv?.text = DEF_TIME_TEXT
        timeRecyclerView?.apply {
            adapter = timeAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    private fun initViewEvent() {
        playTimeBtu?.setOnClickScaleViewListener {
            mHandler?.post(timeRuntime)
            isStart = true
            playTimeBtu?.gone()
            controllerLayout?.visible()
            controllerImageView?.setImageResource(R.drawable.play_image)
        }

        controllerTimeBtu?.setOnClickScaleViewListener {
            if (isStart) {
                mHandler?.removeCallbacksAndMessages(null)
                isStart = false
                controllerImageView?.setImageResource(R.drawable.pause_image)
                stopImageView?.setImageResource(R.drawable.stop_image)
            } else {
                mHandler?.post(timeRuntime)
                isStart = true
                controllerImageView?.setImageResource(R.drawable.play_image)
                stopImageView?.setImageResource(R.drawable.mark_image)
            }
        }

        stopTimeBtu?.setOnClickScaleViewListener {
            if (isStart) {
                rankDiffValue = mTimeValue - rankDiffValue
                val timeData = TimeData(
                    ++rankIndex,
                    formatTime(mTimeValue),
                    formatTime(rankDiffValue)
                )
                timeAdapter.addTime(timeData)
            } else {
                isStart = false
                playTimeBtu?.visible()
                controllerLayout?.gone()
                mTimeValue = 0f
                rankDiffValue = 0f
                rankIndex = 0
                textTimeTv?.text = DEF_TIME_TEXT
                timeAdapter.clearTime()
                stopImageView?.setImageResource(R.drawable.mark_image)
            }
        }
    }

    override fun onDestroy() {
        mHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}