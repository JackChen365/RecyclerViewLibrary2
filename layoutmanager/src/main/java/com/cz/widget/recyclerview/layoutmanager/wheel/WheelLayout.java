package com.cz.widget.recyclerview.layoutmanager.wheel;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.layoutmanager.R;
import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;
import com.cz.widget.recyclerview.layoutmanager.widget.AbsCycleLayout;

/**
 * @author Created by cz
 * @date 2020-03-22 15:56
 * @email bingo110@126.com
 */
public class WheelLayout extends AbsCycleLayout {
    public WheelLayout(Context context) {
        super(context);
    }

    public WheelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WheelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getWheelCount(){
        WheelLayoutManager layoutManager = (WheelLayoutManager) getLayoutManager();
        return layoutManager.getWheelCount();
    }

    @Override
    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        super.addItemDecoration(decor);
        if(decor instanceof WheelItemDecoration){
            WheelItemDecoration itemDecoration = (WheelItemDecoration) decor;
            itemDecoration.setWheelLayout(this);
        }
    }

    @Override
    public void addItemDecoration(@NonNull RecyclerView.ItemDecoration decor, int index) {
        super.addItemDecoration(decor, index);
        if(decor instanceof WheelItemDecoration){
            WheelItemDecoration itemDecoration = (WheelItemDecoration) decor;
            itemDecoration.setWheelLayout(this);
        }
    }

    @Override
    protected CenterLayoutManager createLayoutManager(Context context, AttributeSet attrs, int defStyleAttr) {
        return new WheelLayoutManager(context,attrs,defStyleAttr, R.style.WheelLayout);
    }
}
