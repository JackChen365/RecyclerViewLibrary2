package com.cz.widget.recyclerview.sample.adapter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.adapter.BaseAdapter;
import com.cz.widget.recyclerview.adapter.wrapper.select.Selectable;
import com.cz.widget.recyclerview.sample.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cz on 16/1/23.
 * @warning select adapter must be implement Selectable
 * @see Selectable
 */
public class SimpleSelectAdapter<E> extends BaseAdapter<SimpleSelectAdapter.ViewHolder,E> implements Selectable<SimpleSelectAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private int layout;

    public static SimpleSelectAdapter createFromResource(Context context, @ArrayRes int res) {
        return new SimpleSelectAdapter(context, context.getResources().getStringArray(res));
    }

    public SimpleSelectAdapter(Context context, E[] items) {
        this(context, R.layout.adapter_select_text_item, Arrays.asList(items));
    }

    public SimpleSelectAdapter(Context context, @LayoutRes int layout, E[] items) {
        this(context, layout, Arrays.asList(items));
    }

    public SimpleSelectAdapter(Context context, List<E> items) {
        this(context, R.layout.adapter_select_text_item, items);
    }

    public SimpleSelectAdapter(Context context, @LayoutRes int layout, List<E> items) {
        super(items);
        this.layoutInflater=LayoutInflater.from(context);
        this.layout = layout;
    }

    @Override
    public void onSelectItem(SimpleSelectAdapter.ViewHolder holder, int position, boolean select) {
        holder.itemView.setSelected(select);
    }

    @Override
    public SimpleSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(layout,parent,false));
    }

    @Override
    public void onBindViewHolder(SimpleSelectAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        E item = getItem(position);
        holder.bind(item);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(E item){
            TextView textView = (TextView) itemView;
            if (null != item) {
                textView.setText(item.toString());
            }
        }
    }
}