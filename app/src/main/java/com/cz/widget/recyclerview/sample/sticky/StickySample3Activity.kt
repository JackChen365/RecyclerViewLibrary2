package com.cz.widget.recyclerview.sample.sticky

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.adapter.wrapper.sticky.StickyWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.sticky.adapter.StickySimple3Adapter
import kotlinx.android.synthetic.main.activity_sticky_sample3.*

@SampleSourceCode
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/adapter/document/en/StickyAdapter.md")
@RefRegister(title=R.string.sticky_sample3,desc = R.string.sticky_sample3_desc,category = R.string.sticky,priority = 1)
class StickySample3Activity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_sample3)

        recyclerView.layoutManager = GridLayoutManager(this,2)
        recyclerView.itemAnimator?.changeDuration=300

        val list= mutableListOf<String>()
        for(i in 0 until 100){
            list.add("Data:$i")
        }
        val adapter = StickySimple3Adapter(this, list)
        recyclerView.adapter= StickyWrapperAdapter(adapter)
    }
}
