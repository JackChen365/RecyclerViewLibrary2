package com.cz.widget.recyclerview.library.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.adapter.wrapper.dynamic.DynamicWrapperAdapter;

/**
 * @author Created by cz
 * @date 2020-03-23 21:33
 * @email bingo110@126.com
 */
public class RefreshWrapperAdapter extends DynamicWrapperAdapter {
    private View refreshFooterView = null;

    public RefreshWrapperAdapter(@Nullable RecyclerView.Adapter adapter) {
        super(adapter);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
//        if(viewHolder.itemView==refreshFooterView){
//            viewHolder.setIsRecyclable(false);
//        }
        return viewHolder;
    }

    public void addRefreshFooterView(View view){
        if (null == refreshFooterView) {
            refreshFooterView=view;
            int footerViewCount = getFooterViewCount();
            if(0==footerViewCount){
                super.addFooterView(view,0);
            } else {
                super.addFooterView(view,footerViewCount);
            }
        }
    }

    public void removeRefreshFooterView(View view){
        if(refreshFooterView==view){
            refreshFooterView=null;
        }
        super.removeFooterView(view);
    }

    @Override
    protected void addFooterView(@NonNull View view, int index) {
        if(null==refreshFooterView){
            super.addFooterView(view, index);
        } else {
            int footerViewCount = getFooterViewCount();
            if(0==footerViewCount){
                super.addFooterView(view,0);
            } else {
                super.addFooterView(view,footerViewCount-1);
            }
        }
    }

    @Override
    public void removeFooterView(int position) {
        View footerView = getFooterView(position);
        if(footerView!=refreshFooterView){
            super.removeFooterView(position);
        }
    }

}
