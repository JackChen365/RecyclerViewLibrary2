package com.cz.widget.recyclerview.library.footer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.library.ContextHelper;
import com.cz.widget.recyclerview.library.R;

/**
 * @author Created by cz
 * @date 2020-03-24 10:01
 * @email bingo110@126.com
 */
public class DefaultRefreshFooter extends RefreshFooterContainer {
    @Override
    public View onCreateView(Context context, RecyclerView parent) {
        Context wrapperContext = ContextHelper.getWrapperContext(context);
        RefreshFooterFrameLayout footerFrameLayout = new RefreshFooterFrameLayout(wrapperContext);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int oritation=RecyclerView.VERTICAL;
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            oritation=linearLayoutManager.getOrientation();
        }
        if(RecyclerView.VERTICAL==oritation){
            footerFrameLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            footerFrameLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
        }
        footerFrameLayout.attachToFooterContainer(this);
        return footerFrameLayout;
    }

    @Override
    public void onCrateFrameView(View view) {
        super.onCrateFrameView(view);
        if(R.id.footerErrorLayout==view.getId()){
            View retryButton=view.findViewById(R.id.errorRetryButton);
            if(null!=retryButton){
                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFooterFrame(R.id.footerLoadLayout);
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pullToRefreshLayout.setFooterRefreshing();
                            }
                        },300);
                    }
                });
            }
        }
    }
}
