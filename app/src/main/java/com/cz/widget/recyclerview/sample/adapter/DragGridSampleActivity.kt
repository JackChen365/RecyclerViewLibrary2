package com.cz.widget.recyclerview.sample.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.adapter.wrapper.drag.DragWrapperAdapter
import com.cz.widget.recyclerview.adapter.wrapper.dynamic.DynamicWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleAdapter
import kotlinx.android.synthetic.main.activity_adapter_drag_sample.*

@SampleSourceCode
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/DragAdapter.md")
@RefRegister(title=R.string.drag_adapter,desc = R.string.drag_adapter_desc,category = R.string.adapter)
class DragGridSampleActivity : SampleAppCompatActivity() {
    private var colorList = mutableListOf(-0x1000000, -0xbbbbbc ,-0x777778, -0x333334, -0x1,-0x10000,-0xff0100,-0xffff01,-0x100,-0xff0001,-0xff01)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_drag_sample)
        recyclerView.layoutManager = GridLayoutManager(this,3)
        recyclerView.itemAnimator?.changeDuration=300

        val list= mutableListOf<String>()
        for(i in 0 until 15){
            list.add("Data:$i")
        }
        val adapter = SimpleAdapter(this, list)
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
        val color = colorList[kotlin.random.Random.nextInt(colorList.size)]
        val darkColor = getDarkColor(color)
        val header = LayoutInflater.from(this).inflate(R.layout.adapter_header_layout,recyclerView, false)
        val headerView = header as TextView
        header.setBackgroundColor(color)
        headerView.setTextColor(darkColor)
        headerView.text = "DynamicView:" + wrapperAdapter.extraViewCount
        return headerView
    }

    private fun getDarkColor(color: Int): Int {
        val max = 0xFF
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.rgb(if (r + 30 > max) r - 30 else r + 30, if (g + 30 > max) g - 30 else g + 30, if (b + 30 > max) b - 30 else b + 30)
    }
}
