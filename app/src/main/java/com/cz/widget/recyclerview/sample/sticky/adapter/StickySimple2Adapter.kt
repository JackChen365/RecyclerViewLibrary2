package com.cz.widget.recyclerview.sample.sticky.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.recyclerview.adapter.wrapper.sticky.StickyAdapter
import com.cz.widget.recyclerview.sample.R

/**
 * Created by cz on 16/1/23.
 */
class StickySimple2Adapter<E>(context: Context, @param:LayoutRes private val layout: Int, itemList: List<E>) : StickyAdapter<RecyclerView.ViewHolder, E>(itemList) {
    companion object{
        private const val STICKY_HEADER_ITEM1=0x00
        private const val STICKY_HEADER_ITEM2=0x01
    }
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    constructor(context: Context, items: List<E>) : this(context, R.layout.sticky_adapter_simple_text2_item, items)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object :RecyclerView.ViewHolder(layoutInflater.inflate(layout,parent, false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val textView = holder.itemView.findViewById<TextView>(android.R.id.text1)
        val item = getItem(position)
        if (null != item) {
            textView.text = item.toString()
        }
    }

    override fun getStickyViewType(position: Int): Int {
        return if(0==position%4) STICKY_HEADER_ITEM1 else STICKY_HEADER_ITEM2
    }
    override fun onCreateStickyView(parent: ViewGroup, viewType: Int): View {
        if(STICKY_HEADER_ITEM1==viewType){
            return layoutInflater.inflate(R.layout.sticky_header_layout1, parent, false)
        } else {
            return layoutInflater.inflate(R.layout.sticky_header_layout2, parent, false)
        }
    }

    override fun onBindStickyView(view: View,viewType:Int, position: Int) {
        if(STICKY_HEADER_ITEM1==viewType){
            val stickyHeaderView=view.findViewById<TextView>(R.id.stickyHeaderView)
            val button = view.findViewById<View>(R.id.button)
            val groupIndex = groupingStrategy.getGroupIndex(position)
            stickyHeaderView.text = "Header Type1:$groupIndex"
            stickyHeaderView.setOnClickListener {
                Toast.makeText(view.context,view.context.getString(R.string.click_position,position),Toast.LENGTH_SHORT).show()
            }
            button.setOnClickListener {
                Toast.makeText(view.context,view.context.getString(R.string.click_button_position,position),Toast.LENGTH_SHORT).show()
            }
        } else if(STICKY_HEADER_ITEM2==viewType){
            val stickyHeaderView=view.findViewById<TextView>(R.id.stickyHeaderView)
            val groupIndex = groupingStrategy.getGroupIndex(position)
            stickyHeaderView.text = "Header Type2:$groupIndex"
            stickyHeaderView.setOnClickListener {
                Toast.makeText(view.context,view.context.getString(R.string.click_position,position),Toast.LENGTH_SHORT).show()
            }
        }
    }


}
