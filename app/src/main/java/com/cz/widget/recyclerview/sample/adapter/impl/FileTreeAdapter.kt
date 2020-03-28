package com.cz.widget.recyclerview.sample.adapter.impl

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cz.widget.recyclerview.adapter.support.tree.TreeAdapter
import com.cz.widget.recyclerview.adapter.support.tree.TreeNode
import com.cz.widget.recyclerview.sample.R


import java.io.File

/**
 * Created by cz on 16/1/23.
 */
class FileTreeAdapter(context: Context, rootNode: TreeNode<File>) : TreeAdapter<File>(rootNode) {
    companion object {
        private const val FOLDER_ITEM = 0
        private const val FILE_ITEM = 1
    }
    private val layoutInflater=LayoutInflater.from(context)
    private val padding: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, node: TreeNode<File>, file: File, viewType: Int, position: Int) {
        val itemView = holder.itemView
        itemView.setPadding(padding * node.depth, itemView.paddingTop, itemView.paddingRight, itemView.paddingBottom)
        if(holder is FileViewHolder){
            holder.bind(node)
        }
        if(holder is FolderViewHolder){
            holder.bind(node)
        }
    }

    override fun onNodeExpand(node: TreeNode<File>,file:File, holder: RecyclerView.ViewHolder, expand: Boolean) {
        super.onNodeExpand(node,file, holder, expand)
        holder.itemView.findViewById<View>(R.id.iv_flag).isSelected = expand
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FOLDER_ITEM -> FolderViewHolder(layoutInflater.inflate(R.layout.adapter_tree_folder_item,parent,false))
            else -> FileViewHolder(layoutInflater.inflate(R.layout.adapter_tree_file_item,parent,false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val file = getItem(position)
        var viewType = FILE_ITEM
        if (null!=file&&file.isDirectory) {
            viewType = FOLDER_ITEM
        }
        return viewType
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(node: TreeNode<File>){
            val file=node.item
            itemView.findViewById<TextView>(R.id.tv_simple_name).text = file.name.substring(0,1)
            itemView.findViewById<TextView>(R.id.tv_name).text = file.name
        }
    }

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(node: TreeNode<File>){
            val file=node.item
            itemView.findViewById<TextView>(R.id.tv_simple_name).text = file.name.substring(0,1)
            val fileName = itemView.findViewById<TextView>(R.id.tv_name)
            if(node.isLoad){
                fileName.text = file.name + "(" + node.children.size + ")"
            } else {
                fileName.text = file.name
            }
            itemView.findViewById<ImageView>(R.id.iv_flag).isSelected = node.isExpand
        }
    }

}
