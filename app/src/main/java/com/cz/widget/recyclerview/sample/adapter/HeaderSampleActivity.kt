package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.recyclerview.adapter.wrapper.header.HeaderWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleAdapter
import kotlinx.android.synthetic.main.activity_adapter_header_sample.*
import kotlin.random.Random

@SampleSourceCode
@RefRegister(title=R.string.header_adapter,desc = R.string.header_adapter_desc,category = R.string.adapter)
class HeaderSampleActivity : SampleAppCompatActivity() {
    private var colorList = mutableListOf(-0x1000000, -0xbbbbbc ,-0x777778, -0x333334, -0x1,-0x10000,-0xff0100,-0xffff01,-0x100,-0xff0001,-0xff01)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_header_sample)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.addDuration=300
        val list= mutableListOf<String>()
        for(i in 0 until 3){
            list.add("Data:$i")
        }
        val adapter = SimpleAdapter(this, list)
        val wrapperAdapter=HeaderWrapperAdapter(adapter)
        wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter))
        wrapperAdapter.addFooterView(getFooterView(wrapperAdapter))

        recyclerView.adapter=wrapperAdapter

        buttonAddHeader.setOnClickListener {  wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter)) }
        buttonRemoveHeader.setOnClickListener {
            if(0==wrapperAdapter.headerViewCount){
                Toast.makeText(applicationContext,"当前没有更多列表头!",Toast.LENGTH_SHORT).show()
            } else {
                wrapperAdapter.removeHeaderView(0)
            }
        }
        buttonAddFooter.setOnClickListener { wrapperAdapter.addFooterView(getFooterView(wrapperAdapter)) }
        buttonRemoveFooter.setOnClickListener {
            if(0==wrapperAdapter.footerViewCount){
                Toast.makeText(applicationContext,"当前没有更多列表尾!",Toast.LENGTH_SHORT).show()
            } else {
                wrapperAdapter.removeFooterView(0)
            }
        }
        buttonAddItem.setOnClickListener {  adapter.add("Data:${adapter.itemCount}") }
    }

    /**
     * Return a header view
     */
    private fun getHeaderView(wrapperAdapter:HeaderWrapperAdapter): View {
        val textColor = colorList[Random.nextInt(colorList.size)]
        val header = LayoutInflater.from(this).inflate(R.layout.adapter_header_layout, recyclerView, false)
        val headerView = header as TextView
        headerView.setTextColor(textColor)
        headerView.text = "HeaderView:" + wrapperAdapter.headerViewCount
        headerView.setOnClickListener { wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter)) }
        return headerView
    }


    /**
     * Return a footer view.
     */
    private fun getFooterView(wrapperAdapter: HeaderWrapperAdapter): View {
        val color = colorList[Random.nextInt(colorList.size)]
        val textColor = getDarkColor(color)
        val footer = LayoutInflater.from(this).inflate(R.layout.adapter_footer_layout, recyclerView, false)
        val footerView = footer as TextView
        footerView.text = "FooterView:" + wrapperAdapter.footerViewCount
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
