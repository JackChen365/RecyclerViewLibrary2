package com.cz.widget.recyclerview.layoutmanager.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Dispatches {@link AbsCycleLayout.OnPageChangeCallback} events to subscribers.
 */
final class CompositeOnPageChangeCallback extends AbsCycleLayout.OnPageChangeCallback {
    @NonNull
    private final List<AbsCycleLayout.OnPageChangeCallback> mCallbacks;

    CompositeOnPageChangeCallback(int initialCapacity) {
        mCallbacks = new ArrayList<>(initialCapacity);
    }

    /**
     * Adds the given callback to the list of subscribers
     */
    void addOnPageChangeCallback(AbsCycleLayout.OnPageChangeCallback callback) {
        mCallbacks.add(callback);
    }

    /**
     * Removes the given callback from the list of subscribers
     */
    void removeOnPageChangeCallback(AbsCycleLayout.OnPageChangeCallback callback) {
        mCallbacks.remove(callback);
    }

    /**
     * @see AbsCycleLayout.OnPageChangeCallback#onPageScrolled(int, float, int)
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, @Px int positionOffsetPixels) {
        try {
            for (AbsCycleLayout.OnPageChangeCallback callback : mCallbacks) {
                callback.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        } catch (ConcurrentModificationException ex) {
            throwCallbackListModifiedWhileInUse(ex);
        }
    }

    /**
     * @see AbsCycleLayout.OnPageChangeCallback#onPageSelected(int)
     */
    @Override
    public void onPageSelected(int position) {
        try {
            for (AbsCycleLayout.OnPageChangeCallback callback : mCallbacks) {
                callback.onPageSelected(position);
            }
        } catch (ConcurrentModificationException ex) {
            throwCallbackListModifiedWhileInUse(ex);
        }
    }

    /**
     * @see AbsCycleLayout.OnPageChangeCallback#onPageScrollStateChanged(int)
     */
    @Override
    public void onPageScrollStateChanged(@AbsCycleLayout.ScrollState int state) {
        try {
            for (AbsCycleLayout.OnPageChangeCallback callback : mCallbacks) {
                callback.onPageScrollStateChanged(state);
            }
        } catch (ConcurrentModificationException ex) {
            throwCallbackListModifiedWhileInUse(ex);
        }
    }

    private void throwCallbackListModifiedWhileInUse(ConcurrentModificationException parent) {
        throw new IllegalStateException(
                "Adding and removing callbacks during dispatch to callbacks is not supported",
                parent
        );
    }

}
