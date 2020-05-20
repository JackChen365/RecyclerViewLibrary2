package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widget.recyclerview.adapter.WrapperAdapter
import com.cz.widget.recyclerview.adapter.support.expand.ExpandAdapter
import com.cz.widget.recyclerview.adapter.wrapper.header.HeaderWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.ExpandSampleAdapter
import kotlinx.android.synthetic.main.activity_adapter_expand_sample.*

@SampleSourceCode(".*Expand.*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/ExpandAdapter.md")
@RefRegister(title=R.string.expand_adapter,desc = R.string.expand_adapter_desc,category = R.string.adapter)
class ExpandSampleActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_expand_sample)
        recyclerView.layoutManager= LinearLayoutManager(this)

        val adapter = ExpandSampleAdapter(this, createExpandItems(3, 2), true)

        //If you want to add extra header or footer view.
        val headerWrapperAdapter = HeaderWrapperAdapter(adapter)
        headerWrapperAdapter.addHeaderView(getHeaderView(headerWrapperAdapter))
        recyclerView.adapter=headerWrapperAdapter
        buttonExpandAll.setOnClickListener {
            adapter.expandAll()
        }
        buttonCollapseAll.setOnClickListener {
            adapter.collapseAll()
        }
    }

    private fun createExpandItems(count: Int, childCount: Int): LinkedHashMap<String,List<String>> {
        val expandMap = LinkedHashMap<String,List<String>>()
        for (i in 0 until count) {
            val childItems = createList(i,childCount)
            expandMap.put("Group:$i", childItems)
        }
        return expandMap
    }

    private fun createList(i: Int,childCount: Int): MutableList<String> {
        val childItems = mutableListOf<String>()
        for (k in 0 until childCount) {
            childItems.add("Group:$i Child:$k")
        }
        return childItems
    }

    /**
     * Return a header view
     */
    private fun getHeaderView(wrapperAdapter:HeaderWrapperAdapter): View {
        val dataProvider = DataManager.getDataProvider(this)
        val colorArray = dataProvider.getColorArray(DataProvider.COLOR_PINK)
        val color = colorArray[DataProvider.RANDOM.nextInt(colorArray.size)]
        val header = LayoutInflater.from(this).inflate(R.layout.adapter_header_layout, recyclerView, false)
        val headerView = header as TextView
        headerView.setBackgroundColor(color)
        headerView.setTextColor(Color.WHITE)
        headerView.text = "HeaderView:" + wrapperAdapter.headerViewCount
        headerView.setOnClickListener { wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter)) }
        return headerView
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add) {
            recyclerView.scrollToPosition(0)
            val childItems = createList(0,2)
            val adapter=recyclerView.adapter
            if(adapter is WrapperAdapter){
                val expandAdapter = adapter.adapter as ExpandAdapter<String,String>
                expandAdapter.addGroup("New setCompareCondition",childItems,0,true)
            }
            return true
        } else if (id == R.id.action_remove) {
            val adapter=recyclerView.adapter
            if(adapter is WrapperAdapter) {
                val expandAdapter = adapter.adapter as ExpandAdapter<String,String>
                expandAdapter.removeGroup(1)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
