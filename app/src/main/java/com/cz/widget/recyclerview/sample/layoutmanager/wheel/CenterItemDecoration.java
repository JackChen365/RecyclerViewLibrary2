package com.cz.widget.recyclerview.sample.layoutmanager.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;
import com.cz.widget.recyclerview.layoutmanager.wheel.WheelItemDecoration;
import com.cz.widget.recyclerview.sample.R;

/**
 * @author Created by cz
 * @date 2020-03-23 21:43
 * @email bingo110@126.com
 */
public class CenterItemDecoration extends WheelItemDecoration {
    private Drawable divider;
    private int dividerSize;

    public CenterItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(R.styleable.DebugItemDivider);
        setDivider(a.getDrawable(R.styleable.DebugItemDivider_debug_divider));
        setDividerSize(a.getDimensionPixelOffset(R.styleable.DebugItemDivider_debug_dividerHeight, 0));
        a.recycle();
    }

    /** Sets the drawable to be used as the divider. */
    public void setDivider(Drawable divider) {
        this.divider = divider;
    }

    /** Sets the divider height, in pixels. */
    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int wheelCount = getWheelCount();
        CenterLayoutManager layoutManager = getLayoutManager();
        int orientation = layoutManager.getOrientation();
        OrientationHelper orientationHelper = getOrientationHelper();
        int itemSize = orientationHelper.getEnd() / wheelCount;
        int center = itemSize*(wheelCount/2);
        if(CenterLayoutManager.HORIZONTAL==orientation){
            int top = parent.getTop();
            int bottom = parent.getBottom();
            divider.setBounds(center,top,center+dividerSize,bottom);
            divider.draw(c);
            divider.setBounds(center+itemSize,top,center+itemSize+dividerSize,bottom);
            divider.draw(c);
        } else {
            int left = parent.getLeft();
            int right = parent.getRight();
            divider.setBounds(left,center,right,center+dividerSize);
            divider.draw(c);
            divider.setBounds(left,center+itemSize,right,center+itemSize+dividerSize);
            divider.draw(c);
        }
        //The top line
//        (end-itemSize)/2
        //The bottom line

    }
}
