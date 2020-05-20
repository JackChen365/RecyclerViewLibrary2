package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widget.recyclerview.adapter.wrapper.drag.DragWrapperAdapter
import com.cz.widget.recyclerview.adapter.wrapper.dynamic.DynamicWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleAdapter
import kotlinx.android.synthetic.main.activity_adapter_drag_sample.*

@SampleSourceCode(".*DragGridSampleActivity.*")
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/DragWrapperAdapter.md")
@RefRegister(title=R.string.drag_adapter,desc = R.string.drag_adapter_desc,category = R.string.adapter)
class DragGridSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_drag_sample)
        recyclerView.layoutManager = GridLayoutManager(this,3)
        recyclerView.itemAnimator?.changeDuration=300

        val dataProvider = DataManager.getDataProvider(this)
        val adapter = SimpleAdapter(this, dataProvider.getWordList(100))
        val dragAdapter =
            DragWrapperAdapter(adapter)
        dragAdapter.addAdapterView(getFullItemView(dragAdapter),2)
        dragAdapter.addAdapterView(getFullItemView(dragAdapter),8)
        dragAdapter.addAdapterView(getFullItemView(dragAdapter),14)
        recyclerView.adapter = dragAdapter
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
