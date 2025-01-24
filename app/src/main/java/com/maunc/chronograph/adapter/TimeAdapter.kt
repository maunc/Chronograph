package com.maunc.chronograph.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.chronograph.R
import com.maunc.chronograph.bean.TimeData

@SuppressLint("NotifyDataSetChanged")
class TimeAdapter : BaseQuickAdapter<TimeData, BaseViewHolder>(R.layout.item_time) {

    override fun convert(holder: BaseViewHolder, item: TimeData) {
        holder.setText(R.id.item_time_index_tv, "${item.index}")
        holder.setText(R.id.item_gap_time_tv, "+ " + item.gapTime)
        holder.setText(R.id.item_time_tv, item.time)
    }

    fun addTime(timeData: TimeData) {
        data.add(0, timeData)
        notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    fun clearTime() {
        data.clear()
        notifyDataSetChanged()
    }
}