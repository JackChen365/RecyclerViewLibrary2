package com.cz.widget.recyclerview.sample.adapter.impl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.recyclerview.adapter.BaseAdapter
import com.cz.widget.recyclerview.sample.R

/**
 * @author :Created by cz
 * @date 2020-03-19 20:24
 * @email bingo110@126.com
 * This is an drag channel adapter.
 * @see com.cz.widget.recyclerview.sample.adapter.DragNewsSampleActivity
 */
class ChannelAdapter(context: Context, items: List<Channel>) : BaseAdapter<RecyclerView.ViewHolder,Channel>(items) {
    private val layoutInflater=LayoutInflater.from(context)
    private var editEnable: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object :RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.adapter_channel_layout,parent,false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        holder.itemView.findViewById<TextView>(R.id.tv_name).text = item.name
        holder.itemView.findViewById<View>(R.id.iv_flag).visibility = if (item.use) View.GONE else View.VISIBLE
        holder.itemView.findViewById<View>(R.id.iv_delete_icon).visibility = if (editEnable && item.use) View.VISIBLE else View.GONE
    }

    fun setEditEnable(enable: Boolean) {
        this.editEnable = enable
        notifyDataSetChanged()
    }

}
