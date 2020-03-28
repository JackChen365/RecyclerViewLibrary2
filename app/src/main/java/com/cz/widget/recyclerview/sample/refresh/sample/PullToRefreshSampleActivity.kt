package com.cz.widget.recyclerview.sample.refresh.sample

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.refresh.SimpleArrayAdapter
import kotlinx.android.synthetic.main.activity_pull_to_refresh_sample.*

@SampleSourceCode
@RefRegister(title=R.string.pull_to_refresh_title1,desc = R.string.pull_to_refresh_desc1,category = R.string.pull_to_refresh)
class PullToRefreshSampleActivity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_to_refresh_sample)

        val dataList = (0 until 100).map { "Data:$it" }.toList()

        recyclerView.layoutManager=LinearLayoutManager(this)
        val adapter = SimpleArrayAdapter.createFromResource(this, dataList)
        recyclerView.setAdapter(adapter)


        recyclerView.setOnPullToRefreshListener {
            adapter.addItem(0,"NewItem")
            recyclerView.onRefreshComplete()
        }
        strategyLayout.setOnCheckedChangeListener { _, index, isChecked ->
            if(isChecked){
                recyclerView.setPullToRefreshStrategy(index)
            }
        }
        autoRefresh.setOnClickListener {
            recyclerView.autoRefresh(true)
        }
    }
}
