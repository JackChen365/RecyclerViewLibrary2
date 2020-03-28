package com.cz.widget.recyclerview.layoutmanager.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * Created by cz
 * @date 2020-03-21 20:15
 * @email bingo110@126.com
 *
 */
public class CenterSnapHelper extends SnapHelper {
    private static final int[] snapDistance=new int[2];
    private RecyclerView.OnFlingListener flingDelegate;

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        snapDistance[0]=0;
        snapDistance[1]=0;
        if(null != targetView&&layoutManager instanceof CenterLayoutManager){
            CenterLayoutManager centerLayoutManager = (CenterLayoutManager) layoutManager;
            int orientation = centerLayoutManager.getOrientation();
            OrientationHelper orientationHelper = centerLayoutManager.getOrientationHelper();
            if(CenterLayoutManager.HORIZONTAL==orientation){
                snapDistance[0] = orientationHelper.getDecoratedStart(targetView) +
                        orientationHelper.getDecoratedMeasurement(targetView) / 2 - orientationHelper.getEnd() / 2;
            } else if(CenterLayoutManager.VERTICAL==orientation){
                snapDistance[1] = orientationHelper.getDecoratedStart(targetView) +
                        orientationHelper.getDecoratedMeasurement(targetView) / 2 - orientationHelper.getEnd() / 2;
            }
        }
        return snapDistance;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        int childCount = layoutManager.getChildCount();
        return findOneVisibleChild(layoutManager,0,childCount);
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        final View currentView = findSnapView(layoutManager);
        if (currentView == null) {
            return RecyclerView.NO_POSITION;
        }
        final int currentPosition = layoutManager.getPosition(currentView);
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }
        return currentPosition;
    }

    @Nullable
    private View findOneVisibleChild(RecyclerView.LayoutManager layoutManager,int fromIndex,int toIndex) {
        if(!(layoutManager instanceof CenterLayoutManager)){
            return null;
        } else {
            CenterLayoutManager centerLayoutManager = (CenterLayoutManager) layoutManager;
            int next=toIndex > fromIndex?1:-1;
            OrientationHelper orientationHelper = centerLayoutManager.getOrientationHelper();
            int centerY = orientationHelper.getEnd() / 2;
            int i = fromIndex;
            while (i != toIndex) {
                View child = layoutManager.getChildAt(i);
                int childStart = orientationHelper.getDecoratedStart(child);
                int childEnd = orientationHelper.getDecoratedEnd(child);
                if (childStart<=centerY&&centerY<=childEnd) {
                    return child;
                }
                i += next;
            }
            return null;
        }
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        if(null!=flingDelegate){
            return flingDelegate.onFling(velocityX,velocityY);
        } else {
            return super.onFling(velocityX, velocityY);
        }
    }

    public void setOnFlingDelegate(RecyclerView.OnFlingListener listener){
        this.flingDelegate=listener;
    }
}
