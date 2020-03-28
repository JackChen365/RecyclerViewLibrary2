package com.cz.widget.pulltorefresh.strategy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import androidx.annotation.NonNull;

import com.cz.widget.pulltorefresh.PullToRefreshState;
import com.cz.widget.pulltorefresh.header.PullToRefreshHeader;
import com.cz.widget.pulltorefresh.header.StoreyLayoutHeader;

/**
 * @author Created by cz
 * @date 2020-03-02 22:07
 * @email bingo110@126.com
 *
 * The most popular strategy that the header will follow the content.
 * todo need more time.
 */
public class StoreyStrategy extends PullToRefreshStrategy {
    /**
     * The fling action.
     */
    private Runnable flingAction = new FlingAction();

    @Override
    public void onViewAdded(@NonNull PullToRefreshHeader header,@NonNull View contentView, @NonNull View headerView) {
        View storeyView = null;
        if(header instanceof StoreyLayoutHeader){
            StoreyLayoutHeader storeyLayoutHeader = (StoreyLayoutHeader) header;
            storeyView = storeyLayoutHeader.getStoreyView();
        }
        layout.addView(headerView);
        if(null!=storeyView){
            layout.addView(headerView);
        }
        layout.addView(contentView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onViewLayout(@NonNull PullToRefreshHeader header,@NonNull View contentView, @NonNull View headerView, int left, int top, int right, int bottom) {
        View storeyView = null;
        int measuredHeight = 0;
        if(header instanceof StoreyLayoutHeader){
            StoreyLayoutHeader storeyLayoutHeader = (StoreyLayoutHeader) header;
            storeyView = storeyLayoutHeader.getStoreyView();
            measuredHeight = storeyView.getMeasuredHeight();
        }

        headerView.layout((layout.getMeasuredWidth()-headerView.getMeasuredWidth())/2,
                -headerView.getMeasuredHeight()-measuredHeight,(layout.getMeasuredWidth()+headerView.getMeasuredWidth())/2,-measuredHeight);

        if(null!=storeyView){
            storeyView.layout((layout.getMeasuredWidth()-headerView.getMeasuredWidth())/2,
                    -measuredHeight,(layout.getMeasuredWidth()+headerView.getMeasuredWidth())/2,0);
        }
        int paddingLeft = layout.getPaddingLeft();
        int paddingTop = layout.getPaddingTop();
        contentView.layout(paddingLeft,paddingTop, paddingLeft+contentView.getMeasuredWidth(), paddingTop+contentView.getMeasuredHeight());
    }

    @Override
    public void autoRefresh(PullToRefreshHeader header, boolean smooth) {
        int scrollY = layout.getScrollY();
        int headerHeight = header.getHeaderHeight();
        if(smooth){
            layout.startScroll(0,scrollY,0,-headerHeight-scrollY,DEFAULT_SCROLL_DURATION);
            layout.removeCallbacks(flingAction);
            layout.post(flingAction);
        } else {
            layout.scrollTo(0,-headerHeight-scrollY);
            layout.setRefreshing();
        }
        layout.invalidate();
    }

    @Override
    public boolean onRefreshFling(View target, float velocityX, float velocityY) {
        return layout.getScrollY()<target.getTop();
    }

    @Override
    public void onPullToRefresh(View target, PullToRefreshHeader header, int dx, int dy, int maxScroll, int[] consumed) {
        int scrollY = layout.getScrollY();
        if(0>dy){
            //We don't allow the view goes higher.
            if(-maxScroll>scrollY){
                dy= 0;
            } else if(-maxScroll>scrollY+dy){
                dy=-scrollY-maxScroll;
            }
        }
        float resistance = layout.getPullResistance();
        if(0>dy&&!target.canScrollVertically(dy)){
            consumed[1]= (int) (dy/resistance);
            layout.scrollBy(0, (int) (dy/resistance));
            onRefreshScrollChanged(header,false);
        } else if(scrollY<target.getTop()){
            if(dy+scrollY>target.getTop()){
                dy=target.getTop()-scrollY;
            }
            consumed[1]= dy;
            layout.scrollBy(0, dy);
            onRefreshScrollChanged(header,false);
        }
    }

    private void onRefreshScrollChanged(PullToRefreshHeader header,boolean autoRefresh){
        if(autoRefresh||!layout.isRefreshing()){
            View refreshHeaderView = header.getRefreshHeaderView();
            int headerHeight = refreshHeaderView.getMeasuredHeight();
            float fraction = 0;
            int scrollY = Math.abs(layout.getScrollY());
            if(scrollY > headerHeight/3){
                fraction=Math.min(1f,(scrollY-headerHeight/3)*1f/headerHeight);
            }
            header.onScrollOffset(fraction);
            layout.setRefreshState(1f<=fraction ? PullToRefreshState.RELEASE_TO_REFRESHING : PullToRefreshState.RELEASE_TO_CANCEL);
        }
    }

    @Override
    public void onStopRefreshScroll(View target, PullToRefreshHeader header) {
        int scrollY = layout.getScrollY();
        int headerHeight = header.getHeaderHeight();
        if(!layout.isRefreshState(PullToRefreshState.REFRESHING_COMPLETE)&&-scrollY>=headerHeight){
            layout.startScroll(0,scrollY,0,-headerHeight-scrollY,DEFAULT_SCROLL_DURATION);
            layout.setRefreshing();
        } else {
            layout.startScroll(0,scrollY,0,-scrollY,DEFAULT_SCROLL_DURATION);
            layout.setRefreshState(PullToRefreshState.NONE);
        }
    }

    @Override
    public void onRefreshComplete(PullToRefreshHeader header,int delayedTime) {
        int completeAnimationDuration = header.completeAnimationDuration();
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                int scrollY = layout.getScrollY();
                layout.startScroll(0,scrollY,0,-scrollY,DEFAULT_SCROLL_DURATION);
                layout.setRefreshState(PullToRefreshState.NONE);
            }
        },completeAnimationDuration);
    }

    public class FlingAction implements Runnable{
        @Override
        public void run() {
            OverScroller scroller = layout.getScroller();
            if(!scroller.isFinished()&&scroller.computeScrollOffset()){
                PullToRefreshHeader header = layout.getPullToRefreshHeader();
                onRefreshScrollChanged(header,true);
                layout.postDelayed(this,16);
            } else {
                layout.setRefreshing();
            }
        }


    }
}
