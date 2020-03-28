package com.cz.widget.pulltorefresh.strategy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cz.widget.pulltorefresh.PullToRefreshState;
import com.cz.widget.pulltorefresh.header.PullToRefreshHeader;

/**
 * @author Created by cz
 * @date 2020-03-02 23:31
 * @email bingo110@126.com
 *
 * Only allow content scroll, and stick the header in the back.
 */
public class OverlapStrategy extends PullToRefreshStrategy {
    @Override
    public void onViewAdded(@NonNull PullToRefreshHeader header,@NonNull View contentView, @NonNull View headerView) {
        layout.addView(headerView);
        layout.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onViewLayout(@NonNull PullToRefreshHeader header,@NonNull View contentView, @NonNull View headerView, int left, int top, int right, int bottom) {
        headerView.layout((layout.getMeasuredWidth()-headerView.getMeasuredWidth())/2,
                0,(layout.getMeasuredWidth()+headerView.getMeasuredWidth())/2,headerView.getMeasuredHeight());

        int paddingLeft = layout.getPaddingLeft();
        int paddingTop = layout.getPaddingTop();
        contentView.layout(paddingLeft,paddingTop, paddingLeft+contentView.getMeasuredWidth(), paddingTop+contentView.getMeasuredHeight());
    }

    @Override
    public void autoRefresh(final PullToRefreshHeader header, boolean smooth) {
        int headerHeight = header.getHeaderHeight();
        View contentView = layout.getContentView();
        if(smooth){
            ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(contentView, contentView.getTop() - headerHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    onRefreshScrollChanged(header);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    layout.setRefreshing();
                }
            });
            valueAnimator.start();
        } else {
            contentView.offsetTopAndBottom(contentView.getTop()-headerHeight);
            onRefreshScrollChanged(header);
            layout.setRefreshing();
            layout.invalidate();
        }
    }

    @Override
    public boolean onRefreshFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onPullToRefresh(View target, PullToRefreshHeader header, int dx, int dy, int maxScroll, int[] consumed) {
        int top = target.getTop();
        float resistance = layout.getPullResistance();
        if(0>dy){
            if(maxScroll<top){
                dy= 0;
            } else if(maxScroll<top-dy){
                dy=top-maxScroll;
            }
        }
        if(0>dy&&!target.canScrollVertically(dy)){
            consumed[1]= (int) (dy/resistance);
            target.offsetTopAndBottom((int) (-dy/resistance));
            onRefreshScrollChanged(header);
        } else if(top>0){
            if(top-dy>maxScroll){
                dy=top-maxScroll;
            }
            consumed[1]=dy;
            target.offsetTopAndBottom(-dy);
            onRefreshScrollChanged(header);
        }
    }

    private void onRefreshScrollChanged(PullToRefreshHeader header){
        if(!layout.isRefreshing()){
            int headerHeight = header.getHeaderHeight();
            View contentView = layout.getContentView();
            float fraction=Math.min(1f,contentView.getTop()*1f/headerHeight);
            header.onScrollOffset(fraction);
            layout.setRefreshState(1f<=fraction ? PullToRefreshState.RELEASE_TO_REFRESHING : PullToRefreshState.RELEASE_TO_CANCEL);
        }
    }

    @Override
    public void onStopRefreshScroll(View target, PullToRefreshHeader header) {
        int top = target.getTop();
        if(!layout.isRefreshState(PullToRefreshState.REFRESHING_COMPLETE)){
            int headerHeight = header.getHeaderHeight();
            if(headerHeight<=top){
                if(!layout.isRefreshing()){
                    layout.setRefreshing();
                }
                offsetViewTopAndBottomAnimation(target, top - headerHeight).start();
            } else {
                layout.setRefreshState(PullToRefreshState.NONE);
                offsetViewTopAndBottomAnimation(target, top).start();
            }
        } else {
            layout.setRefreshState(PullToRefreshState.NONE);
            offsetViewTopAndBottomAnimation(target, top).start();
        }
    }

    @Override
    public void onRefreshComplete(PullToRefreshHeader header,int delayedTime) {
        int completeAnimationDuration = header.completeAnimationDuration();
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshState(PullToRefreshState.NONE);
                View contentView = layout.getContentView();
                ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(contentView, contentView.getTop());
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.requestLayout();
                    }
                });
                valueAnimator.start();
            }
        },completeAnimationDuration);
    }
}
