## FilterAdapter

> It's an adapter for filter the list data. The only thing I did was abstract an object filter.

### Example classes:

* [FilterSampleActivity](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/FilterSampleActivity.kt)
* [SimpleFilterAdapter](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/impl/SimpleFilterAdapter.kt)

#### Image
![image](../image/filter_adapter.gif)

### Usage

```
val adapter = SimpleFilterAdapter(this, list)
recyclerView.adapter = adapter

editView.addTextChangedListener(object :TextWatcher{
    override fun afterTextChanged(s: Editable?) {
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //Here we filter the list data by the query word.
        adapter.filter.filter(s)
    }
})
```

## This is how it work

```
class ObjectFilter extends Filter {
    @Override
    protected FilterResults performFiltering(CharSequence word) {
        FilterResults filterResults=new FilterResults();
        List<E> resultItems=new ArrayList<>();
        List<E> itemList = getItemList();
        if (TextUtils.isEmpty(word)) {
            queryWord=null;
        } else {
            queryWord=word.toString();
            for(E item:itemList){
                if(filterObject(item,word)){
                    resultItems.add(item);
                }
            }
        }
        filterResults.count=resultItems.size();
        filterResults.values=resultItems;
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        publishItems(constraint, (List<E>) results.values);
    }

    private void publishItems(CharSequence constraint, List<E> resultItems) {
        if(!TextUtils.isEmpty(constraint)&&null!=resultItems) {
            queryList.clear();
            queryList.addAll(resultItems);
        }
        notifyDataSetChanged();
    }

}
```

It's very easy to understand. But there is a little difference with the others.

We override the a few functions.

```
@Override
public int getItemCount() {
    if(!TextUtils.isEmpty(queryWord)){
        return queryList.size();
    } else {
        return super.getItemCount();
    }
}

@Override
public E getItem(int position) {
    if(!TextUtils.isEmpty(queryWord)){
        return queryList.get(position);
    } else {
        return super.getItem(position);
    }
}
```

In that case, we could save a list copy. We keep the original list.

Here is an example.

```
// In the adapter if you want to highlight the keyword.
override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    ...
    if (null==queryWord) {
        textView.text = itemValue
    } else {
        val spannable = SpannableString(itemValue)
        val index = itemValue.indexOf(queryWord)
        spannable.setSpan(ForegroundColorSpan(Color.RED),index, index + queryWord.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannable, TextView.BufferType.SPANNABLE)
    }
}
```


That's it. Really easy but useful.

