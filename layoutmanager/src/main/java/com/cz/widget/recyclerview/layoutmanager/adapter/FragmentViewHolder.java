package com.cz.widget.recyclerview.layoutmanager.adapter;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/**
 * {@link ViewHolder} implementation for handling {@link Fragment}s. Used in
 * {@link FragmentStateAdapter}.
 */
public final class FragmentViewHolder extends ViewHolder {
    private FragmentViewHolder(@NonNull FrameLayout container) {
        super(container);
    }

    @NonNull
    static FragmentViewHolder create(@NonNull ViewGroup parent) {
        FrameLayout container = new FrameLayout(parent.getContext());
        container.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        container.setId(ViewCompat.generateViewId());
        container.setSaveEnabled(false);
        return new FragmentViewHolder(container);
    }

    @NonNull
    FrameLayout getContainer() {
        return (FrameLayout) itemView;
    }
}
