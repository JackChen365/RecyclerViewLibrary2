package com.cz.widget.recyclerview.sample.adapter.impl;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.adapter.support.cursor.CursorRecyclerAdapter;
import com.cz.widget.recyclerview.sample.R;

/**
 * Created by cz
 * @date 2020-03-19 13:25
 * @email bingo110@126.com
 */
public class CursorSampleAdapter extends CursorRecyclerAdapter<CursorSampleAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;

    public CursorSampleAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor, int position) {
        holder.bind(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.adapter_simple_text_item, parent, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Cursor cursor){
            TextView textView = (TextView) itemView;
            String text = cursor.getString(1);
            textView.setText(text);
        }
    }
}