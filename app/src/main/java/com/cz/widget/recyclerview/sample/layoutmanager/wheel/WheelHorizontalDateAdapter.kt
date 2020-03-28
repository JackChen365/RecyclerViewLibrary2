package com.cz.widget.recyclerview.sample.layoutmanager.wheel

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.recyclerview.sample.R
import java.text.DecimalFormat


/**
 * Created by cz on 1/18/17.
 */
class WheelHorizontalDateAdapter(context: Context, numberFormatValue: String, private val items: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val layoutInflater=LayoutInflater.from(context)
    private var formatter: DecimalFormat = DecimalFormat(numberFormatValue)

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position:Int):Int{
        return items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object :RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.adapter_date_horizontal_item,parent,false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        textView.text = formatter.format(items[position])
    }
}
