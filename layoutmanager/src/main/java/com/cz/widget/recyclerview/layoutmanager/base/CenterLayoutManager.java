package com.cz.widget.recyclerview.layoutmanager.base;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * @author Created by cz
 * @date 2020-03-21 20:44
 * @email bingo110@126.com
 *
 * This ia a custom layout manager.
 * We abstract the fling event and layout the first position in the center of the screen.
 * For some special needs like ViewPager with margins. or the gallery.
 * It's a simple linear layout manager.
 * We have some good feathers.
 * 1. Layout the view in center. Base on this layout manager. We could easily implements ViewGroup like: ViewPager, Gallery, Wheel.
 * 2. Snap the view in center.
 * 3. Scroll the specific position in the center.
 * 4. Support infinite cycle.
 *
 * @see
 *
 */
public class CenterLayoutManager extends RecyclerView.LayoutManager {
    private static final int DIRECTION_START = -1;
    private static final int DIRECTION_END = 1;
    private static final PointF TEMP_POINT=new PointF();
    private static final int[] extraLayoutSpace=new int[2];
    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
    public static final int VERTICAL = OrientationHelper.VERTICAL;
    private RecyclerView recyclerView;
    /**
     * The center snap helper. After fling we will put the view in center.
     */
    private final CenterSnapHelper centerSnapHelper = new CenterSnapHelper();
    /**
     * Center smooth scroller. After we've found the target position. We keep the view in the center.
     * @see CenterLayoutManager#smoothScrollToPosition(RecyclerView, RecyclerView.State, int)
     */
    private CenterSmoothScroller centerSmoothScroller;
    /**
     * Orientation helper. We use this object support both horizontal and vertical layout.
     */
    private OrientationHelper orientationHelper;
    /**
     * Support the infinite cycle layout. Usually for advertisement or banner
     */
    private boolean infiniteCycle=false;
    /**
     * The minimum cycle view count.
     */
    private int minimumCycleCount=1;
    private LayoutState layoutState = new LayoutState();
    private int orientation = VERTICAL;

    public CenterLayoutManager(Context context){
        this(context,null,0,0);
    }

    public CenterLayoutManager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.centerSmoothScroller=new CenterSmoothScroller(context){
            @Nullable
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return smoothScrollComputeScrollVector(targetPosition);
            }

            @Override
            protected int calculateTimeForDeceleration(int dx) {
                return super.calculateTimeForDeceleration(dx);
            }
        };
        //Here we could support the attribute orientation in the XML layout.
        //@see R.styleable.RecyclerView_android_orientation
        Properties properties = RecyclerView.LayoutManager.getProperties(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(properties.orientation);
    }

    /**
     * Change the layout manager orientation.
     * @param orientation
     */
    public void setOrientation(int orientation){
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation:" + orientation);
        }
        removeAllViews();
        this.orientation=orientation;
        if (OrientationHelper.HORIZONTAL == orientation) {
            orientationHelper = OrientationHelper.createHorizontalHelper(this);
        } else if (OrientationHelper.VERTICAL == orientation) {
            orientationHelper = OrientationHelper.createVerticalHelper(this);
        }
        requestLayout();
    }

    /**
     * Return the orientation of the layout.
     * @see CenterLayoutManager#VERTICAL
     * @see CenterLayoutManager#HORIZONTAL
     * @return
     */
    public int getOrientation() {
        return orientation;
    }

    public OrientationHelper getOrientationHelper() {
        return orientationHelper;
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    /**
     * Loop all the view infinitely. Be careful. If there are less than three views.
     * We will create the view twice. Like this example: [A B]
     * If you want to loop this data. We will created each item twice.
     * The first time looks like this:[B,A,B] Then after you move to the next position. It will like this:[A,B,A]
     *
     * If you don't want to loop a list that will create more then once.
     * @see CenterLayoutManager#setMinimumCycleCount(int)
     * @param infiniteCycle
     */
    public void setInfiniteCycle(boolean infiniteCycle) {
        this.infiniteCycle = infiniteCycle;
        removeAllViews();
        requestLayout();
    }

    /**
     * The minimum cycle count.
     * @see CenterLayoutManager#hasMore(androidx.recyclerview.widget.RecyclerView.State)
     * @param minimumCycleCount
     */
    public void setMinimumCycleCount(int minimumCycleCount) {
        this.minimumCycleCount = minimumCycleCount;
    }

    public int getMinimumCycleCount(){
        return minimumCycleCount;
    }

    public boolean isInfiniteCycle() {
        return infiniteCycle;
    }

    /**
     * Attach the center snap helper
     * @see CenterSnapHelper
     * @param recyclerView
     */
    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        this.recyclerView=recyclerView;
        RecyclerView.OnFlingListener onFlingListener = recyclerView.getOnFlingListener();
        if(null==onFlingListener){
            centerSnapHelper.attachToRecyclerView(recyclerView);
        }
    }

    /**
     * The layout fling delegate. Because we use {@link androidx.recyclerview.widget.SnapHelper}
     * In this class. It will override the fling listener. So Here we delegate the fling listener.
     * @see RecyclerView#setOnFlingListener(RecyclerView.OnFlingListener)
     * @see SnapHelper# setupCallbacks()
     * @param layoutFlingDelegate
     */
    public void setLayoutFlingDelegate(RecyclerView.OnFlingListener layoutFlingDelegate) {
        this.centerSnapHelper.setOnFlingDelegate(layoutFlingDelegate);
    }

    /**
     * Here when we scroll over the bound.
     * @param layoutDirection
     * @param consumed
     * @param dy
     * @return
     */
    protected int scrollOver(int layoutDirection,int consumed,int dy){
        int absDy=Math.abs(dy);
        if(DIRECTION_START==layoutDirection){
            View childView=getChildAt(0);
            int start=orientationHelper.getDecoratedStart(childView);
            //Position up out of bound
            int startAvailable=(orientationHelper.getTotalSpace()-orientationHelper.getDecoratedMeasurement(childView))/2;
            if((startAvailable-start)<absDy){
                dy=start-startAvailable;
            }
        } else {
            int childCount = getChildCount();
            View view=getChildAt(childCount-1);
            int end=orientationHelper.getDecoratedEnd(view);
            //Position down out of bound
            int endAvailable=(orientationHelper.getTotalSpace()+orientationHelper.getDecoratedMeasurement(view))/2;
            if((end-endAvailable)<absDy){
                dy=end-endAvailable;
            }
        }
        return dy;
    }

    /**
     * Update the layout state by layout direction.
     * @param layoutDirection the layout direction.
     * @param requiredSpace
     */
    protected void updateLayoutState(RecyclerView.State state,int layoutDirection,int requiredSpace) {
        int scrollingOffset=0;
        calculateExtraLayoutSpace(state, extraLayoutSpace);
        if(layoutDirection== DIRECTION_END){
            int childCount = getChildCount();
            View view=getChildAt(childCount-1);
            if(null!=view){
                layoutState.itemDirection= DIRECTION_END;
                layoutState.position=getPosition(view) + layoutState.itemDirection;
                layoutState.layoutOffset = orientationHelper.getDecoratedEnd(view);
                //Layout from the bottom. so the scroll offset is the view's bottom minus the window height.
                scrollingOffset=orientationHelper.getDecoratedEnd(view) - orientationHelper.getEndAfterPadding();
            }
        } else {
            View childView = getChildAt(0);
            if(null!=childView){
                layoutState.itemDirection= DIRECTION_START;
                layoutState.position=getPosition(childView) + layoutState.itemDirection;
                layoutState.layoutOffset =orientationHelper.getDecoratedStart(childView);
                //Layout from the top. The first view's top plus the window top.
                scrollingOffset= -orientationHelper.getDecoratedStart(childView) + orientationHelper.getStartAfterPadding();
            }
        }
        layoutState.available= requiredSpace - scrollingOffset;
        layoutState.scrollingOffset = scrollingOffset;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        int itemCount = getItemCount();
        int childCount = getChildCount();
        if(0==itemCount||state.isPreLayout()){
            detachAndScrapAttachedViews(recycler);
            return;
        } else if(0==childCount){
            updateLayoutStateFromEnd();
            detachAndScrapAttachedViews(recycler);
        }  else if(!state.isPreLayout()){
            updateLayoutStateStructureChange();
            detachAndScrapAttachedViews(recycler);
        }
        calculateExtraLayoutSpace(state, extraLayoutSpace);
        //Fill the space
        fill(recycler,state,extraLayoutSpace);
        //If we infinite loop the layout manager
        if(infiniteCycle){
            //Fill from end to start. Cause we layout the first view in the center.
            //So there is a blank space at the top. Between the top to the first view.
            updateLayoutState(state,DIRECTION_START,0);
            fill(recycler,state,extraLayoutSpace);
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected void updateLayoutStateFromEnd() {
        layoutState.layoutOffset = 0;
        layoutState.position = 0;
        layoutState.layoutChildren = true;
        layoutState.itemDirection = DIRECTION_END;
        layoutState.available = orientationHelper.getTotalSpace();
    }

    protected void updateLayoutStateStructureChange() {
        View child = getChildAt(0);
        if(null!=child){
            layoutState.layoutChildren = false;
            layoutState.position = getPosition(child);
            layoutState.itemDirection = DIRECTION_END;
            layoutState.scrollingOffset = -orientationHelper.getDecoratedStart(child) + orientationHelper.getStartAfterPadding();
            layoutState.layoutOffset = orientationHelper.getDecoratedStart(child);
            layoutState.available = orientationHelper.getTotalSpace()-layoutState.layoutOffset;
        }
    }

    private int getExtraLayoutSpace(int[] extraLayoutSpace){
        if(HORIZONTAL==orientation){
            return Math.max(0,extraLayoutSpace[0]);
        } else {
            return Math.max(0,extraLayoutSpace[1]);
        }
    }

    /**
     * Fill the content. If we have available space. We fill it.
     * @see LayoutState#available
     * @param recycler
     * @param state
     * @return
     */
    public int fill(RecyclerView.Recycler recycler,RecyclerView.State state,int[] extraLayoutSpace){
        //The available space.
        int start=layoutState.available;
        if(0>layoutState.available){
            layoutState.scrollingOffset+=layoutState.available;
        }
        int extraSpace=getExtraLayoutSpace(extraLayoutSpace);
        //We recycler the view when they out of the window.
        recycleByLayoutState(recycler,extraSpace);
        int remainingSpace=layoutState.available;
        while(0<remainingSpace&&hasMore(state)){
            //Ask for a view.
            View view = nextView(recycler,state);
            //If It's the first time we layout the view.
            //We put the first view to the center of the window.
            if(layoutState.layoutChildren){
                int totalSpace=orientationHelper.getTotalSpace();
                //The available space start from here.
                layoutState.available=(totalSpace+orientationHelper.getDecoratedMeasurement(view))/2;
                layoutState.layoutOffset =totalSpace-layoutState.available;
                remainingSpace=layoutState.available;
                layoutState.layoutChildren=false;
            }
//            calculateExtraLayoutSpace(state,extraLayoutSpace);
            addAdapterView(view);
            int consumed= layoutChildView(view,recycler,state);
            layoutState.layoutOffset +=consumed*layoutState.itemDirection;
            layoutState.available-=consumed;
            remainingSpace-=consumed;
        }
        return start-layoutState.available;
    }

    @Override
    public boolean canScrollHorizontally(){
        return HORIZONTAL==orientation;
    }

    @Override
    public boolean canScrollVertically(){
        return VERTICAL==orientation;
    }

    @Override
    public int scrollHorizontallyBy(int dx,RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (orientation == VERTICAL) return 0;
        return scrollBy(dx,recycler,state);
    }

    @Override
    public int scrollVerticallyBy(int dy,RecyclerView.Recycler recycler,RecyclerView.State state){
        if (orientation == HORIZONTAL) return 0;
        return scrollBy(dy,recycler,state);
    }

    protected int scrollBy(int dy,RecyclerView.Recycler recycler,RecyclerView.State state){
        int childCount = getChildCount();
        if (childCount == 0 || dy == 0) {
            return 0;
        }
        int layoutDirection = dy > 0 ? DIRECTION_END : DIRECTION_START;
        int absDy = Math.abs(dy);
        //Update the layout state.
        updateLayoutState(state,layoutDirection,absDy);
        calculateExtraLayoutSpace(state, extraLayoutSpace);
        int consumed=layoutState.scrollingOffset + fill(recycler,state,extraLayoutSpace);
        //Here we deal with the situation when the view scroll out of the window
        //If you want to put a view int the center. Actually we scroll out of the top.
        int scrolled = absDy > consumed ? scrollOver(layoutDirection,consumed,dy) : dy;
        if(orientation == HORIZONTAL){
            offsetChildrenHorizontal(-scrolled);
        } else if(orientation == VERTICAL){
            offsetChildrenVertical(-scrolled);
        }
        return scrolled;
    }

    protected View nextView(RecyclerView.Recycler recycler,RecyclerView.State state) {
        View view;
        int itemCount = state.getItemCount();
        if(1==itemCount){
            view=recycler.getViewForPosition(itemCount-1);
        } else if(infiniteCycle&&0>layoutState.position){
            view=recycler.getViewForPosition(itemCount-Math.abs(layoutState.position)%itemCount);
        } else {
            view=recycler.getViewForPosition(layoutState.position%itemCount);
        }
        measureChildWithMargins(view,0,0);
        layoutState.position+=layoutState.itemDirection;
        return view;
    }

    protected void addAdapterView(View view){
        if (DIRECTION_END == layoutState.itemDirection) {
            addView(view);
        } else if (DIRECTION_START == layoutState.itemDirection) {
            addView(view, 0);
        }
    }

    protected boolean hasMore(RecyclerView.State state){
        int itemCount = getItemCount();
        if(infiniteCycle&&minimumCycleCount<itemCount){
            return true;
        } else {
            return 0 <= layoutState.position && layoutState.position < state.getItemCount();
        }
    }

    /**
     * Layout the child view.
     * @param view
     * @param recycler
     * @param state
     * @return
     */
    protected int layoutChildView(View view,RecyclerView.Recycler recycler,RecyclerView.State state){
        int consumed = orientationHelper.getDecoratedMeasurement(view);
        int left=getPaddingLeft();
        int top;
        int right;
        int bottom;
        if (orientation == VERTICAL) {
            right = left + orientationHelper.getDecoratedMeasurementInOther(view);
            if (layoutState.itemDirection == DIRECTION_START) {
                bottom = layoutState.layoutOffset;
                top = layoutState.layoutOffset - consumed;
            } else {
                top = layoutState.layoutOffset;
                bottom = layoutState.layoutOffset + consumed;
            }
        } else {
            top = getPaddingTop();
            bottom = top + orientationHelper.getDecoratedMeasurementInOther(view);
            if (layoutState.itemDirection == DIRECTION_START) {
                right = layoutState.layoutOffset;
                left = layoutState.layoutOffset - consumed;
            } else {
                left = layoutState.layoutOffset;
                right = layoutState.layoutOffset + consumed;
            }
        }
        layoutDecorated(view, left, top, right, bottom);
        return consumed;
    }

    /**
     * Recycle the view by the layout state.
     * @param recycler
     */
    protected void recycleByLayoutState(RecyclerView.Recycler recycler,int extraSpace) {
        if(layoutState.itemDirection== DIRECTION_START){
            //Recycle the view from the end.
            recycleViewsFromEnd(recycler, layoutState.scrollingOffset,extraSpace);
        } else if(layoutState.itemDirection== DIRECTION_END){
            //Recycle the view from the start.
            recycleViewsFromStart(recycler, layoutState.scrollingOffset,extraSpace);
        }
    }

    private void recycleViewsFromStart(RecyclerView.Recycler recycler,int dt,int extraSpace) {
        if (dt < 0) {
            return;
        }
        int limit = dt;
        int childCount = getChildCount();
        for (int i=0;i<childCount-1;i++) {
            View child = getChildAt(i);
            if (orientationHelper.getDecoratedEnd(child) > limit) {// stop here
                recycleChildren(recycler, 0, i);
                break;
            }
        }
    }

    private void recycleViewsFromEnd(RecyclerView.Recycler recycler,int dt,int extraSpace) {
        int childCount = getChildCount();
        if (dt < 0) {
            return;
        }
        int limit = orientationHelper.getEnd() - dt;
        for (int i=childCount-1;i>=0;i--) {
            View child = getChildAt(i);
            if (orientationHelper.getDecoratedStart(child) < limit) {// stop here
                recycleChildren(recycler, childCount - 1, i);
                break;
            }
        }
    }

    private void recycleChildren(RecyclerView.Recycler recycler,int startIndex,int endIndex) {
        if (endIndex > startIndex) {
            for (int i=endIndex-1;i>=0;i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        } else if(endIndex < startIndex){
            for (int i=startIndex;i>=endIndex + 1;i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        }
    }


    public int findCurrentItemPosition() {
        int childCount = getChildCount();
        View child = findOneVisibleChild(0, childCount);
        return child == null ? RecyclerView.NO_POSITION : getPosition(child);
    }

    @Nullable
    View findOneVisibleChild(int fromIndex,int toIndex) {
        int next=toIndex > fromIndex?1:-1;
        int centerY = orientationHelper.getEnd() / 2;
        int i = fromIndex;
        while (i != toIndex) {
            View child = getChildAt(i);
            int childStart = orientationHelper.getDecoratedStart(child);
            int childEnd = orientationHelper.getDecoratedEnd(child);
            if (childStart<=centerY&&centerY<=childEnd) {
                return child;
            }
            i += next;
        }
        return null;
    }

    public int findFirstVisibleItemPosition() {
        int childCount = getChildCount();
        View child = findOneVisibleChild(0, childCount, false, true);
        return child == null ? RecyclerView.NO_POSITION : getPosition(child);
    }

    public int findLastVisibleItemPosition() {
        int childCount = getChildCount();
        View child = findOneVisibleChild(childCount - 1, -1, false, true);
        return child == null ? RecyclerView.NO_POSITION : getPosition(child);
    }

    @Nullable
    public View findOneVisibleChild(int fromIndex,int toIndex,boolean completelyVisible,
                                     boolean acceptPartiallyVisible){
        int start = orientationHelper.getStartAfterPadding();
        int end = orientationHelper.getEndAfterPadding();
        int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible=null;
        int i = fromIndex;
        while (i != toIndex) {
            View child = getChildAt(i);
            int childStart = orientationHelper.getDecoratedStart(child);
            int childEnd = orientationHelper.getDecoratedEnd(child);
            if (childStart < end && childEnd > start) {
                if (completelyVisible) {
                    if (childStart >= start && childEnd <= end) {
                        return child;
                    } else if (acceptPartiallyVisible && partiallyVisible == null) {
                        partiallyVisible = child;
                    }
                } else {
                    return child;
                }
            }
            i += next;
        }
        return partiallyVisible;
    }

    /**
     * We don't have to scroll to the position immediately.
     * Here we call the function {@link #scrollToPosition(RecyclerView, int)}
     * @param position
     */
    @Override
    public void scrollToPosition(int position) {
        if(null!=recyclerView){
            scrollToPosition(recyclerView,position);
        }
    }

    /**
     * Scroll to a position smoothly. Since we are a center layout manager. Maybe loop the view infinitely.
     * We have to consider Where are should move. Like the position both in the top and bottom. That's true...
     * From the top, you could found it. Either from the bottom. So we should found the shortest way.
     * @see RecyclerView#smoothScrollToPosition(int)
     * @param recyclerView
     * @param state
     * @param position
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        scrollToPosition(recyclerView, position);
    }

    private void scrollToPosition(RecyclerView recyclerView, int position) {
        int itemCount=getItemCount();
        int firstVisibleItemPosition = findFirstVisibleItemPosition();
        int lastVisibleItemPosition = findLastVisibleItemPosition();
        //If the first visible position more than the last visible position.
        //That's means we are in an infinite loop.
        if(firstVisibleItemPosition>lastVisibleItemPosition){
            if(firstVisibleItemPosition <= position && position<=itemCount||0 <= position && position<=lastVisibleItemPosition){
                smoothScrollToScreenPosition(recyclerView,position);
            } else {
                //We are out of the screen.
                smoothScrollToOutPosition(recyclerView,position);
            }
        } else if (firstVisibleItemPosition <= position && position <= lastVisibleItemPosition) {
            smoothScrollToScreenPosition(recyclerView,position);
        } else {
            smoothScrollToOutPosition(recyclerView,position);
        }
    }

    protected void smoothScrollToScreenPosition(RecyclerView recyclerView,int position){
        View childView = findViewByPosition(position);
        if(null!=childView){
            smoothScrollToView(recyclerView,childView);
        }
    }

    public void smoothScrollToView(View childView){
        if(null!=recyclerView){
            smoothScrollToView(recyclerView,childView);
        }
    }

    public void smoothScrollToView(RecyclerView recyclerView,View childView){
        int offset = orientationHelper.getDecoratedStart(childView) + orientationHelper.getDecoratedMeasurement(childView) / 2 - orientationHelper.getEnd() / 2;
        if (canScrollHorizontally()) {
            recyclerView.smoothScrollBy(offset, 0);
        } else if (canScrollVertically()) {
            recyclerView.smoothScrollBy(0, offset);
        }
    }

    protected void smoothScrollToOutPosition(RecyclerView recyclerView,int position){
        centerSmoothScroller.setTargetPosition(position);
        startSmoothScroll(centerSmoothScroller);
    }

    protected PointF smoothScrollComputeScrollVector(int targetPosition){
        int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        int direction;
        int itemCount = getItemCount();
        if(infiniteCycle){
            direction=findCurrentItemPosition()<itemCount/2 ? DIRECTION_START : DIRECTION_END;
        } else {
            direction = targetPosition < findFirstVisibleItemPosition() ? DIRECTION_START : DIRECTION_END;
        }
        if(orientation== HORIZONTAL){
            TEMP_POINT.set(direction,0f);
        } else {
            TEMP_POINT.set(0f, direction);
        }
        return TEMP_POINT;
    }

    protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state, @NonNull int[] extraLayoutSpace) {
        int extraLayoutSpaceStart = 0;
        int extraLayoutSpaceEnd = 0;
        // If calculateExtraLayoutSpace is not overridden, call the
        // deprecated getExtraLayoutSpace for backwards compatibility
        int extraScrollSpace = getExtraLayoutSpace(state);
        if (layoutState.itemDirection == DIRECTION_START) {
            extraLayoutSpaceStart = extraScrollSpace;
        } else {
            extraLayoutSpaceEnd = extraScrollSpace;
        }
        extraLayoutSpace[0] = extraLayoutSpaceStart;
        extraLayoutSpace[1] = extraLayoutSpaceEnd;
    }

    @Override
    public int computeHorizontalScrollRange(@NonNull RecyclerView.State state) {
        return computeScrollRange(state);
    }

    @Override
    public int computeHorizontalScrollOffset(@NonNull RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    @Override
    public int computeHorizontalScrollExtent(@NonNull RecyclerView.State state) {
        return computeScrollExtent(state);
    }

    @Override
    public int computeVerticalScrollRange(@NonNull RecyclerView.State state) {
        return computeScrollRange(state);
    }

    @Override
    public int computeVerticalScrollOffset(@NonNull RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    @Override
    public int computeVerticalScrollExtent(@NonNull RecyclerView.State state) {
        return computeScrollExtent(state);
    }

    private int computeScrollOffset(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        int firstVisibleItemPosition = findFirstVisibleItemPosition();
        int lastVisibleItemPosition = findLastVisibleItemPosition();
        View firstVisibleView = findViewByPosition(firstVisibleItemPosition);
        View lastVisibleView = findViewByPosition(lastVisibleItemPosition);
        return ScrollbarHelper.computeScrollOffset(state, orientationHelper,
                firstVisibleView, lastVisibleView, this, true, false);
    }

    private int computeScrollExtent(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        int firstVisibleItemPosition = findFirstVisibleItemPosition();
        int lastVisibleItemPosition = findLastVisibleItemPosition();
        View firstVisibleView = findViewByPosition(firstVisibleItemPosition);
        View lastVisibleView = findViewByPosition(lastVisibleItemPosition);
        return ScrollbarHelper.computeScrollExtent(state, orientationHelper,
                firstVisibleView, lastVisibleView, this,  true);
    }

    private int computeScrollRange(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        int firstVisibleItemPosition = findFirstVisibleItemPosition();
        int lastVisibleItemPosition = findLastVisibleItemPosition();
        View firstVisibleView = findViewByPosition(firstVisibleItemPosition);
        View lastVisibleView = findViewByPosition(lastVisibleItemPosition);
        return ScrollbarHelper.computeScrollRange(state, orientationHelper,
                firstVisibleView,lastVisibleView,
                this, true);
    }


    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (state.hasTargetScrollPosition()) {
            return orientationHelper.getTotalSpace();
        } else {
            return 0;
        }
    }

    public interface OnSelectPositionChangedListener {
        void onSelectPositionChanged(@Nullable View view,int position,int lastPosition);
    }

    private class LayoutState{
        static final int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;
        /**
         * The available space
         */
        int available=0;
        /**
         * Current layout offset.
         */
        int layoutOffset =0;
        /**
         * The scroll offset
         */
        int scrollingOffset =0;
        /**
         * If it's the first time to layout.
         * Here we use this boolean value to layout the child view in the center of the window.
         */
        boolean layoutChildren=false;
        /**
         * Current position.
         */
        int position=0;
        /**
         * The direction of the layout.
         */
        int itemDirection= DIRECTION_END;
    }
}
