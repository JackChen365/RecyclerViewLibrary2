package com.cz.widget.recyclerview.layoutmanager.base;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by cz
 * @date 2020-03-21 20:38
 * @email bingo110@126.com
 */
public class CenterSmoothScroller extends LinearSmoothScroller {

    public CenterSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if(!(layoutManager instanceof CenterLayoutManager)){
            super.onTargetFound(targetView,state,action);
        } else {
            CenterLayoutManager centerLinearLayoutManager = (CenterLayoutManager) layoutManager;
            int horizontalSnapPreference = getHorizontalSnapPreference();
            int verticalSnapPreference = getVerticalSnapPreference();
            int dx = calculateDxToMakeVisible(targetView, horizontalSnapPreference);
            int dy = calculateDyToMakeVisible(targetView, verticalSnapPreference);
            int distance = (int) Math.sqrt((dx * dx + dy * dy));
            int time = calculateTimeForDeceleration(distance);
            int offsetX = (layoutManager.getWidth() - targetView.getMeasuredWidth()) / 2;
            int offsetY = (layoutManager.getHeight() - targetView.getMeasuredHeight()) / 2;
            if (null != mTargetVector) {
                int orientation = centerLinearLayoutManager.getOrientation();
                if(CenterLayoutManager.VERTICAL==orientation){
                    if (-1f == mTargetVector.y) {
                        //up
                        dy += offsetY;
                    } else if (1f == mTargetVector.y) {
                        //down
                        dy -= offsetY;
                    }
                } else {
                    if (-1f == mTargetVector.x) {
                        //up
                        dx += offsetX;
                    } else if (1f == mTargetVector.x) {
                        //down
                        dx -= offsetX;
                    }
                }
            }
            if (time > 0) {
                action.update(-dx, -dy, time, mDecelerateInterpolator);
            }
        }
    }

    @Override
    protected void updateActionForInterimTarget(Action action) {
        super.updateActionForInterimTarget(action);
    }
}
