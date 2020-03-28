package com.cz.widget.recyclerview.sample.adapter

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.adapter.wrapper.drag.DefaultDragCallback
import com.cz.widget.recyclerview.adapter.wrapper.drag.DragWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.Channel
import com.cz.widget.recyclerview.sample.adapter.impl.ChannelAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_adapter_drag_sample.*
import java.io.InputStreamReader
import java.util.*
import kotlin.concurrent.thread

@SampleSourceCode(".*((Channel)|(DragNewsSampleActivity)).*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/DragAdapter.md")
@RefRegister(title=R.string.drag_adapter1,desc = R.string.drag_adapter_desc1,category = R.string.adapter)
class DragNewsSampleActivity : SampleAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_drag_sample)

        //load data from assets and initialize the adapter.
        thread {
            val text = getContentFromAssets(resources, "channel_item.json")
            val channelList = Gson().fromJson<ArrayList<Channel>>(text, object : TypeToken<ArrayList<Channel>>() {}.type)
            recyclerView.post { initChannelAdapter(channelList) }
        }
    }

    private fun initChannelAdapter(channelList:List<Channel>){
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ChannelAdapter(this, channelList)
        adapter.setHasStableIds(true)
        val dragAdapter = DragWrapperAdapter(adapter)
        recyclerView.adapter = dragAdapter

        //1. Add extras layout.
        val layoutInflater = LayoutInflater.from(this)
        val editLayout = layoutInflater.inflate(R.layout.adapter_channel_edit_layout, recyclerView,false)
        val editView = editLayout.findViewById<TextView>(R.id.tv_edit)
        var editChannel=false
        editView.setOnClickListener {
            editChannel = !editChannel
            adapter.setEditEnable(editChannel)
            editView.setText(if (editChannel) R.string.complete else R.string.channel_sort_delete)
        }
        dragAdapter.addAdapterView(editLayout, 0)
        val channelHeader = layoutInflater.inflate(R.layout.adapter_channel_header, recyclerView,false)
        dragAdapter.addAdapterView(channelHeader, channelList.count { it.use } + 1)

        //2. Delegate the callback
        dragAdapter.setFixedViewDragEnabled(false)
        dragAdapter.setDragDelegate(object : DefaultDragCallback() {
            override fun isDragEnable(position: Int): Boolean {
                val item=adapter.getItem(position)
                return item.use
            }
            override fun getDragFlag(): Int {
                return ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            }
        })
        //3.  When clicking the channel. We located this item to a new position
        dragAdapter.setOnItemClickListener { _, position, adapterPosition ->
            val count = channelList.count { it.use }
            val item = adapter.getItem(position)
            if (adapterPosition <= count) {
                if (editChannel) {
                    item.use = false
                    dragAdapter.move(adapterPosition, count + 1)
                    dragAdapter.notifyItemChanged(count+1)
                } else {
                    Toast.makeText(applicationContext, "Click:" + item.name, Toast.LENGTH_SHORT).show()
                }
            } else {
                item.use = true
                dragAdapter.move(adapterPosition, count+1)
                dragAdapter.notifyItemChanged(count+1)
            }
        }

    }

    private fun getContentFromAssets(resource: Resources, fileName: String): String {
        var result = StringBuilder()
        var inputStream: InputStreamReader? = null
        try {
            InputStreamReader(resource.assets.open(fileName)).apply { forEachLine { result.append(it) } }
        } finally {
            inputStream?.close()
        }
        return result.toString()
    }
}
