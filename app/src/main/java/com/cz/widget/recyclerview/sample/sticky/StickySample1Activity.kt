package com.cz.widget.recyclerview.sample.sticky

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.sticky.adapter.StickySample1Adapter
import kotlinx.android.synthetic.main.activity_sticky_sample1.*

@SampleSourceCode
@SampleDocument("StickyAdapter.md")
@RefRegister(title=R.string.sticky_sample1,desc = R.string.sticky_sample1_desc,category = R.string.sticky)
class StickySample1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_sample1)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.changeDuration=300

        val list= mutableListOf<String>()
        for(i in 0 until 100){
            list.add("Data:$i")
        }
        val adapter = StickySample1Adapter(this, list)
        recyclerView.adapter=adapter
    }
}
