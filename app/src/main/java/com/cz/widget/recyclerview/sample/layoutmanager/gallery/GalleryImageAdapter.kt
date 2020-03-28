package com.cz.widget.recyclerview.sample.layoutmanager.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.recyclerview.sample.R

/**
 * @author :Created by cz
 * @date 2020-03-22 18:32
 * @email bingo110@126.com
 */
class GalleryImageAdapter(context: Context, imageItems: List<Int>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val imageList= mutableListOf<Int>()

    init {
        if (null != imageItems) {
            this.imageList.addAll(imageItems)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object :RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.layout_gallery_image_item, parent, false)){}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageView = holder.itemView.findViewById(R.id.imageView) as ImageView
        imageView.setImageResource(this.imageList[position])
    }

    override fun getItemCount(): Int {
        return this.imageList.size
    }
}
