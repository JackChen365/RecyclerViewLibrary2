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
 * @date 2020-03-02 23:08
 * @email bingo110@126.com
 * The official style. Like: SwipeRefreshLayout. The header follows the finger but the content was stilling
 *
 */
public class FrontStrategy extends PullToRefreshStrategy {
    @Override
    public void onViewAdded(@NonNull PullToRefreshHeader header,@NonNull View contentView, @NonNull View headerView) {
        layout.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.addView(headerView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onViewLayout(@NonNull PullToRefreshHeader header,@NonNull View contentView, @NonNull View headerView, int left, int top, int right, int bottom) {
        headerView.layout((layout.getMeasuredWidth()-headerView.getMeasuredWidth())/2,
                -headerView.getMeasuredHeight(),(layout.getMeasuredWidth()+headerView.getMeasuredWidth())/2,0);

        int paddingLeft = layout.getPaddingLeft();
        int paddingTop = layout.getPaddingTop();
        contentView.layout(paddingLeft,paddingTop, paddingLeft+contentView.getMeasuredWidth(), paddingTop+contentView.getMeasuredHeight());
    }

    @Override
    public void autoRefresh(final PullToRefreshHeader header, boolean smooth) {
        View headerView = header.getRefreshHeaderView();
        int headerHeight = headerView.getMeasuredHeight();
        if(smooth){
            ValueAnimator animator = offsetViewTopAndBottomAnimation(headerView, headerView.getBottom() - headerHeight);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    onRefreshScrollChanged(header);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    layout.setRefreshing();
                }
            });
            animator.start();
        } else {
            headerView.offsetTopAndBottom(headerView.getBottom()-headerHeight);
            onRefreshScrollChanged(header);
            layout.setRefreshing();
            layout.invalidate();
        }
    }

    private void onRefreshScrollChanged(PullToRefreshHeader header){
        if(!layout.isRefreshing()){
            View headerView = header.getRefreshHeaderView();
            int headerHeight = headerView.getMeasuredHeight();
            float fraction=Math.min(1f,Math.abs(headerView.getBottom())*1f/headerHeight);
            header.onScrollOffset(fraction);
            layout.setRefreshState(1f<=fraction? PullToRefreshState.RELEASE_TO_REFRESHING : PullToRefreshState.RELEASE_TO_CANCEL);
        }
    }


    @Override
    public boolean onRefreshFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onPullToRefresh(View target, PullToRefreshHeader header, int dx, int dy, int maxScroll, int[] consumed) {
        float resistance = layout.getPullResistance();
        View headerView = header.getRefreshHeaderView();
        int bottom = headerView.getBottom();
        if(0>dy){
            if(maxScroll<bottom){
                dy= 0;
            } else if(maxScroll<dy+bottom){
                dy=maxScroll-bottom;
            }
        }
        if(!layout.isRefreshing()&& (0>dy&&!target.canScrollVertically(dy))){
            consumed[1]= (int) (dy/resistance);
            onRefreshScrollChanged(header);
            headerView.offsetTopAndBottom((int) (-dy/resistance));
            headerView.invalidate();
        } else if(!layout.isRefreshing()&&bottom>target.getTop()){
            consumed[1]=dy;
            onRefreshScrollChanged(header);
            headerView.offsetTopAndBottom(-dy);
            headerView.invalidate();
        }
    }

    @Override
    public void onStopRefreshScroll(View target, PullToRefreshHeader header) {
        final View headerView = header.getRefreshHeaderView();
        if(layout.isRefreshState(PullToRefreshState.RELEASE_TO_CANCEL)){
            ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(headerView, headerView.getBottom());
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    layout.setRefreshState(PullToRefreshState.NONE);
                }
            });
            valueAnimator.start();
        } else if(layout.isRefreshState(PullToRefreshState.RELEASE_TO_REFRESHING)){
            final int headerHeight = header.getHeaderHeight();
            ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(headerView, headerView.getBottom() - headerHeight);
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    layout.setRefreshing();
                }
            });
            valueAnimator.start();
        } else if(layout.isRefreshState(PullToRefreshState.REFRESHING_COMPLETE)) {
            ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(headerView, headerView.getBottom());
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    layout.setRefreshState(PullToRefreshState.NONE);
                }
            });
            valueAnimator.start();
        }
    }

    @Override
    public void onRefreshComplete(PullToRefreshHeader header,int delayedTime) {
        int completeAnimationDuration = header.completeAnimationDuration();
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                PullToRefreshHeader pullToRefreshHeader = layout.getPullToRefreshHeader();
                View headerView = pullToRefreshHeader.getRefreshHeaderView();
                ValueAnimator valueAnimator = offsetViewTopAndBottomAnimation(headerView, headerView.getBottom());
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
