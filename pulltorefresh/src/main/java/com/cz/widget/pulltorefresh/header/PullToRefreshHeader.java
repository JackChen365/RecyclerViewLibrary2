package com.cz.widget.pulltorefresh.header;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cz.widget.pulltorefresh.PullToRefreshState;
import com.cz.widget.pulltorefresh.strategy.PullToRefreshStrategy;

/**
 * @author Created by cz
 * @date 2020-03-02 21:49
 * @email bingo110@126.com
 */
public abstract class PullToRefreshHeader {
    /**
     * Return the refresh header view
     * @return
     */
    public abstract View getRefreshHeaderView();

    public int getHeaderHeight(){
        View refreshHeaderView = getRefreshHeaderView();
        return refreshHeaderView.getMeasuredHeight();
    }

    /**
     * When the header view attach to the container.
     * @param container
     */
    public void onAttachToWindow(@NonNull ViewGroup container){
    }

    /**
     * If you want to run a complete animation. Here return the animation's time.
     * The Strategy will postpone for a while. until the animation is done
     * @see PullToRefreshStrategy#onRefreshComplete(PullToRefreshHeader, int)
     * @return
     */
    public int completeAnimationDuration(){
        return 0;
    }

    /**
     * When the header scroll changed.
     */
    public abstract void onScrollOffset(float fraction);

    /**
     * When the refresh state changed.
     * @see PullToRefreshState
     */
    public abstract void onRefreshStateChange(PullToRefreshState refreshState);

    /**
     * When everything is done. If the class wants to clean up.
     */
    public abstract void onRefreshComplete();
}
