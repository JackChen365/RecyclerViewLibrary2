package com.cz.widget.recyclerview.layoutmanager.widget;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adds space between pages via the {@link AbsCycleLayout.PageTransformer} API.
 * <p>
 * Internally relies on {@link View#setTranslationX} and {@link View#setTranslationY}.
 * <p>
 * Note: translations on pages are not reset when this adapter is changed for another one, so you
 * might want to set them manually to 0 when dynamically switching to another transformer, or
 * when switching AbsCycleLayout orientation.
 *
 * @see AbsCycleLayout#setPageTransformer
 * @see CompositeOnPageChangeCallback
 */
public final class MarginPageTransformer implements AbsCycleLayout.PageTransformer {
    private final int mMarginPx;

    /**
     * Creates a {@link MarginPageTransformer}.
     *
     * @param marginPx non-negative margin
     */
    public MarginPageTransformer(@Px int marginPx) {
        Preconditions.checkArgumentNonnegative(marginPx, "Margin must be non-negative");
        mMarginPx = marginPx;
    }

    @Override
    public void transformPage(@NonNull View page, float fraction) {
        AbsCycleLayout viewPager = requireViewPager(page);

        float offset = mMarginPx * fraction;

        if (viewPager.getOrientation() == AbsCycleLayout.ORIENTATION_HORIZONTAL) {
            page.setTranslationX(viewPager.isRtl() ? -offset : offset);
        } else {
            page.setTranslationY(offset);
        }
    }

    private AbsCycleLayout requireViewPager(@NonNull View page) {
        ViewParent parent = page.getParent();
        ViewParent parentParent = parent.getParent();

        if (parent instanceof RecyclerView && parentParent instanceof AbsCycleLayout) {
            return (AbsCycleLayout) parentParent;
        }

        throw new IllegalStateException(
                "Expected the page view to be managed by a AbsCycleLayout instance.");
    }
}
