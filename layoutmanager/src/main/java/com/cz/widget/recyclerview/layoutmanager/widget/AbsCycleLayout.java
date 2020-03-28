package com.cz.widget.recyclerview.layoutmanager.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.layoutmanager.adapter.StatefulAdapter;
import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * @author Created by cz
 * @date 2020-03-22 15:00
 * @email bingo110@126.com
 */
public abstract class AbsCycleLayout extends ViewGroup {
    public static final int ORIENTATION_HORIZONTAL = RecyclerView.HORIZONTAL;
    public static final int ORIENTATION_VERTICAL = RecyclerView.VERTICAL;
    /**
     * Indicates that the AbsCycleLayout is in an idle, settled state. The current page
     * is fully in view and no animation is in progress.
     */
    public static final int SCROLL_STATE_IDLE = 0;

    /**
     * Indicates that the AbsCycleLayout is currently being dragged by the user, or programmatically
     * via fake drag functionality.
     */
    public static final int SCROLL_STATE_DRAGGING = 1;

    /**
     * Indicates that the AbsCycleLayout is in the process of settling to a final position.
     */
    public static final int SCROLL_STATE_SETTLING = 2;
    /**
     * Value to indicate that the default caching mechanism of RecyclerView should be used instead
     * of explicitly prefetch and retain pages to either side of the current page.
     * @see #setOffscreenPageLimit(int)
     */
    public static final int OFFSCREEN_PAGE_LIMIT_DEFAULT = -1;

    @IntDef({SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING})
    public @interface ScrollState {
    }

    @IntDef({OFFSCREEN_PAGE_LIMIT_DEFAULT})
    @IntRange(from = 1)
    public @interface OffscreenPageLimit {
    }
    private final Rect tmpContainerRect = new Rect();
    private final Rect tmpChildRect = new Rect();
    /**
     * @see #onSaveInstanceState()
     * @see #onRestoreInstanceState(Parcelable)
     */
    private int pendingCurrentItem = NO_POSITION;
    private Parcelable pendingAdapterState;

    private CompositeOnPageChangeCallback externalPageChangeCallbacks = new CompositeOnPageChangeCallback(3);
    private CompositeOnPageChangeCallback pageChangeEventDispatcher;
    private @OffscreenPageLimit int mOffscreenPageLimit = OFFSCREEN_PAGE_LIMIT_DEFAULT;
    RecyclerView recyclerView;
    CenterLayoutManager layoutManager;
    private PagerSnapHelper pagerSnapHelper;
    private PageTransformerAdapter pageTransformerAdapter;
    private RecyclerView.ItemAnimator savedItemAnimator = null;
    private boolean savedItemAnimatorPresent = false;
    private ScrollEventAdapter scrollEventAdapter;
    private boolean userInputEnabled=true;
    private FakeDrag fakeDragger;
    private int currentItem;
    private boolean currentItemDirty = false;

    public AbsCycleLayout(Context context) {
        this(context,null,0);
    }

    public AbsCycleLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AbsCycleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        recyclerView=createRecyclerView(context);
        recyclerView.setId(ViewCompat.generateViewId());
        recyclerView.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);

        layoutManager=createLayoutManager(context,attrs,defStyleAttr);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Create FakeDrag before attaching PagerSnapHelper, same reason as above
        scrollEventAdapter = new ScrollEventAdapter(this);
        fakeDragger = new FakeDrag(this, scrollEventAdapter, recyclerView);
        pagerSnapHelper = new PagerSnapHelperImpl();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        // Add mScrollEventAdapter after attaching mPagerSnapHelper to mRecyclerView, because we
        // don't want to respond on the events sent out during the attach process
        recyclerView.addOnScrollListener(scrollEventAdapter);

        pageChangeEventDispatcher = new CompositeOnPageChangeCallback(3);
        scrollEventAdapter.setOnPageChangeCallback(pageChangeEventDispatcher);

        // Callback that updates mCurrentItem after swipes. Also triggered in other cases, but in
        // all those cases mCurrentItem will only be overwritten with the same value.
        final OnPageChangeCallback currentItemUpdater = new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (currentItem != position) {
                    currentItem = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    updateCurrentItem();
                }
            }
        };
        // Prevents focus from remaining on a no-longer visible page
        final OnPageChangeCallback focusClearer = new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                clearFocus();
                if (hasFocus()) { // if clear focus did not succeed
                    recyclerView.requestFocus(View.FOCUS_FORWARD);
                }
            }
        };
        // Add currentItemUpdater before mExternalPageChangeCallbacks, because we need to update
        // internal state first
        pageChangeEventDispatcher.addOnPageChangeCallback(currentItemUpdater);
        pageChangeEventDispatcher.addOnPageChangeCallback(focusClearer);
        // Allow a11y to register its listeners after currentItemUpdater (so it has the
        // right data).
        pageChangeEventDispatcher.addOnPageChangeCallback(externalPageChangeCallbacks);

        // Add mPageTransformerAdapter after mExternalPageChangeCallbacks, because page transform
        // events must be fired after scroll events
        pageTransformerAdapter = new PageTransformerAdapter(layoutManager);
        pageChangeEventDispatcher.addOnPageChangeCallback(pageTransformerAdapter);

        attachViewToParent(recyclerView, 0, recyclerView.getLayoutParams());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(recyclerView, widthMeasureSpec, heightMeasureSpec);
        int width = recyclerView.getMeasuredWidth();
        int height = recyclerView.getMeasuredHeight();
        int childState = recyclerView.getMeasuredState();

        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();

        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, childState),
                resolveSizeAndState(height, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = recyclerView.getMeasuredWidth();
        int height = recyclerView.getMeasuredHeight();

        tmpContainerRect.left = getPaddingLeft();
        tmpContainerRect.right = r - l - getPaddingRight();
        tmpContainerRect.top = getPaddingTop();
        tmpContainerRect.bottom = b - t - getPaddingBottom();

        Gravity.apply(Gravity.TOP | Gravity.START, width, height, tmpContainerRect, tmpChildRect);
        recyclerView.layout(tmpChildRect.left, tmpChildRect.top, tmpChildRect.right, tmpChildRect.bottom);
        if (currentItemDirty) {
            updateCurrentItem();
        }
    }

    /** Updates {@link #currentItem} based on what is currently visible in the viewport. */
    void updateCurrentItem() {
        if (pagerSnapHelper == null) {
            throw new IllegalStateException("Design assumption violated.");
        }

        View snapView = pagerSnapHelper.findSnapView(layoutManager);
        if (snapView == null) {
            return; // nothing we can do
        }
        int snapPosition = layoutManager.getPosition(snapView);

        if (snapPosition != currentItem && getScrollState() == SCROLL_STATE_IDLE) {
            /** TODO: revisit if push to {@link ScrollEventAdapter} / separate component */
            pageChangeEventDispatcher.onPageSelected(snapPosition);
        }
        currentItemDirty = false;
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        recyclerView.setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter(){
        return recyclerView.getAdapter();
    }

    public CenterLayoutManager getLayoutManager(){
        return layoutManager;
    }

    public void setContentPadding(int left,int top,int right,int bottom){
        recyclerView.setPadding(left,top,right,bottom);
        recyclerView.setClipToPadding(false);
    }

    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor){
        recyclerView.addItemDecoration(decor);
    }

    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor, int index){
        recyclerView.addItemDecoration(decor,index);
    }

    public void removeItemDecoration(@NonNull RecyclerView.ItemDecoration decor){
        recyclerView.removeItemDecoration(decor);
    }

    public void removeItemDecorationAt(int index){
        recyclerView.removeItemDecorationAt(index);
    }

    private class PagerSnapHelperImpl extends PagerSnapHelper {
        PagerSnapHelperImpl() {
        }

        @Nullable
        @Override
        public View findSnapView(RecyclerView.LayoutManager layoutManager) {
            // When interrupting a smooth scroll with a fake drag, we stop RecyclerView's scroll
            // animation, which fires a scroll state change to IDLE. PagerSnapHelper then kicks in
            // to snap to a page, which we need to prevent here.
            // Simplifying that case: during a fake drag, no snapping should occur.
            return isFakeDragging() ? null : super.findSnapView(layoutManager);
        }
    }

    protected CenterLayoutManager createLayoutManager(Context context, AttributeSet attrs, int defStyleAttr){
        return new CenterLayoutManager(context);
    }

    protected RecyclerView createRecyclerView(Context context){
        return new RecyclerViewImpl(context);
    }

    /**
     * Slightly modified RecyclerView to get ViewPager behavior in accessibility and to
     * enable/disable user scrolling.
     */
    private class RecyclerViewImpl extends RecyclerView {
        RecyclerViewImpl(@NonNull Context context) {
            super(context);
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
        layoutManager.setInfiniteCycle(infiniteCycle);
    }

    /**
     * The minimum cycle count.
     * @param minimumCycleCount
     */
    public void setMinimumCycleCount(int minimumCycleCount) {
        layoutManager.setMinimumCycleCount(minimumCycleCount);
    }

    public int getMinimumCycleCount(){
        return layoutManager.getMinimumCycleCount();
    }

    public int getItemCount(){
        return layoutManager.getItemCount();
    }

    public boolean isInfiniteCycle() {
        return layoutManager.isInfiniteCycle();
    }


    public int getOrientation(){
        return layoutManager.getOrientation();
    }

    public boolean isHorizontal(){
        return layoutManager.getOrientation() == ORIENTATION_HORIZONTAL;
    }

    public boolean isVertical(){
        return layoutManager.getOrientation() == ORIENTATION_VERTICAL;
    }

    /**
     * Returns the current scroll state of the AbsCycleLayout. Returned value is one of can be one of
     * {@link #SCROLL_STATE_IDLE}, {@link #SCROLL_STATE_DRAGGING} or {@link #SCROLL_STATE_SETTLING}.
     *
     * @return The scroll state that was last dispatched to {@link
     *         OnPageChangeCallback#onPageScrollStateChanged(int)}
     */
    @ScrollState
    public int getScrollState() {
        return scrollEventAdapter.getScrollState();
    }

    /**
     * Start a fake drag of the pager.
     *
     * <p>A fake drag can be useful if you want to synchronize the motion of the AbsCycleLayout with the
     * touch scrolling of another view, while still letting the AbsCycleLayout control the snapping
     * motion and fling behavior. (e.g. parallax-scrolling tabs.) Call {@link #fakeDragBy(float)} to
     * simulate the actual drag motion. Call {@link #endFakeDrag()} to complete the fake drag and
     * fling as necessary.
     *
     * <p>A fake drag can be interrupted by a real drag. From that point on, all calls to {@code
     * fakeDragBy} and {@code endFakeDrag} will be ignored until the next fake drag is started by
     * calling {@code beginFakeDrag}. If you need the AbsCycleLayout to ignore touch events and other
     * user input during a fake drag, use {@link #setUserInputEnabled(boolean)}. If a real or fake
     * drag is already in progress, this method will return {@code false}.
     *
     * @return {@code true} if the fake drag began successfully, {@code false} if it could not be
     *         started
     *
     * @see #fakeDragBy(float)
     * @see #endFakeDrag()
     * @see #isFakeDragging()
     */
    public boolean beginFakeDrag() {
        return fakeDragger.beginFakeDrag();
    }

    /**
     * Fake drag by an offset in pixels. You must have called {@link #beginFakeDrag()} first. Drag
     * happens in the direction of the orientation. Positive offsets will drag to the previous page,
     * negative values to the next page, with one exception: if layout direction is set to RTL and
     * the AbsCycleLayout's orientation is horizontal, then the behavior will be inverted. This matches
     * the deltas of touch events that would cause the same real drag.
     *
     * <p>If the pager is not in the fake dragging state anymore, it ignores this call and returns
     * {@code false}.
     *
     * @param offsetPxFloat Offset in pixels to drag by
     * @return {@code true} if the fake drag was executed. If {@code false} is returned, it means
     *         there was no fake drag to end.
     *
     * @see #beginFakeDrag()
     * @see #endFakeDrag()
     * @see #isFakeDragging()
     */
    public boolean fakeDragBy(@SuppressLint("SupportAnnotationUsage") @Px float offsetPxFloat) {
        return fakeDragger.fakeDragBy(offsetPxFloat);
    }

    /**
     * End a fake drag of the pager.
     *
     * @return {@code true} if the fake drag was ended. If {@code false} is returned, it means there
     *         was no fake drag to end.
     *
     * @see #beginFakeDrag()
     * @see #fakeDragBy(float)
     * @see #isFakeDragging()
     */
    public boolean endFakeDrag() {
        return fakeDragger.endFakeDrag();
    }

    /**
     * Returns {@code true} if a fake drag is in progress.
     *
     * @return {@code true} if currently in a fake drag, {@code false} otherwise.
     * @see #beginFakeDrag()
     * @see #fakeDragBy(float)
     * @see #endFakeDrag()
     */
    public boolean isFakeDragging() {
        return fakeDragger.isFakeDragging();
    }

    /**
     * Snaps the AbsCycleLayout to the closest page
     */
    void snapToPage() {
        // Method copied from PagerSnapHelper#snapToTargetExistingView
        // When fixing something here, make sure to update that method as well
        View view = pagerSnapHelper.findSnapView(layoutManager);
        if (view == null) {
            return;
        }
        int[] snapDistance = pagerSnapHelper.calculateDistanceToFinalSnap(layoutManager, view);
        //noinspection ConstantConditions
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            recyclerView.smoothScrollBy(snapDistance[0], snapDistance[1]);
        }
    }

    /**
     * Enable or disable user initiated scrolling. This includes touch input (scroll and fling
     * gestures) and accessibility input. Disabling keyboard input is not yet supported. When user
     * initiated scrolling is disabled, programmatic scrolls through {@link #setCurrentItem(int)}
     * still work. By default, user initiated scrolling is enabled.
     * @param enabled {@code true} to allow user initiated scrolling, {@code false} to block user
     *        initiated scrolling
     * @see #isUserInputEnabled()
     */
    public void setUserInputEnabled(boolean enabled) {
        userInputEnabled = enabled;
    }

    /**
     * Returns if user initiated scrolling between pages is enabled. Enabled by default.
     *
     * @return {@code true} if users can scroll the AbsCycleLayout, {@code false} otherwise
     * @see #setUserInputEnabled(boolean)
     */
    public boolean isUserInputEnabled() {
        return userInputEnabled;
    }

    /**
     * <p>Set the number of pages that should be retained to either side of the currently visible
     * page(s). Pages beyond this limit will be recreated from the adapter when needed. Set this to
     * {@link #OFFSCREEN_PAGE_LIMIT_DEFAULT} to use RecyclerView's caching strategy. The given value
     * must either be larger than 0, or {@code #OFFSCREEN_PAGE_LIMIT_DEFAULT}.</p>
     *
     * <p>Pages within {@code limit} pages away from the current page are created and added to the
     * view hierarchy, even though they are not visible on the screen. Pages outside this limit will
     * be removed from the view hierarchy, but the {@code ViewHolder}s will be recycled as usual by
     * {@link RecyclerView}.</p>
     *
     * <p>This is offered as an optimization. If you know in advance the number of pages you will
     * need to support or have lazy-loading mechanisms in place on your pages, tweaking this setting
     * can have benefits in perceived smoothness of paging animations and interaction. If you have a
     * small number of pages (3-4) that you can keep active all at once, less time will be spent in
     * layout for newly created view subtrees as the user pages back and forth.</p>
     *
     * <p>You should keep this limit low, especially if your pages have complex layouts. By default
     * it is set to {@code OFFSCREEN_PAGE_LIMIT_DEFAULT}.</p>
     *
     * @param limit How many pages will be kept offscreen on either side. Valid values are all
     *        values {@code >= 1} and {@link #OFFSCREEN_PAGE_LIMIT_DEFAULT}
     * @throws IllegalArgumentException If the given limit is invalid
     * @see #getOffscreenPageLimit()
     */
    public void setOffscreenPageLimit(@OffscreenPageLimit int limit) {
        if (limit < 1 && limit != OFFSCREEN_PAGE_LIMIT_DEFAULT) {
            throw new IllegalArgumentException(
                    "Offscreen page limit must be OFFSCREEN_PAGE_LIMIT_DEFAULT or a number > 0");
        }
        mOffscreenPageLimit = limit;
        // Trigger layout so prefetch happens through getExtraLayoutSize()
        recyclerView.requestLayout();
    }

    /**
     * Returns the number of pages that will be retained to either side of the current page in the
     * view hierarchy in an idle state. Defaults to {@link #OFFSCREEN_PAGE_LIMIT_DEFAULT}.
     *
     * @return How many pages will be kept offscreen on either side
     * @see #setOffscreenPageLimit(int)
     */
    @OffscreenPageLimit
    public int getOffscreenPageLimit() {
        return mOffscreenPageLimit;
    }


    /**
     * Set the currently selected page. If {@code smoothScroll = true}, will perform a smooth
     * animation from the current item to the new item. Silently ignored if the adapter is not set
     * or empty. Clamps item to the bounds of the adapter.
     *
     * @param item Item index to select
     */
    public void setCurrentItem(int item) {
        if (isFakeDragging()) {
            throw new IllegalStateException("Cannot change current item when AbsCycleLayout is fake " + "dragging");
        }
        recyclerView.smoothScrollToPosition(item);
    }

    /**
     * Returns the currently selected page. If no page can sensibly be selected because there is no
     * adapter or the adapter is empty, returns 0.
     *
     * @return Currently selected page
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * Callback interface for responding to changing state of the selected page.
     */
    public abstract static class OnPageChangeCallback {
        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position Position index of the first page currently being displayed.
         *                 Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        public void onPageScrolled(int position, float positionOffset,
                                   @Px int positionOffsetPixels) {
        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        public void onPageSelected(int position) {
        }

        /**
         * Called when the scroll state changes. Useful for discovering when the user begins
         * dragging, when a fake drag is started, when the pager is automatically settling to the
         * current page, or when it is fully stopped/idle. {@code state} can be one of {@link
         * #SCROLL_STATE_IDLE}, {@link #SCROLL_STATE_DRAGGING} or {@link #SCROLL_STATE_SETTLING}.
         */
        public void onPageScrollStateChanged(@ScrollState int state) {
        }
    }

    public boolean isRtl() {
        return layoutManager.getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    protected int getPageSize() {
        final RecyclerView rv = recyclerView;
        return getOrientation() == LinearLayoutManager.HORIZONTAL
                ? rv.getWidth() - rv.getPaddingLeft() - rv.getPaddingRight()
                : rv.getHeight() - rv.getPaddingTop() - rv.getPaddingBottom();
    }

    /**
     * Sets the orientation of the AbsCycleLayout.
     *
     * @param orientation {@link CenterLayoutManager#HORIZONTAL} or {@link CenterLayoutManager#VERTICAL}
     */
    public void setOrientation(@RecyclerView.Orientation int orientation) {
        layoutManager.setOrientation(orientation);
    }

    /**
     * Add a callback that will be invoked whenever the page changes or is incrementally
     * scrolled. See {@link OnPageChangeCallback}.
     *
     * <p>Components that add a callback should take care to remove it when finished.
     *
     * @param callback callback to add
     */
    public void addOnPageChangeCallback(@NonNull OnPageChangeCallback callback) {
        externalPageChangeCallbacks.addOnPageChangeCallback(callback);
    }

    /**
     * Remove a callback that was previously added via
     * {@link #addOnPageChangeCallback(OnPageChangeCallback)}.
     *
     * @param callback callback to remove
     */
    public void removeOnPageChangeCallback(@NonNull OnPageChangeCallback callback) {
        externalPageChangeCallbacks.removeOnPageChangeCallback(callback);
    }

    /**
     * Sets a {@link PageTransformer} that will be called for each attached page whenever the
     * scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding behavior.
     * <p>
     * Note: setting a {@link PageTransformer} disables data-set change animations to prevent
     * conflicts between the two animation systems. Setting a {@code null} transformer will restore
     * data-set change animations.
     *
     * @param transformer PageTransformer that will modify each page's animation properties
     *
     * @see MarginPageTransformer
     * @see CompositePageTransformer
     */
    public void setPageTransformer(@Nullable PageTransformer transformer) {
        if (transformer != null) {
            if (!savedItemAnimatorPresent) {
                savedItemAnimator = recyclerView.getItemAnimator();
                savedItemAnimatorPresent = true;
            }
            recyclerView.setItemAnimator(null);
        } else {
            if (savedItemAnimatorPresent) {
                recyclerView.setItemAnimator(savedItemAnimator);
                savedItemAnimator = null;
                savedItemAnimatorPresent = false;
            }
        }

        // TODO: add support for reverseDrawingOrder: b/112892792
        // TODO: add support for pageLayerType: b/112893074
        if (transformer == pageTransformerAdapter.getPageTransformer()) {
            return;
        }
        pageTransformerAdapter.setPageTransformer(transformer);
        requestTransform();
    }

    /**
     * Trigger a call to the registered {@link PageTransformer PageTransformer}'s {@link
     * PageTransformer#transformPage(View, float) transformPage} method. Call this when something
     * has changed which has invalidated the transformations defined by the {@code PageTransformer}
     * that did not trigger a page scroll.
     */
    public void requestTransform() {
        if (pageTransformerAdapter.getPageTransformer() == null) {
            return;
        }
        double relativePosition = scrollEventAdapter.getRelativeScrollPosition();
        int position = (int) relativePosition;
        float positionOffset = (float) (relativePosition - position);
        int positionOffsetPx = Math.round(getPageSize() * positionOffset);
        pageTransformerAdapter.onPageScrolled(position, positionOffset, positionOffsetPx);
    }

    /**
     * A PageTransformer is invoked whenever a visible/attached page is scrolled.
     * This offers an opportunity for the application to apply a custom transformation
     * to the page views using animation properties.
     */
    public interface PageTransformer {

        /**
         * Apply a property transformation to the given page.
         *
         * @param page Apply the transformation to this page
         * @param fraction Position of page relative to the current front-and-center
         *                 position of the pager. 0 is front and center. 1 is one full
         *                 page position to the right, and -2 is two pages to the left.
         *                 Minimum / maximum observed values depend on how many pages we keep
         *                 attached, which depends on offscreenPageLimit.
         *
         */
        void transformPage(@NonNull View page, float fraction);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.mRecyclerViewId = recyclerView.getId();
        ss.mCurrentItem = pendingCurrentItem == NO_POSITION ? currentItem : pendingCurrentItem;

        if (pendingAdapterState != null) {
            ss.mAdapterState = pendingAdapterState;
        } else {
            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if (adapter instanceof StatefulAdapter) {
                ss.mAdapterState = ((StatefulAdapter) adapter).saveState();
            }
        }

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        pendingCurrentItem = ss.mCurrentItem;
        pendingAdapterState = ss.mAdapterState;
    }

    private void restorePendingState() {
        if (pendingCurrentItem == NO_POSITION) {
            // No state to restore, or state is already restored
            return;
        }
        RecyclerView.Adapter<?> adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        if (pendingAdapterState != null) {
            if (adapter instanceof StatefulAdapter) {
                ((StatefulAdapter) adapter).restoreState(pendingAdapterState);
            }
            pendingAdapterState = null;
        }
        // Now we have an adapter, we can clamp the pending current item and set it
        currentItem = Math.max(0, Math.min(pendingCurrentItem, adapter.getItemCount() - 1));
        pendingCurrentItem = NO_POSITION;
        recyclerView.scrollToPosition(currentItem);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        // RecyclerView changed an id, so we need to reflect that in the saved state
        Parcelable state = container.get(getId());
        if (state instanceof SavedState) {
            final int previousRvId = ((SavedState) state).mRecyclerViewId;
            final int currentRvId = recyclerView.getId();
            container.put(currentRvId, container.get(previousRvId));
            container.remove(previousRvId);
        }
        super.dispatchRestoreInstanceState(container);
        // State of AbsCycleLayout and its child (RecyclerView) has been restored now
        restorePendingState();
    }


    static class SavedState extends BaseSavedState {
        int mRecyclerViewId;
        int mCurrentItem;
        Parcelable mAdapterState;

        @RequiresApi(24)
        SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            readValues(source, loader);
        }

        SavedState(Parcel source) {
            super(source);
            readValues(source, null);
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        private void readValues(Parcel source, ClassLoader loader) {
            mRecyclerViewId = source.readInt();
            mCurrentItem = source.readInt();
            mAdapterState = source.readParcelable(loader);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mRecyclerViewId);
            out.writeInt(mCurrentItem);
            out.writeParcelable(mAdapterState, flags);
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return Build.VERSION.SDK_INT >= 24
                        ? new SavedState(source, loader)
                        : new SavedState(source);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(source, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
