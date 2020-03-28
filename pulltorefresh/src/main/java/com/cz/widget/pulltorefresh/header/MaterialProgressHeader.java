package com.cz.widget.pulltorefresh.header;

import android.content.Context;
import android.view.View;

import com.cz.widget.pulltorefresh.PullToRefreshState;
import com.cz.widget.pulltorefresh.R;
import com.cz.widget.pulltorefresh.widget.MaterialProgressView;
import com.cz.widget.pulltorefresh.widget.MaterialProgressDrawable;

/**
 * @author Created by cz
 * @date 2020-03-02 21:54
 * @email bingo110@126.com
 * This header is more like the official pull to refresh header.
 */
public class MaterialProgressHeader extends PullToRefreshHeader {
    /**
     * The header view.
     */
    private MaterialProgressView materialProgressView;

    public MaterialProgressHeader(Context context) {
        int drawablePadding = context.getResources().getDimensionPixelOffset(R.dimen.material_drawable_padding);
        this.materialProgressView=new MaterialProgressView(context);
        this.materialProgressView.setPadding(0,drawablePadding,0,drawablePadding);
    }

    @Override
    public View getRefreshHeaderView() {
        return materialProgressView;
    }

    @Override
    public void onScrollOffset(float fraction) {
        MaterialProgressDrawable drawable = materialProgressView.getMaterialDrawable();
        drawable.setAlpha((int) (255 * fraction));
        float strokeStart = fraction * .8f;
        drawable.setStartEndTrim(0f, Math.min(0.8f, strokeStart));
        drawable.setArrowScale(Math.min(1f, fraction));

        float rotation = (-0.25f + .4f * fraction + fraction * 2) * .5f;
        drawable.setProgressRotation(rotation);
        materialProgressView.invalidate();
    }

    @Override
    public void onRefreshStateChange(PullToRefreshState refreshState) {
        MaterialProgressDrawable drawable = materialProgressView.getMaterialDrawable();
        switch (refreshState){
            case START_PULL:{
                materialProgressView.setVisibility(View.VISIBLE);
                stopArrowDrawable(drawable);
                break;
            }
            case REFRESHING:{
                materialProgressView.setVisibility(View.VISIBLE);
                if (!drawable.isRunning()) {
                    drawable.showArrow(false);
                    drawable.setAlpha(0xFF);
                    drawable.start();
                }
                break;
            }
            case NONE:{
                materialProgressView.setVisibility(View.INVISIBLE);
                stopArrowDrawable(drawable);
                break;
            }
        }
    }

    private void stopArrowDrawable(MaterialProgressDrawable drawable) {
        drawable.stop();
        drawable.setArrowScale(1f);
        drawable.showArrow(true);
    }

    @Override
    public void onRefreshComplete() {
    }
}
