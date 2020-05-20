package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widget.recyclerview.adapter.wrapper.header.HeaderWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleAdapter
import kotlinx.android.synthetic.main.activity_adapter_header_sample.*

@SampleSourceCode(".*Header.*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/HeaderWrapperAdapter.md")
@RefRegister(title=R.string.header_adapter,desc = R.string.header_adapter_desc,category = R.string.adapter)
class HeaderSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_header_sample)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.addDuration=300
        val dataProvider = DataManager.getDataProvider(this)
        val adapter = SimpleAdapter(this, dataProvider.getWordList(8))
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
    /**
     * Return a header view
     */
    private fun getHeaderView(wrapperAdapter:HeaderWrapperAdapter): View {
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
