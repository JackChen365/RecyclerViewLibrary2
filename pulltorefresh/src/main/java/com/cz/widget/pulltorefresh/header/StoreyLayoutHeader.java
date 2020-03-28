package com.cz.widget.pulltorefresh.header;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.cz.widget.pulltorefresh.PullToRefreshState;

/**
 * @author Created by cz
 * @date 2020-03-07 22:27
 * @email bingo110@126.com
 */
public class StoreyLayoutHeader extends PullToRefreshHeader {

    /**
     * The extra view between header view and content view
     */
    private View storeyView;
    private View headerView;
    private int storeyLayout;
    private int layout;

    public StoreyLayoutHeader(@LayoutRes int storeyLayout, @LayoutRes int layout) {
        this.storeyLayout = storeyLayout;
        this.layout=layout;
    }

    @NonNull
    public View getStoreyView() {
        return storeyView;
    }

    @Override
    public void onAttachToWindow(@NonNull ViewGroup container) {
        super.onAttachToWindow(container);
        LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());
        storeyView=layoutInflater.inflate(storeyLayout,container,false);
        headerView=layoutInflater.inflate(layout,container,false);
    }

    @Override
    public View getRefreshHeaderView() {
        return headerView;
    }

    @Override
    public void onScrollOffset(float fraction) {

    }

    @Override
    public void onRefreshStateChange(PullToRefreshState refreshState) {

    }

    @Override
    public void onRefreshComplete() {

    }
}
