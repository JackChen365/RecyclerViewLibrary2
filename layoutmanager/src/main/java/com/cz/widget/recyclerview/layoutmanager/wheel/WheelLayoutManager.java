package com.cz.widget.recyclerview.layoutmanager.wheel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;



/**
 * @author Created by cz
 * @date 2020-03-22 23:14
 * @email bingo110@126.com
 */
public class WheelLayoutManager extends CenterLayoutManager {
    private int wheelCount = 3;

    public WheelLayoutManager(Context context) {
        super(context);
    }

    public WheelLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setWheelCount(int itemCount) {
        this.wheelCount = itemCount;
        removeAllViews();
        requestLayout();
    }

    public int getWheelCount() {
        return wheelCount;
    }

    @Override
    protected void addAdapterView(View view) {
        super.addAdapterView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smoothScrollToView(v);
            }
        });
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        int measureContentWidth = 0;
        int measureContentHeight = 0;
        int itemCount = getItemCount();
        int orientation = getOrientation();
        if (0 != itemCount&&!state.isPreLayout()) {
            OrientationHelper orientationHelper = getOrientationHelper();
            for(int i=0;i<wheelCount;i++){
                View view = recycler.getViewForPosition(i);
                //We measure the view by different orientation.
                if(HORIZONTAL==orientation){
                    //Only measure the width unspecified. But measure the child view's height by parent's mode.
                    measurePreLayoutChild(view, View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    measureContentWidth+=orientationHelper.getDecoratedMeasurement(view);
                    measureContentHeight=orientationHelper.getDecoratedMeasurementInOther(view);
                } else {
                    //Only measure the height unspecified. But measure the child view's width by parent's mode.
                    measurePreLayoutChild(view, View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    measureContentWidth=orientationHelper.getDecoratedMeasurementInOther(view);
                    measureContentHeight+=orientationHelper.getDecoratedMeasurement(view);
                }
                recycler.recycleView(view);
                //Since we recycle this view. But for next time.If want this view re-measured. We should request layout.
                //Take a look at:RecyclerView.LayoutManager.shouldMeasureChild
                view.requestLayout();
            }
        }
        int measuredWidth = View.MeasureSpec.getSize(widthSpec);
        int measuredHeight = View.MeasureSpec.getSize(heightSpec);
        if(HORIZONTAL==orientation){
            measureContentHeight = getPaddingTop() + measureContentHeight + getPaddingBottom();
            setMeasuredDimension(getPaddingLeft()+measureContentWidth+getPaddingRight(), Math.max(measureContentHeight,measuredHeight));
        } else {
            measureContentWidth = getPaddingLeft() + measureContentWidth + getPaddingRight();
            setMeasuredDimension(Math.max(measuredWidth,measureContentWidth), getPaddingTop()+measureContentHeight+getPaddingBottom());
        }
    }
    /**
     * This function only for pre-layout. We use unspecified mode to measure the view.
     * Otherwise, it may return an unexpected size.
     * @see #wheelCount We use the wheel count determine the size
     * @param child
     */
    private void measurePreLayoutChild(View child,int widthMode, int heightMode) {
        final RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int hPadding = getPaddingLeft() + getPaddingRight();
        final int vPadding = getPaddingTop() + getPaddingBottom();
        final int hMargin = p.leftMargin + p.rightMargin;
        final int vMargin = p.topMargin + p.bottomMargin;
        final int hDecoration = getRightDecorationWidth(child) + getLeftDecorationWidth(child);
        final int vDecoration = getTopDecorationHeight(child) + getBottomDecorationHeight(child);

        final int childWidthSpec = getChildMeasureSpec(getWidth(), widthMode,hPadding + hMargin + hDecoration, p.width, canScrollHorizontally());
        final int childHeightSpec = getChildMeasureSpec(getHeight(), heightMode,vPadding + vMargin + vDecoration, p.height, canScrollVertically());
        child.measure(childWidthSpec, childHeightSpec);
    }
}
