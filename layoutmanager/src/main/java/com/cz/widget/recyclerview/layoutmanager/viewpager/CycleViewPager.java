package com.cz.widget.recyclerview.layoutmanager.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.layoutmanager.R;
import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;
import com.cz.widget.recyclerview.layoutmanager.widget.AbsCycleLayout;

/**
 * @author Created by cz
 * @date 2020-03-22 15:56
 * @email bingo110@126.com
 */
public class CycleViewPager extends AbsCycleLayout {
    private AutoScrollRunnable autoScrollRunnable =new AutoScrollRunnable();
    private boolean autoScroll;
    private int scrollDuration;
    private int scrollInterval;

    public CycleViewPager(Context context) {
        this(context,null,0);
    }

    public CycleViewPager(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CycleViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CycleViewPager,defStyleAttr,R.style.CycleViewPager);
        setPageAutoScrollDuration(a.getInteger(R.styleable.CycleViewPager_pager_autoScrollDuration,0));
        setPageAutoScrollInterval(a.getInteger(R.styleable.CycleViewPager_pager_autoScrollInterval,0));
        a.recycle();
    }

    private void setPageAutoScrollDuration(int scrollDuration) {
        this.scrollDuration=scrollDuration;
    }

    private void setPageAutoScrollInterval(int scrollInterval) {
        this.scrollInterval=scrollInterval;
    }

    /**
     * Start infinite scroll.
     * @see #scrollDuration
     * @see #scrollInterval
     */
    public void startAutoScroll() {
        int minimumCycleCount = getMinimumCycleCount();
        int itemCount = getItemCount();
        if (minimumCycleCount<itemCount) {
            autoScroll = true;
            removeCallbacks(autoScrollRunnable);
            postDelayed(autoScrollRunnable, scrollInterval);
        }
    }

    /**
     * Stop scrolling.
     */
    public void stopAutoScroll() {
        autoScroll = false;
        removeCallbacks(autoScrollRunnable);
    }

    @Override
    protected RecyclerView createRecyclerView(Context context) {
        return new RecyclerViewImpl(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAutoScroll();
        super.onDetachedFromWindow();
    }

    /**
     * Change the recycler view auto scroll duration.
     */
    private class RecyclerViewImpl extends RecyclerView {
        RecyclerViewImpl(@NonNull Context context) {
            super(context);
        }

        /**
         * We change the smooth scroll duration When we scroll the page automatically
         * @param dx
         * @param dy
         * @param interpolator
         * @param duration
         */
        @Override
        public void smoothScrollBy(int dx, int dy, @Nullable Interpolator interpolator, int duration) {
            if(autoScroll){
                super.smoothScrollBy(dx, dy, interpolator, scrollDuration);
            } else {
                super.smoothScrollBy(dx,dy,interpolator,duration);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return isUserInputEnabled() && super.onTouchEvent(event);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return isUserInputEnabled() && super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    protected CenterLayoutManager createLayoutManager(Context context,AttributeSet attrs, int defStyleAttr) {
        return new LinearLayoutManagerImpl(context,attrs,defStyleAttr,R.style.CycleViewPager);
    }

    private class LinearLayoutManagerImpl extends ViewPagerLayoutManager {

        public LinearLayoutManagerImpl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state,
                                                 @NonNull int[] extraLayoutSpace) {
            int pageLimit = getOffscreenPageLimit();
            if (pageLimit == OFFSCREEN_PAGE_LIMIT_DEFAULT) {
                // Only do custom prefetching of offscreen pages if requested
                super.calculateExtraLayoutSpace(state, extraLayoutSpace);
                return;
            }
            final int offscreenSpace = getPageSize() * pageLimit;
            extraLayoutSpace[0] = offscreenSpace;
            extraLayoutSpace[1] = offscreenSpace;
        }

        @Override
        public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent,
                                                     @NonNull View child, @NonNull Rect rect, boolean immediate,
                                                     boolean focusedChildVisible) {
            return false; // users should use setCurrentItem instead
        }
    }

    public class AutoScrollRunnable implements Runnable {

        @Override
        public void run() {
            if (hasWindowFocus() && isShown()) {
                if (autoScroll) {
                    int itemCount = getItemCount();
                    int currentItem = getCurrentItem();
                    setCurrentItem((currentItem+1)%itemCount);
                }
            }
            removeCallbacks(this);
            postDelayed(this, scrollInterval);
        }
    }

}
