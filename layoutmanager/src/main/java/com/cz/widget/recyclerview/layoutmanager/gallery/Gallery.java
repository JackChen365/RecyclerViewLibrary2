package com.cz.widget.recyclerview.layoutmanager.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.layoutmanager.R;
import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;
import com.cz.widget.recyclerview.layoutmanager.widget.AbsCycleLayout;

/**
 * @author Created by cz
 * @date 2020-03-22 14:39
 * @email bingo110@126.com
 */
public class Gallery extends AbsCycleLayout {

    public Gallery(Context context) {
        this(context,null,R.attr.gallery);
    }

    public Gallery(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.gallery);
    }

    public Gallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs,defStyleAttr);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        RecyclerView recyclerView = getRecyclerView();
        boolean b = recyclerView.canScrollHorizontally(direction);
        Log.i("canScrollHorizontally","canScrollHorizontally:"+direction);
        return b;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        RecyclerView recyclerView = getRecyclerView();
        int i = recyclerView.computeVerticalScrollExtent();
        int i1 = recyclerView.computeVerticalScrollRange();
        int i2 = recyclerView.computeVerticalScrollOffset();
        Log.i("canScrollVertically","canScrollVertically:"+direction+" i:"+i+" i1:"+i1+" i2:"+i2);
        return recyclerView.canScrollVertically(direction);
    }

    @Override
    protected CenterLayoutManager createLayoutManager(Context context, AttributeSet attrs, int defStyleAttr) {
        return new CenterLayoutManager(context, attrs, defStyleAttr,R.style.Gallery){
            @Override
            protected void addAdapterView(View view) {
                super.addAdapterView(view);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        smoothScrollToView(v);
                    }
                });
            }
        };
    }
}
