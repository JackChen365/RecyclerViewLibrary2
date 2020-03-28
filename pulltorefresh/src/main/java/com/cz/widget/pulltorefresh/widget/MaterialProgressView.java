package com.cz.widget.pulltorefresh.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.cz.widget.pulltorefresh.PullToRefreshContextHelper;
import com.cz.widget.pulltorefresh.R;

/**
 * @author Created by cz
 * @date 2020-03-02 20:46
 * @email bingo110@126.com
 */
public class MaterialProgressView extends View {
    /**
     * The material process drawable;
     */
    private MaterialProgressDrawable materialDrawable;
    private float scale = 1f;

    public MaterialProgressView(Context context) {
        this(context,null,R.attr.materialHeaderView);
    }

    public MaterialProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,R.attr.materialHeaderView);
    }

    public MaterialProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        materialDrawable=new MaterialProgressDrawable(context,this);
        materialDrawable.setBackgroundColor(Color.WHITE);
        materialDrawable.setCallback(this);

        Context wrapperContext = PullToRefreshContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.MaterialHeaderView, defStyleAttr, R.style.MaterialHeaderView);
        setMaterialStyle(a.getInt(R.styleable.MaterialHeaderView_materialStyle,0));
        int paddingTop = a.getDimensionPixelSize(R.styleable.MaterialHeaderView_materialPaddingTop, 0);
        int paddingBottom = a.getDimensionPixelSize(R.styleable.MaterialHeaderView_materialPaddingBottom, 0);
        setPadding(0, paddingTop,0, paddingBottom);
        a.recycle();
    }
    private void setMaterialStyle(int style) {
        materialDrawable.updateSizes(style);
    }

    public void setMaterialDrawableScale(float scale){
        this.scale=scale;
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        if(who==materialDrawable){
            invalidate();
            return true;
        } else {
            return super.verifyDrawable(who);
        }
    }

    @Nullable
    public MaterialProgressDrawable getMaterialDrawable() {
        return materialDrawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int intrinsicWidth = materialDrawable.getIntrinsicWidth();
        int intrinsicHeight = materialDrawable.getIntrinsicHeight();
        int measuredWidth = intrinsicWidth + paddingLeft + paddingRight;
        int measuredHeight = intrinsicHeight + paddingTop + paddingBottom;
        setMeasuredDimension(measuredWidth,measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int intrinsicWidth = materialDrawable.getIntrinsicWidth();
        int intrinsicHeight = materialDrawable.getIntrinsicHeight();
        materialDrawable.setBounds(paddingLeft, paddingTop,
                paddingLeft+intrinsicWidth, paddingTop+intrinsicHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        materialDrawable.draw(canvas);
    }
}
