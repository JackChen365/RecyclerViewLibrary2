package com.cz.widget.recyclerview.sample.refresh.header

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cz.widget.pulltorefresh.PullToRefreshState
import com.cz.widget.pulltorefresh.header.PullToRefreshHeader
import com.cz.widget.recyclerview.sample.R

/**
 * @author Created by cz
 * @date 2020-03-04 19:32
 * @email bingo110@126.com
 */
class DisplayHeader: PullToRefreshHeader() {
    private var headerView:View?=null
    override fun getRefreshHeaderView(): View? {
        return headerView
    }

    override fun onAttachToWindow(container: ViewGroup) {
        super.onAttachToWindow(container)
        headerView=LayoutInflater.from(container.context).inflate(R.layout.refresh_web_header_layout,container,false)
    }

    override fun onScrollOffset(fraction: Float) {
    }

    override fun onRefreshStateChange(refreshState: PullToRefreshState?) {
    }

    override fun onRefreshComplete() {
    }

}