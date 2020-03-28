package com.cz.widget.pulltorefresh.strategy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cz.widget.pulltorefresh.PullToRefreshState;
import com.cz.widget.pulltorefresh.header.PullToRefreshHeader;

/**
 * @author Created by cz
 * @date 2020-03-02 23:45
 * @email bingo110@126.com
 */
public class DisplayStrategy extends PullToRefreshStrategy {
    @Override
    public void onViewAdded(@NonNull PullToRefreshHeader header,@NonNull View contentView, @NonNull View headerView) {
        layout.addView(headerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        View contentView = layout.getContentView();
        int top = contentView.getTop();
        int headerHeight = header.getHeaderHeight();
        if(smooth){
            ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(contentView, top - headerHeight);
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    layout.setRefreshing();
                    layout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onRefreshComplete(header,0);
                        }
                    },100);
                }
            });
            valueAnimator.start();
        } else {
            offsetViewTopAndBottomAnimation(contentView, top - headerHeight).start();
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
        } else if(top>0){
            if(top-dy>maxScroll){
                dy=top-maxScroll;
            }
            consumed[1]=dy;
            target.offsetTopAndBottom(-dy);
        }
    }

    @Override
    public void onStopRefreshScroll(final View target, PullToRefreshHeader header) {
        Log.i("onStopRefreshScroll","top:"+target.getTop());
        offsetViewTopAndBottomAnimation(target, target.getTop()).start();
    }

    @Override
    public void onRefreshComplete(PullToRefreshHeader header,int delayedTime) {
        int completeAnimationDuration = header.completeAnimationDuration();
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                View contentView = layout.getContentView();
                ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(contentView, contentView.getTop());
                Log.i("onStopRefreshScroll","onRefreshComplete====");
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setRefreshState(PullToRefreshState.NONE);
                    }
                });
                valueAnimator.start();
            }
        },completeAnimationDuration);
    }
}
