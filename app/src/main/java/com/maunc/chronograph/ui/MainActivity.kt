package com.maunc.chronograph.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maunc.chronograph.R
import com.maunc.chronograph.adapter.TimeAdapter
import com.maunc.chronograph.utils.GONE
import com.maunc.chronograph.utils.VISIBLE
import kotlinx.coroutines.MainScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val mainScope by lazy {
        MainScope()
    }

    private companion object {
        private class TimeHandler(looper: Looper) : Handler(looper)
    }

    private var timeRecyclerView: RecyclerView? = null
    private var playTimeBtu: CardView? = null
    private var controllerLayout: ConstraintLayout? = null
    private var controllerTimeBtu: CardView? = null
    private var stopTimeBtu: CardView? = null
    private var controllerImageView: AppCompatImageView? = null
    private var stopImageView: AppCompatImageView? = null
    private var textTimeTv: TextView? = null
    private var mSpeed2: TextView? = null
    private var mSpeed1to5: TextView? = null
    private var mSpeed1: TextView? = null
    private var mSpeed0to75: TextView? = null
    private var mSpeed0to5: TextView? = null

    private var mHandler: TimeHandler? = null
    private var mTimeThread: HandlerThread? = null

    private val mDelayMills = 10L
    private var mTimeValue = 0f
    private var speedNum = 0.0117f
    private var isStart = false
    private val defTimeText = "00:00.00"

    private val timeRuntime = object : Runnable {
        override fun run() {
            mHandler?.postDelayed(this, mDelayMills)
            calculateTime()
        }
    }

    private fun calculateTime() {
        mTimeValue.plus(speedNum).let {
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
        textTimeTv?.text = defTimeText

        mSpeed2 = findViewById(R.id.speed_2)
        mSpeed1to5 = findViewById(R.id.speed_1_5)
        mSpeed1 = findViewById(R.id.speed_1)
        mSpeed0to75 = findViewById(R.id.speed_0_75)
        mSpeed0to5 = findViewById(R.id.speed_0_5)

        mSpeed2?.setOnClickListener { switchSpeed(2f) }
        mSpeed1to5?.setOnClickListener { switchSpeed(1.5f) }
        mSpeed1?.setOnClickListener { switchSpeed(1f) }
        mSpeed0to75?.setOnClickListener { switchSpeed(0.75f) }
        mSpeed0to5?.setOnClickListener { switchSpeed(0.5f) }

        refreshSpeedUI()

        playTimeBtu?.setOnClickListener {
            mHandler?.post(timeRuntime)
            isStart = true
            playTimeBtu?.GONE()
            controllerLayout?.VISIBLE()
            controllerImageView?.setImageResource(R.drawable.play_image)
        }

        controllerTimeBtu?.setOnClickListener {
            if (isStart) {
                mHandler?.removeCallbacksAndMessages(null)
                isStart = false
                controllerImageView?.setImageResource(R.drawable.pause_image)
            } else {
                mHandler?.post(timeRuntime)
                isStart = true
                controllerImageView?.setImageResource(R.drawable.play_image)
            }
        }

        stopTimeBtu?.setOnClickListener {
            playTimeBtu?.VISIBLE()
            controllerLayout?.GONE()
            mTimeValue = 0f
            textTimeTv?.text = defTimeText
        }

        timeRecyclerView?.apply {
            adapter = TimeAdapter()
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    private fun switchSpeed(index: Float) {
        when (index) {
            2f -> speedNum = 0.0234f
            1.5f -> speedNum = 0.01755f
            1f -> speedNum = 0.0117f
            0.75f -> speedNum = 0.008775f
            0.5f -> speedNum = 0.00585f
        }
        refreshSpeedUI()
    }

    private fun refreshSpeedUI() {
        when (speedNum) {
            0.0234f -> {
                mSpeed2?.setTextColor(Color.parseColor("#D5562E"))
                mSpeed1to5?.setTextColor(Color.parseColor("#000000"))
                mSpeed1?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to75?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to5?.setTextColor(Color.parseColor("#000000"))
            }

            0.01755f -> {
                mSpeed2?.setTextColor(Color.parseColor("#000000"))
                mSpeed1to5?.setTextColor(Color.parseColor("#D5562E"))
                mSpeed1?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to75?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to5?.setTextColor(Color.parseColor("#000000"))
            }

            0.0117f -> {
                mSpeed2?.setTextColor(Color.parseColor("#000000"))
                mSpeed1to5?.setTextColor(Color.parseColor("#000000"))
                mSpeed1?.setTextColor(Color.parseColor("#D5562E"))
                mSpeed0to75?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to5?.setTextColor(Color.parseColor("#000000"))
            }

            0.008775f -> {
                mSpeed2?.setTextColor(Color.parseColor("#000000"))
                mSpeed1to5?.setTextColor(Color.parseColor("#000000"))
                mSpeed1?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to75?.setTextColor(Color.parseColor("#D5562E"))
                mSpeed0to5?.setTextColor(Color.parseColor("#000000"))
            }

            0.00585f -> {
                mSpeed2?.setTextColor(Color.parseColor("#000000"))
                mSpeed1to5?.setTextColor(Color.parseColor("#000000"))
                mSpeed1?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to75?.setTextColor(Color.parseColor("#000000"))
                mSpeed0to5?.setTextColor(Color.parseColor("#D5562E"))
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(time: Float): String {
        val minutes = (time / 60).toInt() // 转换为分钟
        val remainingSeconds = time % 60 // 计算剩余的秒数
        return String.format("%02d", minutes) + ":" + String.format("%05.2f", remainingSeconds)
    }

    private fun timeUnitMillion(lapSpeedMillions: Long): String { // int * 1000
        val minutes = TimeUnit.MILLISECONDS.toMinutes(lapSpeedMillions)
        val seconds =
            (TimeUnit.MILLISECONDS.toSeconds(lapSpeedMillions) - TimeUnit.MINUTES.toSeconds(minutes))
        val seconds2 = lapSpeedMillions / 1000.0f - (lapSpeedMillions / 1000).toInt()
        val minutesStr = String.format(Locale.getDefault(), "%02d", minutes)
        val secondStr = String.format(Locale.getDefault(), "%05.2f", (seconds + seconds2))
        return "$minutesStr:$secondStr"
    }

    @SuppressLint("SimpleDateFormat")
    fun currentTime() {
        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
        Log.e("ww", sdf.format(Date(currentTimeMillis)))
    }
}