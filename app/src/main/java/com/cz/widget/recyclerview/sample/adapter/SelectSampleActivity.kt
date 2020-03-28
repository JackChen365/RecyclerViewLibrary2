package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.adapter.wrapper.select.SelectWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleSelectAdapter
import kotlinx.android.synthetic.main.activity_select_sample.*
import kotlin.random.Random

@SampleSourceCode
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/SelectWrapperAdapter.md")
@RefRegister(title=R.string.select_adapter,desc = R.string.select_adapter_desc,category = R.string.adapter)
class SelectSampleActivity : SampleAppCompatActivity() {

    private var colorList = mutableListOf(-0x1000000, -0xbbbbbc ,-0x777778, -0x333334, -0x1,-0x10000,-0xff0100,-0xffff01,-0x100,-0xff0001,-0xff01)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sample)
        val list= mutableListOf<String>()
        for(i in 0 .. 100){
            list.add("Data:$i")
        }
        val adapter = SimpleSelectAdapter(this, list)
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
        wrapperAdapter.setOnSingleSelectListener { v, newPosition, oldPosition ->
        }
        //The maximum number we could have.
//        wrapperAdapter.setSelectMaxCount(5)
        //wrapperAdapter.setMultiSelectItems(listOf(1,2,3))
        wrapperAdapter.setOnMultiSelectListener { v, selectPositions, lastSelectCount, maxCount ->
        }
        wrapperAdapter.setOnRectangleSelectListener { startPosition, endPosition ->
        }
        wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter))
        wrapperAdapter.addFooterView(getFooterView(wrapperAdapter))
    }

    /**
     * Return a header view
     */
    private fun getHeaderView(wrapperAdapter:SelectWrapperAdapter): View {
        val textColor = colorList[Random.nextInt(colorList.size)]
        val header = LayoutInflater.from(this).inflate(R.layout.adapter_header_layout,
            findViewById<ViewGroup>(android.R.id.content), false)
        val headerView = header as TextView
        headerView.setTextColor(textColor)
        headerView.text = "HeaderView:" + wrapperAdapter.headerViewCount
        headerView.setOnClickListener { wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter)) }
        return headerView
    }


    /**
     * Return a footer view.
     */
    private fun getFooterView(wrapperAdapter:SelectWrapperAdapter): View {
        val color = colorList[Random.nextInt(colorList.size)]
        val textColor = getDarkColor(color)
        val footer = LayoutInflater.from(this).inflate(R.layout.adapter_footer_layout,
            findViewById<ViewGroup>(android.R.id.content), false)
        val footerView = footer as TextView
        footerView.text = "FooterView:" + wrapperAdapter.footerViewCount
        footerView.setBackgroundColor(color)
        footerView.setTextColor(textColor)
        return footerView
    }

    private fun getDarkColor(color: Int): Int {
        val max = 0xFF
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.rgb(if (r + 30 > max) r - 30 else r + 30, if (g + 30 > max) g - 30 else g + 30, if (b + 30 > max) b - 30 else b + 30)
    }
}
