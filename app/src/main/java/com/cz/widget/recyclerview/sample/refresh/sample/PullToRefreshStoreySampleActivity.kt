package com.cz.widget.recyclerview.sample.refresh.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.refresh.SimpleArrayAdapter
import kotlinx.android.synthetic.main.activity_pull_to_refresh_storey_sample.*

/**
 * Need more time...
 */
//@RefRegister(title=R.string.pull_to_refresh_storey,desc = R.string.pull_to_refresh_storey_desc,category = R.string.pull_to_refresh)
class PullToRefreshStoreySampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_to_refresh_storey_sample)

        val dataList = (0 until 100).map { "Data:$it" }.toList()

        recyclerView.layoutManager= LinearLayoutManager(this)
        val adapter = SimpleArrayAdapter.createFromResource(this, dataList)
        recyclerView.adapter=adapter


//        pullToRefreshLayout.pullToRefreshHeader=StoreyLayoutHeader(R.layout.refresh_search_layout,)
//        pullToRefreshLayout.setPullToRefreshStrategy()
        pullToRefreshLayout.setOnPullToRefreshListener {
            adapter.addItem(0,"NewItem")
            //If content changed for a view like RecyclerView, otherwise it won't move to the first position.
            recyclerView.scrollToPosition(0)
            pullToRefreshLayout.onRefreshComplete()
        }
    }
}
