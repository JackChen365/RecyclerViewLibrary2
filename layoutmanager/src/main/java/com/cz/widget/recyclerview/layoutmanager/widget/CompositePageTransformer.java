package com.cz.widget.recyclerview.layoutmanager.widget;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows for combining multiple {@link AbsCycleLayout.PageTransformer} objects.
 *
 * @see AbsCycleLayout#setPageTransformer
 * @see MarginPageTransformer
 */
public final class CompositePageTransformer implements AbsCycleLayout.PageTransformer {
    private final List<AbsCycleLayout.PageTransformer> mTransformers = new ArrayList<>();

    /**
     * Adds a page transformer to the list.
     * <p>
     * Transformers will be executed in the order that they were added.
     */
    public void addTransformer(@NonNull AbsCycleLayout.PageTransformer transformer) {
        mTransformers.add(transformer);
    }

    /** Removes a page transformer from the list. */
    public void removeTransformer(@NonNull AbsCycleLayout.PageTransformer transformer) {
        mTransformers.remove(transformer);
    }

    @Override
    public void transformPage(@NonNull View page, float fraction) {
        float absPosition=Math.abs(fraction);
        float animationFraction = (absPosition > 1) ? 0f : (1f-absPosition);
        for (AbsCycleLayout.PageTransformer transformer : mTransformers) {
            transformer.transformPage(page, animationFraction);
        }
    }
}
