package com.cz.widget.recyclerview.library.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import com.cz.widget.recyclerview.library.ContextHelper;
import com.cz.widget.recyclerview.library.R;

/**
 * @author Created by cz
 * @date 2020-03-24 10:02
 * @email bingo110@126.com
 * @see R.attr#layout_footerFrameType
 *
 * We have three preset frame for the footer layout
 * 1. The load frame. only show the load progress with a text.
 * 2. The complete frame. After we've loaded all the list data.
 * 3. The error frame. The network changed or we didn't get data from the network. We will show you the retry error information.
 *
 * @see FooterViewContainer A view container that displays or hide the frame.
 */
public class RefreshFooterFrameLayout extends FrameLayout implements FooterViewContainer {
    private static final int FRAME_LOAD=0x01;
    /**
     * The footer container.
     * @see #attachToFooterContainer(RefreshFooterContainer)
     */
    private RefreshFooterContainer container;
    @IdRes
    private int currentFrameId=View.NO_ID;

    public RefreshFooterFrameLayout(@NonNull Context context) {
        this(context,null, R.attr.refreshFooterFrameLayout);
    }

    public RefreshFooterFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.refreshFooterFrameLayout);
    }

    public RefreshFooterFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            inflate19(context, defStyleAttr);
        } else {
            final Context wrapperContext = ContextHelper.getWrapperContext(context,defStyleAttr);
            View.inflate(wrapperContext,R.layout.refresh_footer_frame_layout,this);
        }
    }

    /**
     * We can't load merge tag with attribute. I don't know the reason.
     * Here we user the LayoutInflater.Factor2 to set the ViewStub's layoutResources.
     * @see
     * @param context
     * @param defStyleAttr
     */
    private void inflate19(@NonNull Context context, int defStyleAttr) {
        final Context wrapperContext = ContextHelper.getWrapperContext(context,defStyleAttr);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LayoutInflater newLayoutInflater = layoutInflater.cloneInContext(wrapperContext);
        LayoutInflaterCompat.setFactory2(newLayoutInflater, new LayoutInflater.Factory2() {
            @Nullable
            @Override
            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                //Here we resolve our layout attribute.
                TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout});
                int layoutResources = a.getResourceId(0, -1);
                a.recycle();
                ViewStub viewStub = new ViewStub(context, attrs);
                viewStub.setLayoutResource(layoutResources);
                return viewStub;
            }
            @Nullable
            @Override
            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                return null;
            }
        });
        newLayoutInflater.inflate(R.layout.refresh_footer_frame_layout, this,true);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Generate a default layout params.
     * When you call {@link ViewGroup#addView(View)}.
     * It will ask for a default LayoutParams
     * @return
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Create a layout params from a giving one.
     * @param p
     * @return
     */
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = getContext();
        return new LayoutParams(context,attrs);
    }

    @Override
    public void setFooterFrame(@IdRes int id) {
        int lastFrameId=currentFrameId;
        this.currentFrameId=id;
        if(View.NO_ID!=lastFrameId){
            View lastFrameView = findViewById(lastFrameId);
            if(null!=lastFrameView){
                lastFrameView.setVisibility(View.GONE);
            }
        }
        View childView = findViewById(id);
        if(null!=childView){
            if(childView instanceof ViewStub) {
                ViewStub viewStub = (ViewStub) childView;
                View inflateView = viewStub.inflate();
                inflateView.setVisibility(View.VISIBLE);
                onLoadFrame(inflateView);
            } else {
                childView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * When this view created. We
     * @param container
     */
    public void attachToFooterContainer(RefreshFooterContainer container){
        this.container=container;
    }

    private void onLoadFrame(View view) {
        if(null!=container){
            this.container.onCrateFrameView(view);
        }
    }

    @Override
    public boolean isFooterFrame(int id) {
        boolean isFooterFrame=false;
        View view = findViewById(id);
        if(null!=view){
            isFooterFrame=View.VISIBLE==view.getVisibility();
        }
        return isFooterFrame;
    }

    /**
     * Our custom LayoutParams object to support layout transition.
     * @attr R.attr.layout_footerFrameType
     */
    public class LayoutParams extends FrameLayout.LayoutParams{
        public int frameType=FRAME_LOAD;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RefreshFooterFrameLayout);
            frameType = a.getInt(R.styleable.RefreshFooterFrameLayout_layout_footerFrameType, 0);
            a.recycle();
        }
    }
}
