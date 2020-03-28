## HeaderWrapperAdapter

> It's a same thing like the old ListView's ExpandListAdapter. But It's more flexible and easy to use.

### Example classes:

* [ExpandSampleActivity](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/adapter/ExpandSampleActivity.kt)
* [ExpandSampleAdapter](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/adapter/impl/ExpandSampleAdapter.java)

#### Image
![expand_adapter](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/adapter/expand_adapter.gif?raw=true)

### Usage

```
val adapter = ExpandSampleAdapter(this, createExpandItems(3, 2), true)
recyclerView.adapter = adapter

buttonExpandAll.setOnClickListener {
    adapter.expandAll()
}

buttonCollapseAll.setOnClickListener {
    adapter.collapseAll()
}
```


### The functions

| Function prototype | Sample | Note |
| ------ | ------ | ------ |
| ExpandAdapter.getGroupExpand(int position) | adapter.getGroupExpand(0) | Check is one group is expand or collapse |
| ExpandAdapter.expandAll() | adapter.expandAll() | expand all the nodes. |
| ExpandAdapter.collapseAll() | adapter.collapseAll() | close all the opened nodes. |
| setOnExpandItemClickListener(OnExpandItemClickListener listener) | setOnExpandItemClickListener(..) | handle the expand item click event. |
| ExpandAdapter.removeGroup(int, int) | adapter.removeGroup(0,0) | Remove a sub-node in one group |
| ExpandAdapter.removeGroup(int) | adapter.removeGroup(0) | Remove one group |
| ExpandAdapter.addGroup(K, java.util.List<E>, int, boolean) | adapter.addGroup("NewGroup",list,0,false/true) | Add a new group |

Let's take a look at an example of how to implement your own adapter.

```
public class ExpandSampleAdapter extends ExpandAdapter<String, String> {
    ...

    /**
     * Create the group view holder.
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
     * Binding the group header view holder with the data.
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
     * When one group expands or collapses. You could either change the arrowhead or something here.
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
}
```

you may aware of one thing that the ListView's ExpandListAdapter could be hard to change something like the arrowhead or the margin for the sub-node.

By use this ExpandAdapter. All the difficulty is gone.

One thing I have to mention. If you want to add extra header or footer. Take a look at [HeaderWrapperAdapter](HeaderWrapperAdapter.md)



