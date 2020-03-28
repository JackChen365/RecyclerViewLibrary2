package com.cz.widget.recyclerview.sample.adapter.impl

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.recyclerview.adapter.FilterAdapter
import com.cz.widget.recyclerview.sample.R

/**
 * @author :Created by cz
 * @date 2020-03-19 22:15
 * @email bingo110@126.com
 *
 * An filter adapter example
 * @see com.cz.widget.recyclerview.sample.adapter.FilterSampleActivity
 */
class SimpleFilterAdapter<E>(context: Context, @param:LayoutRes private val layout: Int, items: List<E>) : FilterAdapter<RecyclerView.ViewHolder,E>(items) {

    companion object {
        fun createFromResource(context: Context, @ArrayRes res: Int): SimpleFilterAdapter<*> {
            return SimpleFilterAdapter(context, context.resources.getStringArray(res))
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
        val itemValue=item.toString()
        if (null != item) {
            textView.text = item.toString()
        }
        if (null==queryWord) {
            textView.text = itemValue
        } else {
            val spannable = SpannableString(itemValue)
            val index = itemValue.indexOf(queryWord)
            spannable.setSpan(ForegroundColorSpan(Color.RED),index, index + queryWord.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.setText(spannable, TextView.BufferType.SPANNABLE)
        }
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context,"点击:$position",Toast.LENGTH_SHORT).show()
        }
    }

    override fun filterObject(item: E, word: CharSequence): Boolean {
        return item.toString().contains(word)
    }
}
