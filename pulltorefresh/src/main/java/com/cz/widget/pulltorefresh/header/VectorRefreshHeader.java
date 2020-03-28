package com.cz.widget.pulltorefresh.header;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import com.cz.widget.pulltorefresh.PullToRefreshState;
import com.cz.widget.pulltorefresh.widget.HeaderVectorView;

/**
 * @author Created by cz
 * @date 2020-03-05 17:18
 * @email bingo110@126.com
 */
public class VectorRefreshHeader extends PullToRefreshHeader {
    @LayoutRes
    private final int layout;
    private HeaderVectorView headerView;

    public VectorRefreshHeader(int layout) {
        this.layout=layout;
    }

    @Override
    public void onAttachToWindow(ViewGroup container) {
        super.onAttachToWindow(container);
        Context context = container.getContext();
        headerView = (HeaderVectorView) LayoutInflater.from(context).inflate(layout, container, false);
    }

    @Override
    public int completeAnimationDuration() {
        return (int) headerView.completeAnimationDuration();
    }

    @Override
    public View getRefreshHeaderView() {
        return headerView;
    }

    @Override
    public void onScrollOffset(float fraction) {
        headerView.onScrollOffset(fraction);
    }

    @Override
    public void onRefreshStateChange(PullToRefreshState refreshState) {
        headerView.onRefreshStateChange(refreshState);
    }

    @Override
    public void onRefreshComplete() {
        headerView.onRefreshComplete();
    }
}
