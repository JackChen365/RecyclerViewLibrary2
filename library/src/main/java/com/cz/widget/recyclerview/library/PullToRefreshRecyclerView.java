package com.cz.widget.recyclerview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cz.widget.pulltorefresh.PullToRefreshLayout;
import com.cz.widget.pulltorefresh.RefreshMode;
import com.cz.widget.recyclerview.adapter.listener.OnItemClickListener;
import com.cz.widget.recyclerview.adapter.listener.OnItemLongClickListener;
import com.cz.widget.recyclerview.library.adapter.RefreshWrapperAdapter;
import com.cz.widget.recyclerview.library.footer.DefaultRefreshFooter;
import com.cz.widget.recyclerview.library.footer.RefreshFooterContainer;

/**
 * @author Created by cz
 * @date 2020-03-23 21:18
 * @email bingo110@126.com
 *
 * @see #FOOTER_LOAD
 * @see #FOOTER_ERROR
 * @see #FOOTER_COMPLETE
 */
public class PullToRefreshRecyclerView extends PullToRefreshLayout {
    private static final int FOOTER_STATUS_NONE = 0x00;
    private static final int FOOTER_STATUS_REFRESHING = 0x01;
    private static final int FOOTER_STATUS_COMPLETE =0x03;

    public static final int FOOTER_LOAD=R.id.footerLoadLayout;
    public static final int FOOTER_COMPLETE=R.id.footerCompleteLayout;
    public static final int FOOTER_ERROR=R.id.footerErrorLayout;

    /**
     * The wrapper adapter.
     * @see #setAdapter(RecyclerView.Adapter)
     */
    private final RefreshWrapperAdapter wrapperAdapter;
    /**
     * The load footer to refresh listener.
     */
    @Nullable
    private OnPullFooterToRefreshListener listener = null;

    private RefreshFooterContainer refreshFooterContainer =new DefaultRefreshFooter();
    /**
     * When the content is not fill the view.
     * Does we need to call the {@link OnPullFooterToRefreshListener#onRefresh()} if it does existed.
     * Sometimes we need to trigger this function. But maybe you don't need it. It depends on the server.
     * So here we have this bool and let you determine if you want to trigger the function.
     *
     * @see PullToRefreshRecyclerView#scrollStateChanged(int)
     */
    private boolean alwaysLoad;
    /**
     * The footer refresh state.
     */
    private int footerRefreshState = FOOTER_STATUS_NONE;
    /**
     * The list divider drawable。
     */
    private Drawable listDivider=null;

    public PullToRefreshRecyclerView(Context context) {
        this(context,null,0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.refreshFooterContainer.setPullToRefreshLayout(this);
        this.wrapperAdapter = new RefreshWrapperAdapter(null);

        Context wrapperContext = ContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView, R.attr.pullToRefreshRecyclerView, R.style.PullToRefresh);
        setListDivider(a.getDrawable(R.styleable.PullToRefreshRecyclerView_pull_listDivider));
        setAlwaysLoad(a.getBoolean(R.styleable.PullToRefreshRecyclerView_pull_alwaysLoad,false));
        a.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.refreshFooterContainer.setPullToRefreshLayout(this);
        this.wrapperAdapter=new RefreshWrapperAdapter(null);
        Context wrapperContext = ContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView, R.attr.pullToRefreshRecyclerView, R.style.PullToRefresh);
        setListDivider(a.getDrawable(R.styleable.PullToRefreshRecyclerView_pull_listDivider));
        setAlwaysLoad(a.getBoolean(R.styleable.PullToRefreshRecyclerView_pull_alwaysLoad,false));
        a.recycle();
    }

    private void setListDivider(@Nullable Drawable drawable) {
        this.listDivider=drawable;
    }

    private void setAlwaysLoad(boolean dissatisfiedScreenLoad) {
        this.alwaysLoad =dissatisfiedScreenLoad;
    }


    @Override
    protected void onFinishInflate() {
        //Add adapter header/footer view from xml by layout_type
        addAdapterLayout();
        super.onFinishInflate();
    }

    /**
     * Add adapter header/footer view from xml by layout type
     * @see R.attr#layout_adapterView
     */
    private void addAdapterLayout() {
        int index=0;
        while (index < getChildCount()) {
            View childView = getChildAt(index);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            if (LayoutParams.ITEM_TYPE_HEADER == layoutParams.itemType) {
                wrapperAdapter.addHeaderView(childView);
                removeViewAt(index);
            } else if (LayoutParams.ITEM_TYPE_FOOTER == layoutParams.itemType) {
                wrapperAdapter.addFooterView(childView);
                removeViewAt(index);
            } else {
                index++;
            }
        }
    }

    @Override
    protected View createContentView(Context context) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                scrollStateChanged(newState);
            }
        });
        return recyclerView;
    }

    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor){
        RecyclerView recyclerView=getContentView();
        recyclerView.addItemDecoration(decor);
    }

    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor, int index){
        RecyclerView recyclerView=getContentView();
        recyclerView.addItemDecoration(decor,index);
    }

    public void setItemAnimator(@Nullable RecyclerView.ItemAnimator animator){
        RecyclerView recyclerView=getContentView();
        recyclerView.setItemAnimator(animator);
    }

    public int getHeaderViewCount() {
        return wrapperAdapter.getHeaderViewCount();
    }

    public int getFooterViewCount() {
        return wrapperAdapter.getFooterViewCount();
    }

    /**
     * Add a header view.
     * @param view
     */
    public void addHeaderView(@NonNull View view) {
        wrapperAdapter.addHeaderView(view);
    }

    /**
     * Add a footer view.
     * @param view
     */
    public void addFooterView(@NonNull View view) {
        wrapperAdapter.addFooterView(view);
    }

    /**
     * Return a header view by index.
     * @param index
     * @return
     */
    @Nullable
    public View getHeaderView(int index) {
        return wrapperAdapter.getHeaderView(index);
    }

    /**
     * Return a footer view by index
     * @param index
     * @return
     */
    @Nullable
    public View getFooterView(int index) {
        return wrapperAdapter.getFooterView(index);
    }

    public void addAdapterView(@NonNull View view,int position){
        wrapperAdapter.addAdapterView(view,position);
    }

    public void removeAdapterView(@Nullable View view){
        wrapperAdapter.removeAdapterView(view);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        RecyclerView recyclerView=getContentView();
        recyclerView.addOnScrollListener(listener);
    }

    public void removeOnScrollListener(RecyclerView.OnScrollListener listener) {
        RecyclerView recyclerView=getContentView();
        recyclerView.removeOnScrollListener(listener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.wrapperAdapter.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.wrapperAdapter.setOnItemLongClickListener(listener);
    }

    public void scrollToPosition(int position) {
        RecyclerView recyclerView=getContentView();
        if(null!=recyclerView){
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                linearLayoutManager.scrollToPositionWithOffset(position,0);
            } else {
                layoutManager.scrollToPosition(position);
            }
        }
    }

    public void smoothScrollToPosition(int position){
        RecyclerView recyclerView=getContentView();
        if(null!=recyclerView){
            recyclerView.smoothScrollToPosition(position);
        }
    }

    public void onRefreshFooterComplete() {
        if (FOOTER_STATUS_REFRESHING == footerRefreshState) {
            footerRefreshState = FOOTER_STATUS_NONE;
            RecyclerView recyclerView=getContentView();
            recyclerView.requestLayout();
        }
    }

    public void setRefreshMode(RefreshMode refreshMode) {
        super.setRefreshMode(refreshMode);
        RecyclerView recyclerView=getContentView();
        if(null!=recyclerView){
            if(refreshMode.isDisable()){
                recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS);
            } else {
                recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            }
        }
        initializeRefreshFooter(refreshMode);
    }

    /**
     * Change the footer frame by id.
     * @see #setRefreshFooterContainer(RefreshFooterContainer)
     * By using this delegate, you will be able to support the error or other custom footer frame.
     * @param id
     */
    public void setRefreshFooterFrame(@IdRes int id){
        this.refreshFooterContainer.setFooterFrame(id);
    }

    public void setRefreshFooterContainer(@NonNull RefreshFooterContainer refreshFooterContainer){
        RecyclerView.Adapter adapter = getAdapter();
        if(null!=adapter){
            throw new IllegalArgumentException("The refresh footer container should be set before changing the footer container!");
        }
        Context context = getContext();
        RecyclerView contentView = getContentView();
        this.refreshFooterContainer=refreshFooterContainer;
        this.refreshFooterContainer.setPullToRefreshLayout(this);
        this.refreshFooterContainer.createFrameView(context,contentView);
    }

    private void initializeRefreshFooter(RefreshMode mode) {
        if(null!=refreshFooterContainer){
            View footerView = refreshFooterContainer.getFooterView();
            if (mode.isEnableEnd()) {
                footerRefreshState = FOOTER_STATUS_NONE;
                refreshFooterContainer.setFooterFrame(FOOTER_LOAD);
                wrapperAdapter.addRefreshFooterView(footerView);
                scrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
            } else {
                footerRefreshState = FOOTER_STATUS_NONE;
                wrapperAdapter.removeRefreshFooterView(footerView);
            }
        }
    }

    public void setAdapter(@NonNull RecyclerView.Adapter adapter){
        RecyclerView recyclerView=getContentView();
        if(null==recyclerView.getAdapter()){
            recyclerView.setAdapter(wrapperAdapter);
        }
        //We change the wrapped adapter.
        wrapperAdapter.setAdapter(adapter);
    }

    @Nullable
    public RecyclerView.Adapter getAdapter(){
        return wrapperAdapter.getAdapter();
    }

    public void setLayoutManager(@NonNull RecyclerView.LayoutManager layoutManager){
        RecyclerView recyclerView=getContentView();
        recyclerView.setLayoutManager(layoutManager);

        Context context = getContext();
        int orientation=RecyclerView.VERTICAL;
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            orientation=linearLayoutManager.getOrientation();
        }
        if(null!=listDivider){
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, orientation);
            dividerItemDecoration.setDrawable(listDivider);
            addItemDecoration(dividerItemDecoration);
        }

        //The layout manager might change all the view. like the orientation.
        //We consider support pull to refresh horizontally in the future.
        refreshFooterContainer.createFrameView(context,recyclerView);
        RefreshMode refreshMode = getRefreshMode();
        initializeRefreshFooter(refreshMode);
    }

    public RecyclerView.LayoutManager getLayoutManager(){
        RecyclerView.LayoutManager layoutManager=null;
        RecyclerView recyclerView=getContentView();
        if(null!=recyclerView){
            layoutManager=recyclerView.getLayoutManager();
        }
        return layoutManager;
    }

    /**
     * Find the first visible position.
     * We support three different layout manager
     * @see LinearLayoutManager#findFirstVisibleItemPosition()
     * @see GridLayoutManager#findFirstVisibleItemPosition()
     * @see StaggeredGridLayoutManager#findFirstVisibleItemPositions(int[])
     *
     * The others I will suggest you use {@link #getLayoutManager()} and use your own LayoutManager to find the vibible item position.
     */
    public int findFirstVisibleItemPosition(){
        int firstVisibleItemPosition=RecyclerView.NO_POSITION;
        RecyclerView recyclerView=getContentView();
        if(null!=recyclerView){
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] spanArray = new int[]{staggeredGridLayoutManager.getSpanCount()};
                staggeredGridLayoutManager.findFirstVisibleItemPositions(spanArray);
                for(int i=0;i<spanArray.length;i++){
                    if(firstVisibleItemPosition<spanArray[i]){
                        firstVisibleItemPosition=spanArray[i];
                    }
                }
            } else if(layoutManager instanceof LinearLayoutManager){
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            }
        }
        return firstVisibleItemPosition;
    }

    /**
     * Find the last visible position
     * We support three different layout manager
     * @see LinearLayoutManager#findLastVisibleItemPosition()
     * @see GridLayoutManager#findLastVisibleItemPosition()
     * @see StaggeredGridLayoutManager#findLastVisibleItemPositions(int[])
     *
     * The others I will suggest you use {@link #getLayoutManager()} and use your own LayoutManager to find the vibible item position.
     */
    public int findLastVisibleItemPosition(){
        int lastVisibleItemPosition=RecyclerView.NO_POSITION;
        RecyclerView recyclerView=getContentView();
        if(null!=recyclerView){
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] spanArray = new int[]{staggeredGridLayoutManager.getSpanCount()};
                staggeredGridLayoutManager.findFirstVisibleItemPositions(spanArray);
                for(int i=0;i<spanArray.length;i++){
                    if(lastVisibleItemPosition<spanArray[i]){
                        lastVisibleItemPosition=spanArray[i];
                    }
                }
            } else if(layoutManager instanceof LinearLayoutManager){
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        }
        return lastVisibleItemPosition;
    }

    @Override
    public void onRefreshComplete() {
        scrollToPosition(0);
        super.onRefreshComplete();
    }

    @Override
    protected int getRefreshCompleteAnimationDelayedTime() {
        RecyclerView recyclerView=getContentView();
        RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        //After we insert the data to the adapter. we should wait for a while
        return (int) (itemAnimator.getAddDuration()*2);
    }

    /**
     * on recyclerView scroll state changed
     * @param state
     */
    private void scrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE && null != listener && isEnableEnd()) {
            RecyclerView recyclerView=getContentView();
            if(null!=recyclerView&&footerRefreshState != FOOTER_STATUS_COMPLETE){
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int itemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = findFirstVisibleItemPosition();
                int lastVisibleItemPosition = findLastVisibleItemPosition();
                if (!alwaysLoad &&lastVisibleItemPosition - firstVisibleItemPosition >= itemCount - 1) {
                    //If the content not fill the view and you don't want to load anymore.
                    refreshFooterContainer.setFooterFrame(FOOTER_COMPLETE);
                    footerRefreshState = FOOTER_STATUS_COMPLETE;
                } else if (lastVisibleItemPosition >= itemCount - 1 &&
                        layoutManager.getItemCount() >= layoutManager.getChildCount() &&
                        footerRefreshState == FOOTER_STATUS_NONE && !refreshFooterContainer.isFooterFrame(FOOTER_COMPLETE)) {
                    //start load data.
                    setFooterRefreshing();
                }
            }
        }
    }

    /**
     * Call footer start refresh.
     */
    public void setFooterRefreshing() {
        footerRefreshState = FOOTER_STATUS_REFRESHING;
        if(null!=listener){
            listener.onRefresh();
        }
    }

    @Nullable
    public View findAdapterView(@IdRes int id){
        return wrapperAdapter.findView(id);
    }

    public void setOnPullFooterToRefreshListener(OnPullFooterToRefreshListener listener) {
        this.listener = listener;
    }

    public void autoRefresh(final boolean smooth) {
        scrollToPosition(0);
        super.autoRefresh(smooth);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = getContext();
        return new LayoutParams(context, attrs);
    }

    public class LayoutParams extends ViewGroup.LayoutParams {
        public static final int ITEM_TYPE_HEADER=0x00;
        public static final int ITEM_TYPE_FOOTER=0x01;
        public int itemType = 0;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PullToRefreshRecyclerView);
            itemType = a.getInt(R.styleable.PullToRefreshRecyclerView_layout_adapterView, ITEM_TYPE_HEADER);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    public interface OnPullFooterToRefreshListener {
        void onRefresh();
    }
}

