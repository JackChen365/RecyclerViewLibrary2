package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widget.recyclerview.adapter.wrapper.dynamic.DynamicWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleAdapter
import kotlinx.android.synthetic.main.activity_adapter_dynamic_sample.*
import java.util.*

@SampleSourceCode(".*Dynamic.*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/DynamicAdapter.md")
@RefRegister(title=R.string.dynamic_adapter,desc = R.string.dynamic_adapter_desc,category = R.string.adapter)
class DynamicSampleActivity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_dynamic_sample)
        val random = Random()
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val simpleAdapter= SimpleAdapter(this, createList(20))
        val dynamicAdapter = DynamicWrapperAdapter(simpleAdapter)
        recyclerView.adapter = dynamicAdapter

        buttonAdd.setOnClickListener {
            val itemCount = simpleAdapter.itemCount
            simpleAdapter.add("new:$itemCount", if(0==itemCount) 0 else random.nextInt(itemCount))
        }
        buttonRemove.setOnClickListener {
            if (0 != simpleAdapter.itemCount) {
                simpleAdapter.remove(0, Math.min(8,simpleAdapter.itemCount))
            } else {
                AlertDialog.Builder(this).
                    setTitle(R.string.add_random_item).
                    setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }.
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        simpleAdapter.addAll(createList(30))
                    }.show()
            }
        }
        buttonRandomAdd.setOnClickListener {
            if(0 < dynamicAdapter.itemCount){
                val itemView = getFullItemView(dynamicAdapter)
                val index = random.nextInt(dynamicAdapter.itemCount)
                dynamicAdapter.addAdapterView(itemView,index)
            }
        }
        buttonRandomRemove.setOnClickListener {
            val extraViewCount = dynamicAdapter.extraViewCount
            if(0==extraViewCount){
                Toast.makeText(applicationContext,R.string.no_any_dynamic_item,Toast.LENGTH_SHORT).show()
            } else {
                dynamicAdapter.removeAdapterView(if(0==extraViewCount) 0 else random.nextInt(extraViewCount))
            }
        }

        //Add a bunch of layout.
        AlertDialog.Builder(this).
            setTitle(R.string.add_dynamic_item).
            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }.
            setPositiveButton(android.R.string.ok) { _, _ ->
                for(i in 0 until 10){
                    val itemView = getFullItemView(dynamicAdapter)
                    val index = random.nextInt(dynamicAdapter.itemCount)
                    dynamicAdapter.addAdapterView(itemView,index)
                }
            }.show()
    }


    private fun createList(childCount: Int): MutableList<String> {
        val childItems = mutableListOf<String>()
        for (k in 0 until childCount) {
            childItems.add("Item:$k")
        }
        return childItems
    }


    /**
     * Return a full column layout
     */
    private fun getFullItemView(wrapperAdapter: DynamicWrapperAdapter): View {
        val dataProvider = DataManager.getDataProvider(this)
        val colorArray = dataProvider.getColorArray(DataProvider.COLOR_PINK)
        val color = colorArray[DataProvider.RANDOM.nextInt(colorArray.size)]
        val header = LayoutInflater.from(this).inflate(R.layout.adapter_header_layout,recyclerView, false)
        val headerView = header as TextView
        header.setBackgroundColor(color)
        headerView.setTextColor(Color.WHITE)
        headerView.text = "DynamicView:" + wrapperAdapter.extraViewCount
        return headerView
    }
}
