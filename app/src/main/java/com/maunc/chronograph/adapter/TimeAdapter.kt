package com.maunc.chronograph.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.maunc.chronograph.R
import com.maunc.chronograph.bean.TimeData

class TimeAdapter : BaseQuickAdapter<TimeData, BaseViewHolder>(R.layout.item_time) {
    override fun convert(holder: BaseViewHolder, item: TimeData) {

    }
}