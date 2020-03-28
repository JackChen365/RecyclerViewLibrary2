package com.cz.widget.pulltorefresh.strategy;

import android.animation.ValueAnimator;
import android.view.View;

import androidx.annotation.NonNull;

import com.cz.widget.pulltorefresh.PullToRefreshLayout;
import com.cz.widget.pulltorefresh.header.PullToRefreshHeader;

/**
 * @author Created by cz
 * @date 2020-03-02 21:28
 * @email bingo110@126.com
 * The basic strategy for the view {@link com.cz.widget.pulltorefresh.PullToRefreshLayout}
 * This class allow you to implement your own pull to refresh gesture.
 * We already have four different strategies.
 */
public abstract class PullToRefreshStrategy {
    /**
     * The target view.
     */
    PullToRefreshLayout layout;

    public static final int DEFAULT_SCROLL_DURATION=300;

    public PullToRefreshStrategy() {
    }

    /**
     * When we use a specific strategy. The view will call this method.
     * @see
     * @param layout
     */
    public void setTarget(PullToRefreshLayout layout){
        this.layout=layout;
    }

    /**
     * Let's the Strategy object determine which one should put in front or in the back
     * It actually depends on different products
     * @param contentView
     * @param headerView
     */
    public abstract void onViewAdded(@NonNull PullToRefreshHeader header,@NonNull View contentView,@NonNull View headerView);

    /**
     * Layout the view. Different strategies could have different way to layout the view.
     * @param contentView
     * @param headerView
     */
    public abstract void onViewLayout(@NonNull PullToRefreshHeader header,@NonNull View contentView,@NonNull View headerView,int left,int top,int right,int bottom);

    /**
     * Trigger the refresh event programmatically.
     * @param header The header. We get header's height from it
     * @param smooth this determines we scroll to the header fast or slowly
     */
    public abstract void autoRefresh(PullToRefreshHeader header, boolean smooth);

    /**
     * When user released. Sometimes it fast to fling.
     * But when it fling. It will won't call the function {@link PullToRefreshStrategy#onStopRefreshScroll(View, PullToRefreshHeader)}
     *
     */
    public abstract boolean onRefreshFling(View target,float velocityX,float velocityY);

    /**
     * When the finger keep move. We consider change the header and the content.
     * Here each strategy could move the target view and the header by its own procedure.
     *
     */
    public abstract void onPullToRefresh(View target, PullToRefreshHeader header, int dx, int dy, int maxScroll, int[] consumed);

    /**
     * Normally when user slowly stop scroll. Either trigger the refresh or go back.
     * It will trigger this function. But it won't call {@link PullToRefreshStrategy#onRefreshFling(View, float, float)}
     */
    public abstract void onStopRefreshScroll(View target,PullToRefreshHeader header);

    /**
     * When content changed. If we didn't layout the view. Sometimes it comes with unexpected problems.
     * So here for some strategy we allow you to control if you want to layout the view.
     * @return
     */
    public boolean contentChangedLayout(){
        return true;
    }

    /**
     * After refresh completed.
     * We call this function to notify the view. We are done
     */
    public abstract void onRefreshComplete(PullToRefreshHeader header,int delayedTime);


    /**
     * Return an animator that will move the view for a short distance.
     * This animator for some special strategy. Because they don't usually scroll the whole view.
     * But scroll one view instead. {@link FrontStrategy}
     * @param v
     * @param top
     * @return
     */
    protected ValueAnimator offsetViewTopAndBottomAnimation(final View v, final int top) {
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(top);
        valueAnimator.setDuration(DEFAULT_SCROLL_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int lastValue = 0;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) valueAnimator.getAnimatedValue();
                v.offsetTopAndBottom(lastValue - value);
                this.lastValue = value;
            }
        });
        return valueAnimator;
    }
}
