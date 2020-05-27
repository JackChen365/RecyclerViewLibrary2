package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widget.recyclerview.adapter.wrapper.header.HeaderWrapperAdapter
import com.cz.widget.recyclerview.adapter.wrapper.select.SelectWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleSelectAdapter
import kotlinx.android.synthetic.main.activity_select_sample.*

@SampleSourceCode
@SampleDocument("SelectWrapperAdapter.md")
@RefRegister(title=R.string.select_adapter,desc = R.string.select_adapter_desc,category = R.string.adapter)
class SelectSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sample)
        val dataProvider = DataManager.getDataProvider(this)
        val adapter = SimpleSelectAdapter(this, dataProvider.getWordList(100))
        val wrapperAdapter= SelectWrapperAdapter(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter=wrapperAdapter
        radioLayout.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                0->wrapperAdapter.setSelectMode(SelectWrapperAdapter.CLICK)
                1->wrapperAdapter.setSelectMode(SelectWrapperAdapter.SINGLE_SELECT)
                2->wrapperAdapter.setSelectMode(SelectWrapperAdapter.MULTI_SELECT)
                3->wrapperAdapter.setSelectMode(SelectWrapperAdapter.RECTANGLE_SELECT)
            }
        }
        //wrapperAdapter.setSingleSelectPosition(0)
        wrapperAdapter.setOnSingleSelectListener { _, _, _ ->
        }
        //The maximum number we could have.
//        wrapperAdapter.setSelectMaxCount(5)
        //wrapperAdapter.setMultiSelectItems(listOf(1,2,3))
        wrapperAdapter.setOnMultiSelectListener { _, _, _, _ ->
        }
        wrapperAdapter.setOnRectangleSelectListener { _, _ ->
        }
        wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter))
        wrapperAdapter.addFooterView(getFooterView(wrapperAdapter))
    }

    /**
     * Return a header view
     */
    /**
     * Return a header view
     */
    private fun getHeaderView(wrapperAdapter: HeaderWrapperAdapter): View {
        val dataProvider = DataManager.getDataProvider(this)
        val colorArray = dataProvider.getColorArray(DataProvider.COLOR_RED)
        val color = colorArray[DataProvider.RANDOM.nextInt(colorArray.size)]
        val header = LayoutInflater.from(this).inflate(R.layout.adapter_header_layout, recyclerView, false)
        val headerView = header as TextView
        headerView.setBackgroundColor(color)
        headerView.setTextColor(Color.WHITE)
        headerView.text = "HeaderView:" + wrapperAdapter.headerViewCount
        headerView.setOnClickListener { wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter)) }
        return headerView
    }


    /**
     * Return a footer view.
     */
    private fun getFooterView(wrapperAdapter: HeaderWrapperAdapter): View {
        val dataProvider = DataManager.getDataProvider(this)
        val colorArray = dataProvider.getColorArray(DataProvider.COLOR_ORANGE)
        val color = colorArray[DataProvider.RANDOM.nextInt(colorArray.size)]
        val footer = LayoutInflater.from(this).inflate(R.layout.adapter_footer_layout, recyclerView, false)
        footer.setBackgroundColor(color)
        val footerView = footer as TextView
        footerView.text = "FooterView:" + wrapperAdapter.footerViewCount
        footerView.setTextColor(Color.WHITE)
        return footerView
    }
}
