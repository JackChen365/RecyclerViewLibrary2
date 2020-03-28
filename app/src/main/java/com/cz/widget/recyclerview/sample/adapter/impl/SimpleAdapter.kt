package com.cz.widget.recyclerview.sample.adapter.impl

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.recyclerview.adapter.BaseAdapter
import com.cz.widget.recyclerview.sample.R

/**
 * Created by cz on 16/1/23.
 */
class SimpleAdapter<E>(context: Context, @param:LayoutRes private val layout: Int, items: List<E>) : BaseAdapter<RecyclerView.ViewHolder,E>(items) {

    companion object {

        fun createFromResource(context: Context, @ArrayRes res: Int): SimpleAdapter<*> {
            return SimpleAdapter(context, context.resources.getStringArray(res))
        }
    }
    val inflater: LayoutInflater = LayoutInflater.from(context)


    constructor(context: Context, items: Array<E>) : this(context, R.layout.adapter_simple_text_item, listOf(*items))

    constructor(context: Context, @LayoutRes layout: Int, items: Array<E>) : this(context, layout, listOf(*items))

    constructor(context: Context, items: List<E>) : this(context, R.layout.adapter_simple_text_item, items)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object :RecyclerView.ViewHolder(inflater.inflate(layout,parent, false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val textView = holder.itemView as TextView
        val item = getItem(position)
        if (null != item) {
            textView.text = item.toString()
        }
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context,"点击:$position",Toast.LENGTH_SHORT).show()
        }
    }
}
