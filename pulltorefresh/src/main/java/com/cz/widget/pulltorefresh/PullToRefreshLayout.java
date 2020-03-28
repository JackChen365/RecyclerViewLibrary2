package com.cz.widget.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;

import com.cz.widget.pulltorefresh.header.MaterialProgressHeader;
import com.cz.widget.pulltorefresh.header.PullToRefreshHeader;
import com.cz.widget.pulltorefresh.header.VectorRefreshHeader;
import com.cz.widget.pulltorefresh.strategy.DisplayStrategy;
import com.cz.widget.pulltorefresh.strategy.FollowStrategy;
import com.cz.widget.pulltorefresh.strategy.FrontStrategy;
import com.cz.widget.pulltorefresh.strategy.OverlapStrategy;
import com.cz.widget.pulltorefresh.strategy.PullToRefreshStrategy;

/**
 * @author Created by cz
 * @date 2020-03-02 20:29
 * @email bingo110@126.com
 * Here is the basic pull to refresh layout.
 * I always want to have a component like this. That you could be able to implement your own pull strategy
 *
 * This is four typical strategies
 * @see FollowStrategy The most popular strategy that the header will follow the content.
 * @see OverlapStrategy Only allow content scroll, and stick the header in the back.
 * @see FrontStrategy The officeial style. Like:SwipeRefreshLayout. The header follows the finger but the content was stilling
 * @see DisplayStrategy Display some information. When user drag the content. The primary example would be some websites.
 *
 *
 * Here are all the attributes.
 *
 * @see R.attr#pull_resistance If want to pull slowly. Here are the pull resistance.
 * @see R.attr#pull_headerStrategy The different pull header strategy.
 * @see R.attr#pull_maximumScrollOffset The maximum scroll offset. If you don't want the user to pull the screen that hard.
 * @see R.attr#pull_minimumRefreshDuration The minimum refresh duration.
 *      Acually we postpone the trigger time. If you want user watch the animation for a while
 * @see R.attr#pull_refreshFreeze Freeze the screen while frefreshing.
 * @see R.attr#pull_refreshMode The different modes of fetch data.
 * We got four different modes
 * <pre>
 *     <enum name="both" value="0x00"/>
 *     <enum name="start" value="0x01"/>
 *     <enum name="end" value="0x02"/>
 *     <enum name="none" value="0x03"/>
 * </pre>
 *
 */
public class PullToRefreshLayout extends ViewGroup implements NestedScrollingParent {
    /**
     * The header will follow the content.
     */
    public static final int STRATEGY_FOLLOW = 0x00;
    /**
     * This strategy will put the header in the back of the content.
     * After the finger pull the content down. the Header will show up.
     */
    public static final int STRATEGY_OVERLAP = 0x01;
    /**
     * The strategy will put the header in front of the content.
     * The primary sample would be The official header.
     */
    public static final int STRATEGY_FRONT = 0x02;
    /**
     * This is a typical strategy for the WebView.
     * When your finger pulled. It shows some information.
     * When you released. It goes back.
     */
    public static final int STRATEGY_SCROLL = 0x03;

    /**
     * The default header style. which is the official style
     */
    private static final int HEADER_MATERIAL=0x00;
    /**
     * The custom vector style.
     */
    private static final int HEADER_VECTOR=0x01;
    /**
     * The default scroller.
     */
    @NonNull
    private OverScroller scroller;
    /**
     * The pull resistance value. I assume this value should in the range[1f..3f]
     */
    private float pullResistance;
    /**
     * The maximum scroll offset.
     * Be careful, This value should usually bigger than the header's height.
     */
    private float maximumScrollOffset;
    /**
     * This minimum refresh duration.
     */
    private int minimumRefreshDuration;
    /**
     * Freeze the view while fetch data from server.
     */
    private boolean refreshFreeze;
    /**
     * The refresh mode.
     */
    private RefreshMode refreshMode;
    /**
     * Current state of this view;
     */
    private PullToRefreshState pullToRefreshState=PullToRefreshState.NONE;
    /**
     * The pull strategy.
     */
    private PullToRefreshStrategy pullToRefreshStrategy;
    /**
     * The default header.
     * @see MaterialProgressHeader
     */
    private PullToRefreshHeader header;
    /**
     * The refresh listener.
     */
    private OnPullToRefreshListener listener;
    /**
     * The content view. This view will cooperate with the header view.
     */
    private View contentView;

    @IntDef({STRATEGY_FOLLOW, STRATEGY_OVERLAP, STRATEGY_FRONT, STRATEGY_SCROLL})
    public @interface Strategy{
    }

    public PullToRefreshLayout(Context context) {
        this(context,null,R.attr.pullToRefreshLayout);
        setupHeaderAndContentView();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.pullToRefreshLayout);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller=new OverScroller(context);
        header = new MaterialProgressHeader(context);

        Context wrapperContext = PullToRefreshContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout, defStyleAttr, R.style.PullToRefreshLayout);
        setPullResistance(a.getFloat(R.styleable.PullToRefreshLayout_pull_resistance,1f));
        setPullMaximumScrollOffset(a.getDimension(R.styleable.PullToRefreshLayout_pull_maximumScrollOffset,0f));
        setMinimumRefreshDuration(a.getInteger(R.styleable.PullToRefreshLayout_pull_minimumRefreshDuration,0));
        setRefreshFreeze(a.getBoolean(R.styleable.PullToRefreshLayout_pull_refreshFreeze,false));
        setRefreshMode(RefreshMode.values()[a.getInt(R.styleable.PullToRefreshLayout_pull_refreshMode,RefreshMode.BOTH.ordinal())]);
        setPullHeaderStyle(context,a.getInt(R.styleable.PullToRefreshLayout_pull_headerStyle,0));
        pullToRefreshStrategy = getPullToRefreshStrategy(a.getInt(R.styleable.PullToRefreshLayout_pull_headerStrategy, STRATEGY_FOLLOW));
        a.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        scroller=new OverScroller(context);

        Context wrapperContext = PullToRefreshContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout, defStyleAttr, R.style.PullToRefreshLayout);
        setPullResistance(a.getFloat(R.styleable.PullToRefreshLayout_pull_resistance,1f));
        setPullMaximumScrollOffset(a.getDimension(R.styleable.PullToRefreshLayout_pull_maximumScrollOffset,0f));
        setMinimumRefreshDuration(a.getInteger(R.styleable.PullToRefreshLayout_pull_minimumRefreshDuration,0));
        setRefreshMode(RefreshMode.values()[a.getInt(R.styleable.PullToRefreshLayout_pull_refreshMode,RefreshMode.BOTH.ordinal())]);
        setPullHeaderStyle(context,a.getInt(R.styleable.PullToRefreshLayout_pull_headerStyle,0));
        pullToRefreshStrategy = getPullToRefreshStrategy(a.getInt(R.styleable.PullToRefreshLayout_pull_headerStrategy, STRATEGY_FOLLOW));
        a.recycle();
    }

    /**
     * We support custom vector style.
     * @param style
     */
    private void setPullHeaderStyle(Context context,int style) {
        if(HEADER_MATERIAL==style){
            header = new MaterialProgressHeader(context);
        } else if(HEADER_VECTOR==style){
            TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.headerVectorLayout});
            if (!a.hasValue(0)) {
                throw new IllegalArgumentException("If you want to use vector refresh header. Make sure you configure the attribute:headerVectorLayout!");
            } else {
                //We initialize vector layout from attr:headerVectorLayout
                int layoutResources = a.getResourceId(0, 0);
                header= new VectorRefreshHeader(layoutResources);
            }
            a.recycle();
        }
    }


    /**
     * The pull resistance value. I assume this value should in the range[1f..3f]
     * @param resistance
     */
    public void setPullResistance(@FloatRange(from = 1f,to = 3f) float resistance){
        this.pullResistance=resistance;
    }

    /**
     * The maximum scroll offset.
     * Be careful, This value should usually bigger than the header's height.
     * @param offset
     */
    public void setPullMaximumScrollOffset(float offset){
        this.maximumScrollOffset=offset;
    }

    /**
     * This minimum refresh duration.
     * It's a special needs. Which is sometimes you just want to see the animation for a while.
     * But your server just too powerful and return your request immediately.
     * So here the minimum duration for refresh.
     * @param minimumDuration
     */
    private void setMinimumRefreshDuration(int minimumDuration) {
        this.minimumRefreshDuration=minimumDuration;
    }

    /**
     * When you start to refresh and fetch data from server.
     * You want to freeze the view while the animation is running.
     * @param refreshFreeze
     */
    private void setRefreshFreeze(boolean refreshFreeze) {
        this.refreshFreeze=refreshFreeze;
    }


    /**
     * The refresh mode. We have four different refresh mode.
     * What's more. This function is steady for the user to switch different modes in code.
     * So after you load all the data. You could just isDisable the view.
     *
     * @see RefreshMode#BOTH  This will enable both start and end.
     * @see RefreshMode#PULL_START Only allow you pull from start
     * @see RefreshMode#PULL_END Only allow you pull from end.
     * @see RefreshMode#NONE Do nothing.
     * @param mode
     */
    public void setRefreshMode(RefreshMode mode) {
        this.refreshMode=mode;
    }

    /**
     * Change a refresh header.
     * @param header
     */
    public void setPullToRefreshHeader(PullToRefreshHeader header) {
        this.header = header;
        setupHeaderAndContentView();
    }

    /**
     * Return the strategy by index.
     * @see PullToRefreshLayout#STRATEGY_FOLLOW
     * @see PullToRefreshLayout#STRATEGY_FRONT
     * @see PullToRefreshLayout#STRATEGY_OVERLAP
     * @see PullToRefreshLayout#STRATEGY_SCROLL
     * @param strategy
     * @return
     */
    private PullToRefreshStrategy getPullToRefreshStrategy(@Strategy int strategy){
        if(strategy==STRATEGY_FOLLOW){
            return new FollowStrategy();
        } else if(strategy==STRATEGY_FRONT){
            return new FrontStrategy();
        } else if(strategy==STRATEGY_OVERLAP){
            return new OverlapStrategy();
        } else if(strategy==STRATEGY_SCROLL){
            return new DisplayStrategy();
        }
        return null;
    }

    /**
     * Setup a strategy by index.
     * @see PullToRefreshLayout#STRATEGY_FOLLOW
     * @see PullToRefreshLayout#STRATEGY_FRONT
     * @see PullToRefreshLayout#STRATEGY_OVERLAP
     * @see PullToRefreshLayout#STRATEGY_SCROLL
     * @param strategy
     */
    public void setPullToRefreshStrategy(@Strategy int strategy) {
        PullToRefreshStrategy pullToRefreshStrategy = getPullToRefreshStrategy(strategy);
        if(null==pullToRefreshStrategy){
            throw new NullPointerException("We can't find the strategy by index!");
        } else {
            setPullToRefreshStrategy(pullToRefreshStrategy);
        }
    }

    public void setPullToRefreshStrategy(@NonNull PullToRefreshStrategy strategy){
        this.pullToRefreshStrategy=strategy;
        setupHeaderAndContentView();
    }

    /**
     * Setup header and content view.
     * Here are three different situations.
     * The first one: Create a new layout by PullToRefreshLayout#PullToRefreshLayout(android.content.Context)
     * In that case we don't have the chance to setup.
     *
     * The second one:
     * com.cz.widget.pulltorefresh.PullToRefreshLayout#onFinishInflate()
     * After inflate the view from the XML. we setup the header and content view.
     *
     * The last one:
     * We change the strategy manually. Then we have to re-layout the view.
     */
    private void setupHeaderAndContentView(){
        removeAllViews();
        header.onAttachToWindow(this);
        View refreshHeaderView = header.getRefreshHeaderView();
        pullToRefreshStrategy.setTarget(this);
        pullToRefreshStrategy.onViewAdded(header,contentView,refreshHeaderView);
        pullToRefreshStrategy.onViewLayout(header,contentView,refreshHeaderView,0,0,getWidth(),getHeight());
        requestLayout();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if(1 == childCount){
            this.contentView=getChildAt(0);
        } else {
            this.contentView=createContentView(getContext());
        }
        if(null==contentView){
            throw new IllegalArgumentException("PullToRefreshLayout need one content view. You could either have it in XML or call#getContentView()");
        }
        //We don't want to show the over scroll effect
        contentView.setOverScrollMode(OVER_SCROLL_NEVER);
        setupHeaderAndContentView();
    }

    public boolean isRefreshDisable(){
        return refreshMode.isDisable();
    }

    public boolean isEnableStart(){
        return refreshMode.isEnableStart();
    }

    public boolean isEnableEnd(){
        return refreshMode.isEnableEnd();
    }

    @NonNull
    public OverScroller getScroller() {
        return scroller;
    }

    public float getPullResistance() {
        return pullResistance;
    }

    public float getMaximumScrollOffset() {
        return maximumScrollOffset;
    }

    public int getMinimumRefreshDuration() {
        return minimumRefreshDuration;
    }

    public RefreshMode getRefreshMode() {
        return refreshMode;
    }

    public PullToRefreshState getPullToRefreshState() {
        return pullToRefreshState;
    }

    public PullToRefreshStrategy getPullToRefreshStrategy() {
        return pullToRefreshStrategy;
    }

    public PullToRefreshHeader getPullToRefreshHeader() {
        return header;
    }

    public boolean isRefreshState(PullToRefreshState state){
        return pullToRefreshState==state;
    }

    public boolean isRefreshing(){
        return isRefreshState(PullToRefreshState.REFRESHING)||
                isRefreshState(PullToRefreshState.REFRESHING_DRAGGING)||
                isRefreshState(PullToRefreshState.REFRESHING_COMPLETE);
    }

    /**
     * The the refresh state changed.
     * We dispatch the state to the header.
     * The change usually from {@link com.cz.widget.pulltorefresh.strategy.PullToRefreshStrategy}
     *
     * @param state
     */
    public void setRefreshState(PullToRefreshState state){
        if(pullToRefreshState!=state){
            pullToRefreshState=state;
            header.onRefreshStateChange(pullToRefreshState);
        }
    }

    /**
     * Start refresh immediately.
     * This function is for strategy. So be careful to use it.
     * @see {@link PullToRefreshStrategy#autoRefresh(PullToRefreshHeader, boolean)}
     *
     * If you want to trigger refresh.
     * call {@link PullToRefreshLayout#autoRefresh(boolean)}
     */
    public void setRefreshing(){
        if(!isRefreshing()){
            setRefreshState(PullToRefreshState.REFRESHING);
            if(0>=minimumRefreshDuration){
                if(null!=listener){
                    listener.onRefresh();
                }
            } else {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=listener){
                            listener.onRefresh();
                        }
                    }
                },minimumRefreshDuration);
            }
        }
    }

    /**
     * Return a refresh header animation delayed time.
     * After we complete refresh. We may recover the layout by some default animation
     * While we restructure the layout and We are running the animation You may also change the layout.
     * That how the pull to refresh layout work.
     * So Here we want to avoid this situation by return this delayed time.
     *
     * For example for a Recycler view. You may return the ItemAnimator's insert animation duration.
     * @return
     */
    protected int getRefreshCompleteAnimationDelayedTime(){
        return 0;
    }

    /**
     * When outsize if complete. It will call this function to restore the refresh state.
     */
    public void onRefreshComplete(){
        if((isRefreshState(PullToRefreshState.REFRESHING)||
                isRefreshState(PullToRefreshState.REFRESHING_COMPLETE))||
                isRefreshState(PullToRefreshState.REFRESHING_DRAGGING)) {
            setRefreshState(PullToRefreshState.REFRESHING_COMPLETE);
            int refreshCompleteDelayedTime = getRefreshCompleteAnimationDelayedTime();
            pullToRefreshStrategy.onRefreshComplete(header,refreshCompleteDelayedTime);
            header.onRefreshComplete();
        }
    }

    /**
     * There are two ways to set the content view.
     * override this function and return a view. We will use the view.
     * Another way is add the only child in xml. {@link PullToRefreshLayout#onFinishInflate()}
     * @return
     */
    protected View createContentView(Context context){
        return null;
    }

    @NonNull
    public <V extends View> V getContentView(){
        return (V)contentView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(null==contentView) return;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int measureWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        if (View.MeasureSpec.UNSPECIFIED == widthMode) {
            measureWidth = contentView.getMeasuredWidth();
        }
        if (View.MeasureSpec.UNSPECIFIED == heightMode) {
            measureHeight = contentView.getMeasuredHeight();
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(null==contentView)return;
        View refreshHeaderView = header.getRefreshHeaderView();
        if(changed){
            //Here if the size changed. we should re-layout the content view and the header view.
            pullToRefreshStrategy.onViewLayout(header,contentView,refreshHeaderView,l,t,r,b);
        } else if(pullToRefreshStrategy.contentChangedLayout()){
            //If content changed for a view like RecyclerView. We have to re-layout the view.
            //Otherwise it won't move to the first position.
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            contentView.layout(paddingLeft,paddingTop, paddingLeft+contentView.getMeasuredWidth(), paddingTop+contentView.getMeasuredHeight());
        }
    }


    /**
     * Start scroll the view.
     * Both strategy and others could call this function to move the view.
     * @param startX
     * @param startY
     * @param dx
     * @param dy
     * @param duration
     */
    public void startScroll(int startX,int startY,int dx,int dy,int duration){
        scroller.startScroll(startX, startY, dx, dy,duration);
        invalidate();
    }

    /**
     * call the refresh programmatically
     */
    public void autoRefresh(boolean smooth){
        if(!isRefreshing()){
            setRefreshState(PullToRefreshState.START_PULL);
            pullToRefreshStrategy.autoRefresh(header,smooth);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(!scroller.isFinished()&&scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            invalidate();
        }
    }

    /**
     * We override dispatchTouchEvent instead of onInterceptedEvent.
     * Because when you hold the touch event and slightly canceled the event.
     * SomeHow we lost the event. Without up and cancel event. we can't stop the scroll.
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isRefreshing()&&refreshFreeze){
            return true;
        }
        int action = ev.getActionMasked();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                if(!scroller.isFinished()){
                    scroller.abortAnimation();
                    invalidate();
                }
                if(isRefreshing()){
                    //If we already in refresh state. But trigger refresh again.
                    //We mark the state as a special state.
                    setRefreshState(PullToRefreshState.REFRESHING_DRAGGING);
                } else {
                    //It means user start to pull.
                    setRefreshState(PullToRefreshState.START_PULL);
                }
                break;
            }
            //Sometimes it ends with action cancel. I have no idea. When you slightly move your finger.
            case MotionEvent.ACTION_UP: case MotionEvent.ACTION_CANCEL: {
                onStopScroll(contentView);
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * When we stop scroll the screen. Here is usually the user's finger leaves the screen
     */
    private void onStopScroll(View target) {
        if (isRefreshState(PullToRefreshState.REFRESHING_DRAGGING)) {
            setRefreshState(PullToRefreshState.REFRESHING);
        }
        pullToRefreshStrategy.onStopRefreshScroll(target, header);
        invalidate();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return refreshMode.isEnableStart()&&0!=(ViewCompat.SCROLL_AXIS_VERTICAL & nestedScrollAxes);
    }

    /**
     * We can't implement our strategy here.
     * Due to pull resistance we have to consume the scroll value.
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        pullToRefreshStrategy.onPullToRefresh(target, header,dx,dy, (int) maximumScrollOffset,consumed);
    }

    /**
     * We are not going to put {@link PullToRefreshLayout#onStopScroll(View)} here
     * Because this function will trigger every time when user touch down
     *
     * Code from {@link android.view.View#dispatchTouchEvent(android.view.MotionEvent)}
     * if (actionMasked == MotionEvent.ACTION_DOWN) {
     *     // Defensive cleanup for new gesture
     *     stopNestedScroll();
     * }
     * So we put the onStopScroll to {@link PullToRefreshLayout#onInterceptTouchEvent(android.view.MotionEvent)}
     *
     * @param child
     */
    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
    }

    /**
     * When the target view try to start finger fling.
     * If we want to stop if when we scroll over the content.
     * We might return false and do something else.
     * @param target
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return pullToRefreshStrategy.onRefreshFling(target,velocityX,velocityY);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    public void setOnPullToRefreshListener(OnPullToRefreshListener listener){
        this.listener=listener;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }

    /**
     * This interface responsible for refresh event.
     * If the finger pulls beyond the header view. It will trigger the call function.
     *
     */
    public interface OnPullToRefreshListener {
        void onRefresh();
    }

}
