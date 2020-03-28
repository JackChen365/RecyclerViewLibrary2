package com.cz.widget.recyclerview.sample.adapter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.adapter.support.expand.ExpandAdapter;
import com.cz.widget.recyclerview.sample.R;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by cz
 * @date 2020-03-18 18:59
 * @email bingo110@126.com
 */
public class ExpandSampleAdapter extends ExpandAdapter<String, String> {
    private final LayoutInflater layoutInflater;

    public ExpandSampleAdapter(Context context,LinkedHashMap<String, List<String>> map) {
        super(map);
        this.layoutInflater=LayoutInflater.from(context);
    }

    public ExpandSampleAdapter(Context context,LinkedHashMap<String, List<String>> map, boolean expand) {
        super(map, expand);
        this.layoutInflater=LayoutInflater.from(context);
    }

    /**
     * Create the setCompareCondition view holder.
     * @param parent
     * @return
     */
    @Override
    public RecyclerView.ViewHolder createGroupHolder(ViewGroup parent) {
        return new GroupHolder(layoutInflater.inflate(R.layout.adapter_expand_group_layout,parent, false));
    }

    /**
     * Create the sub-node view holder
     * @param parent
     * @return
     */
    @Override
    public RecyclerView.ViewHolder createChildHolder(ViewGroup parent) {
        return new ItemHolder(layoutInflater.inflate(R.layout.adapter_expand_layout,parent, false));
    }

    /**
     * Binding the setCompareCondition header view holder with the data.
     * @param holder
     * @param groupPosition
     */
    @Override
    public void onBindGroupHolder(RecyclerView.ViewHolder holder, int groupPosition) {
        GroupHolder groupHolder = (GroupHolder) holder;
        groupHolder.imageFlag.setSelected(getGroupExpand(groupPosition));
        groupHolder.textView.setText(getGroup(groupPosition));
        groupHolder.count.setText("(" + getChildrenCount(groupPosition) + ")");
    }

    /**
     * Binding the sub-view with the data.
     * @param holder
     * @param groupPosition
     * @param childPosition
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onBindChildHolder(RecyclerView.ViewHolder holder, int groupPosition, int childPosition) {
        ItemHolder itemHolder = (ItemHolder) holder;
        String item = getChild(groupPosition, childPosition);
        itemHolder.textView.setText(item);
        return null;
    }

    /**
     * When one setCompareCondition expands or collapses. You could either change the arrowhead or something here.
     * @param holder
     * @param expand
     * @param groupPosition
     */
    @Override
    protected void onGroupExpand(RecyclerView.ViewHolder holder, boolean expand, int groupPosition) {
        super.onGroupExpand(holder, expand, groupPosition);
        GroupHolder groupHolder = (GroupHolder) holder;
        groupHolder.imageFlag.setSelected(expand);
    }

    public static class GroupHolder extends RecyclerView.ViewHolder {
        public ImageView imageFlag;
        public TextView textView;
        public TextView count;

        public GroupHolder(View itemView) {
            super(itemView);
            imageFlag = itemView.findViewById(R.id.iv_group_flag);
            textView = itemView.findViewById(R.id.tv_group_name);
            count = itemView.findViewById(R.id.tv_group_count);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ItemHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }

}