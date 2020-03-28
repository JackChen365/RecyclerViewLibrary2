package com.cz.widget.recyclerview.layoutmanager.viewpager;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;

/**
 * @author Created by cz
 * @date 2020-03-22 13:18
 * @email bingo110@126.com
 * This class only implement {@link RecyclerView.SmoothScroller.ScrollVectorProvider}
 * in order to support {@link androidx.recyclerview.widget.PagerSnapHelper}
 *
 * But for a center layout manager. We don't have to use page snap helper.
 *
 */
public class ViewPagerLayoutManager extends CenterLayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    public ViewPagerLayoutManager(Context context) {
        super(context);
    }

    public ViewPagerLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    /**
     * This function support the ViewPager page scrolling.
     * @see SnapHelper# snapFromFling(androidx.recyclerview.widget.RecyclerView.LayoutManager, int, int)
     * @param targetPosition
     * @return
     */
    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return smoothScrollComputeScrollVector(targetPosition);
    }
}
