package com.cz.widget.recyclerview.layoutmanager.wheel;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;

/**
 * @author Created by cz
 * @date 2020-03-23 21:43
 * @email bingo110@126.com
 */
public abstract class WheelItemDecoration extends RecyclerView.ItemDecoration {
    private WheelLayout wheelLayout;

    void setWheelLayout(WheelLayout layout){
        this.wheelLayout=layout;
    }

    public int getWheelCount(){
        return this.wheelLayout.getWheelCount();
    }

    public OrientationHelper getOrientationHelper(){
        CenterLayoutManager layoutManager = getLayoutManager();
        return layoutManager.getOrientationHelper();
    }

    public CenterLayoutManager getLayoutManager(){
        return wheelLayout.getLayoutManager();
    }
}
