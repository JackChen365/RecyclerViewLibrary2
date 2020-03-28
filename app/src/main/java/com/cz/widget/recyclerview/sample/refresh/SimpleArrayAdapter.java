package com.cz.widget.recyclerview.sample.refresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cz.widget.recyclerview.sample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020-01-28 18:37
 * @email bingo110@126.com
 */
public class SimpleArrayAdapter<E> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static SimpleArrayAdapter createFromResource(Context context, @ArrayRes int res){
        return new SimpleArrayAdapter(context, context.getResources().getStringArray(res));
    }

    public static<E> SimpleArrayAdapter createFromResource(Context context, E[] array){
        return new SimpleArrayAdapter(context, array);
    }

    public static<E> SimpleArrayAdapter createFromResource(Context context, List<E> list){
        return new SimpleArrayAdapter(context, list);
    }

    private final LayoutInflater layoutInflater;
    private @LayoutRes int layoutResources;
    private List<E> items= new ArrayList<>();

    public SimpleArrayAdapter(Context context, E[] items){
        this(context, R.layout.adapter_simple_text_item, Arrays.asList(items));
    }

    public SimpleArrayAdapter(Context context, @LayoutRes int layout, E[]  items){
        this(context, layout, Arrays.asList(items));
    }

    public SimpleArrayAdapter(Context context, List<E> items){
        this(context,R.layout.adapter_simple_text_item,items);
    }

    public SimpleArrayAdapter(Context context, @LayoutRes int layout, @NonNull List<E> items){
        this.layoutInflater = LayoutInflater.from(context);
        this.layoutResources = layout;
        this.items.addAll(items);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(layoutInflater.inflate(layoutResources,parent,false)) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        E item = getItem(position);
        if (null != item) {
            textView.setText(item.toString());
        }
    }

    public E getItem(int position){
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(E item){
        this.items.add(item);
        notifyItemInserted(items.size());
    }

    public void addItem(int index,E item){
        this.items.add(index,item);
        notifyItemInserted(index);
    }
}
