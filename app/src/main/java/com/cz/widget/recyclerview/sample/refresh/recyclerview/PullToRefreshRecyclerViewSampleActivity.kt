package com.cz.widget.recyclerview.sample.refresh.recyclerview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widget.pulltorefresh.RefreshMode
import com.cz.widget.recyclerview.library.PullToRefreshRecyclerView
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.SimpleAdapter
import kotlinx.android.synthetic.main.activity_pull_to_refresh_recycler_view_sample.*

@SampleSourceCode
@SampleDocument("https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/library/README.MD")
@RefRegister(title=R.string.pull_to_refresh_recycler_view_title1,desc = R.string.pull_to_refresh_recycler_view_desc,category = R.string.pull_to_refresh)
class PullToRefreshRecyclerViewSampleActivity : SampleAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_to_refresh_recycler_view_sample)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.setOnItemClickListener { v: View, position: Int, _: Int ->
//            Snackbar.make(v, getString(R.string.click_position, position), Snackbar.LENGTH_LONG).show()
        }
        recyclerView.addHeaderView(getHeaderView())
//        recyclerView.addFooterView(getFooterView())

        val dataProvider = DataManager.getDataProvider(this)
        val adapter = SimpleAdapter(this, dataProvider.getWordList(4))
        recyclerView.setAdapter(adapter)

        recyclerView.setOnPullToRefreshListener {
            adapter.add("New item from top", 0)
            recyclerView.onRefreshComplete()
        }
        var times=0
        recyclerView.setOnPullFooterToRefreshListener {
            if (times < 2) {
                recyclerView.postDelayed({
                    adapter.add("New Item")
                    recyclerView.onRefreshFooterComplete()
                }, 1000)
            } else if (times < 3) {
                recyclerView.setRefreshFooterFrame(PullToRefreshRecyclerView.FOOTER_ERROR)
            }  else if(times < 4){
                recyclerView.postDelayed({
                    adapter.add("New Item")
                    recyclerView.onRefreshFooterComplete()
                }, 1000)
            }  else {
                recyclerView.postDelayed({ recyclerView.setRefreshFooterFrame(PullToRefreshRecyclerView.FOOTER_COMPLETE) }, 1000)
            }
            times++
        }

        refreshModeLayout.setOnCheckedChangeListener { _, index, selected ->
            if(selected){
                recyclerView.refreshMode = when (index) {
                    0 ->  RefreshMode.BOTH
                    1 -> RefreshMode.PULL_START
                    2 -> RefreshMode.PULL_END
                    else -> RefreshMode.NONE
                }
            }
        }
    }

    /**
     * Return a header view
     */
    private fun getHeaderView(): View {
        val dataProvider = DataManager.getDataProvider(this)
        val colorArray = dataProvider.getColorArray(DataProvider.COLOR_RED)
        val color = colorArray[DataProvider.RANDOM.nextInt(colorArray.size)]
        val header = LayoutInflater.from(this).inflate(R.layout.adapter_header_layout, recyclerView, false)
        val headerView = header as TextView
        headerView.setBackgroundColor(color)
        headerView.setTextColor(Color.WHITE)
        headerView.text = "HeaderView:" + recyclerView.headerViewCount
        headerView.setOnClickListener { recyclerView.addHeaderView(getHeaderView()) }
        return headerView
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_refresh, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_refresh) {
            recyclerView.autoRefresh(true)
            return true
        } else if (id == R.id.action_re_refresh) {
            recyclerView.autoRefresh(false)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
