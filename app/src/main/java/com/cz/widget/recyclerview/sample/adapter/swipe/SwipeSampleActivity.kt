package com.cz.widget.recyclerview.sample.adapter.swipe

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.widget.recyclerview.sample.R
import kotlinx.android.synthetic.main.activity_swipe_sample.*

//@RefRegister(title= R.string.swipe_adapter1,desc = R.string.swipe_adapter_desc1,category = R.string.adapter)
class SwipeSampleActivity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe_sample)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator?.addDuration=300
        val list= mutableListOf<String>()
        for(i in 0 until 100){
            list.add("DataDataData:$i")
        }
        val adapter = SwipeSimpleAdapter(this, list)
        recyclerView.adapter=adapter
    }
}
