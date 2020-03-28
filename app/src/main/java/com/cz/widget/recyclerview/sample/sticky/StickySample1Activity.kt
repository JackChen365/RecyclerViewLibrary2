package com.cz.widget.recyclerview.sample.sticky

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.sticky.adapter.StickySimple1Adapter
import kotlinx.android.synthetic.main.activity_sticky_sample1.*

@SampleSourceCode
@RefRegister(title=R.string.sticky_sample1,desc = R.string.sticky_sample1_desc,category = R.string.sticky)
class StickySample1Activity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky_sample1)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.changeDuration=300

        val list= mutableListOf<String>()
        for(i in 0 until 100){
            list.add("Data:$i")
        }
        val adapter = StickySimple1Adapter(this, list)
        adapter.setCompareCondition { t1, t2 ->
            t1[0]!=t2[0]
        }
        adapter.setCondition { item, position ->
            item=="xxx"
        }
        recyclerView.adapter=adapter
    }
}
