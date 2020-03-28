package com.cz.widget.recyclerview.sample.layoutmanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroupOverlay;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.sample.R;

/**
 * @author Created by cz
 * @date 2020-03-21 20:01
 * @email bingo110@126.com
 */
public class DebugItemDecoration extends View {
    private Drawable divider;
    private int dividerSize;
    private int orientation;

    public DebugItemDecoration(Context context) {
        super(context);
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

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void attachToView(RecyclerView recyclerView){
        recyclerView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int widthMeasureSpec = MeasureSpec.makeMeasureSpec(right - left, MeasureSpec.EXACTLY);
                int heightMeasureSpec = MeasureSpec.makeMeasureSpec(bottom - top, MeasureSpec.EXACTLY);
                measure(widthMeasureSpec,heightMeasureSpec);
                layout(left,top,right,bottom);
            }
        });
        recyclerView.addItemDecoration(new ItemDecorationImpl());
        ViewGroupOverlay overlay = recyclerView.getOverlay();
        overlay.add(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if(null!=divider){
            //The horizontal line.
            divider.setBounds(0,(height- dividerSize *2)/2,width,(height+ dividerSize *2)/2);
            divider.draw(canvas);
            //The vertical line.
            divider.setBounds((width- dividerSize *2)/2,0,(width+ dividerSize *2)/2,height);
            divider.draw(canvas);
        }
    }

    private class ItemDecorationImpl extends RecyclerView.ItemDecoration{
        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);
            if(LinearLayoutManager.HORIZONTAL==orientation){
                drawHorizontalDivider(c,parent,state);
            } else {
                drawVerticalDivider(c, parent);
            }
        }

        private void drawVerticalDivider(@NonNull Canvas c, @NonNull RecyclerView parent) {
            int childCount = parent.getChildCount();
            for (int i=0;i<childCount;i++) {
                View child = parent.getChildAt(i);
                int left = 0;
                int right = child.getWidth();
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)child.getLayoutParams();
                if(null!=divider){
                    int itemPosition = layoutParams.getViewAdapterPosition();
                    if(0==itemPosition){
                        int top = child.getTop() + layoutParams.topMargin;
                        int bottom = top + dividerSize;
                        divider.setBounds(left, top, right, bottom);
                        divider.draw(c);
                    }
                    //Draw the bottom
                    int top = child.getBottom() + layoutParams.bottomMargin;
                    int bottom = top + dividerSize;
                    divider.setBounds(left, top, right, bottom);
                    divider.draw(c);
                }
            }
        }

        private void drawHorizontalDivider(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            for (int i=0;i<childCount;i++) {
                View child = parent.getChildAt(i);
                int top = 0;
                int bottom = child.getHeight();
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)child.getLayoutParams();
                if(null!=divider){
                    int left = child.getRight() + layoutParams.rightMargin;
                    int right = left + dividerSize;
                    divider.setBounds(left, top + dividerSize, right, bottom - dividerSize);
                    divider.draw(c);
                }
            }
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if(LinearLayoutManager.HORIZONTAL==orientation){
                outRect.set(0, 0, dividerSize, 0);
            } else {
                outRect.set(0, 0, 0, dividerSize);
            }
        }

    }
}
