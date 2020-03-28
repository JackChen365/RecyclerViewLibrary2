package com.cz.widget.recyclerview.layoutmanager.widget;

import android.view.View;

import androidx.annotation.Nullable;

import com.cz.widget.recyclerview.layoutmanager.base.CenterLayoutManager;

import java.util.Locale;

/**
 * Translates {@link AbsCycleLayout.OnPageChangeCallback} events to {@link AbsCycleLayout.PageTransformer} events.
 */
final class PageTransformerAdapter extends AbsCycleLayout.OnPageChangeCallback {
    private final CenterLayoutManager mLayoutManager;

    private AbsCycleLayout.PageTransformer mPageTransformer;

    PageTransformerAdapter(CenterLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    AbsCycleLayout.PageTransformer getPageTransformer() {
        return mPageTransformer;
    }

    /**
     * Sets the PageTransformer. The page transformer will be called for each attached page whenever
     * the scroll position is changed.
     *
     * @param transformer The PageTransformer
     */
    void setPageTransformer(@Nullable AbsCycleLayout.PageTransformer transformer) {
        // TODO: add support for reverseDrawingOrder: b/112892792
        // TODO: add support for pageLayerType: b/112893074
        mPageTransformer = transformer;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPageTransformer == null) {
            return;
        }
        float transformOffset = -positionOffset;
        for (int i = 0; i < mLayoutManager.getChildCount(); i++) {
            View view = mLayoutManager.getChildAt(i);
            if (view == null) {
                throw new IllegalStateException(String.format(Locale.US,
                        "LayoutManager returned a null child at pos %d/%d while transforming pages",
                        i, mLayoutManager.getChildCount()));
            }
            int currPos = mLayoutManager.getPosition(view);
            float viewOffset = transformOffset + (currPos - position);
            float absFraction = Math.abs(viewOffset);
            float animationFraction = (absFraction > 1) ? 0f : (1f-absFraction);
            mPageTransformer.transformPage(view, animationFraction);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
