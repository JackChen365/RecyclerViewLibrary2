package com.cz.widget.recyclerview.sample.refresh.sample

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.pulltorefresh.header.VectorRefreshHeader
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.refresh.SimpleArrayAdapter
import kotlinx.android.synthetic.main.activity_pull_to_refresh_vector_sample.*

@SampleSourceCode
@RefRegister(title=R.string.pull_to_refresh_vector,desc = R.string.pull_to_refresh_vector_desc,category = R.string.pull_to_refresh)
class PullToRefreshVectorSampleActivity : SampleAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_to_refresh_vector_sample)

        val dataList = (0 until 100).map { "Data:$it" }.toList()

        recyclerView.layoutManager= LinearLayoutManager(this)
        val adapter=SimpleArrayAdapter.createFromResource(this,dataList)
        recyclerView.setAdapter(adapter)


        recyclerView.pullToRefreshHeader=VectorRefreshHeader(R.layout.vecor_header_layout)
        recyclerView.setOnPullToRefreshListener {
            adapter.addItem(0,"NewItem")
            recyclerView.onRefreshComplete()
        }
    }
}
