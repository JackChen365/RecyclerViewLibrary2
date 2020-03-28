package com.cz.widget.recyclerview.library.footer;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.library.PullToRefreshRecyclerView;

/**
 * @author Created by cz
 * @date 2020-03-23 21:40
 * @email bingo110@126.com
 */
public abstract class RefreshFooterContainer<V extends View&FooterViewContainer>{
    /**
     * The pull to refresh view.
     */
    protected PullToRefreshRecyclerView pullToRefreshLayout;
    /**
     * The footer view.
     */
    private V footerView=null;

    public V createFrameView(Context context, RecyclerView parent){
        footerView= onCreateView(context,parent);
        return footerView;
    }

    public void setPullToRefreshLayout(PullToRefreshRecyclerView recyclerView) {
        this.pullToRefreshLayout = recyclerView;
    }

    /**
     * On create frame view.
     * @param context
     * @param parent
     */
    @NonNull
    protected abstract V onCreateView(Context context, RecyclerView parent);

    /**
     * When the first time we created the frame view.
     * @param view
     */
    public void onCrateFrameView(View view){

    }

    /**
     * Return the footer view.
     * @return
     */
    @Nullable
    public View getFooterView(){
        return footerView;
    }

    /**
     * Show a frame in this container by id
     * @param id
     */
    public void setFooterFrame(@IdRes int id){
        footerView.setFooterFrame(id);
    }

    /**
     * If refreshing is done.
     * @return
     */
    public boolean isFooterFrame(@IdRes int id){
        return footerView.isFooterFrame(id);
    }
}
