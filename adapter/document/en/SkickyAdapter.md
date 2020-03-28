## StickyAdapter

> It's an adapter for filter the list data. The only thing I did was abstract an object filter.

### Example classes:

* [StickySample1Activity](app/src/main/java/com/cz/widget/recyclerview/sample/sticky/StickySample1Activity.kt)
* [StickySimple1Adapter](app/src/main/java/com/cz/widget/recyclerview/sample/sticky/adapter/StickySimple1Adapter.kt)

* [StickySample2Activity](app/src/main/java/com/cz/widget/recyclerview/sample/sticky/StickySample2Activity.kt)
* [StickySimple2Adapter](app/src/main/java/com/cz/widget/recyclerview/sample/sticky/adapter/StickySimple2Adapter.kt)

* [StickySample3Activity](app/src/main/java/com/cz/widget/recyclerview/sample/sticky/StickySample3Activity.kt)
* [StickySimple3Adapter](app/src/main/java/com/cz/widget/recyclerview/sample/sticky/adapter/StickySimple3Adapter.kt)

#### Image
![sticky_adapter](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/adapter/sticky_adapter.gif?raw=true)

### Usage

```
# In adapter XML
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <FrameLayout
        android:id="@+id/stickyLayout"
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <TextView
            android:id="@+id/stickyHeaderView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="24sp"
            android:textColor="@android:color/white"
            android:paddingTop="12dp"
            android:paddingBottom="12dp"
            android:paddingLeft="12dp"
            android:paddingRight="12dp"
            android:background="@drawable/stick_header_selector"/>

        <Button
            android:id="@+id/button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="right"
            android:textAllCaps="false"
            android:text="Button"/>

    </FrameLayout>

    <TextView
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceListItemSmall"
        android:gravity="center_vertical"
        android:paddingStart="?android:attr/listPreferredItemPaddingStart"
        android:paddingEnd="?android:attr/listPreferredItemPaddingEnd"
        android:minHeight="?android:attr/listPreferredItemHeightSmall"
        android:background="@drawable/white_item_selector"/>

</LinearLayout>

#The sticky header xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/stickyHeaderView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="24sp"
        android:textColor="@android:color/white"
        android:paddingTop="12dp"
        android:paddingBottom="12dp"
        android:paddingLeft="12dp"
        android:paddingRight="12dp"
        android:background="@drawable/stick_header_selector"/>

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="right|center_vertical"
        android:textAllCaps="false"
        android:text="Button"/>

</FrameLayout>


override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    super.onBindViewHolder(holder, position)
    ...
    //Initialize the adpater and check show or hide the sticky header.
    val stickyLayout = holder.itemView.findViewById<View>(R.id.stickyLayout)
    stickyLayout.visibility=if(groupingStrategy.isGroupPosition(position)) View.VISIBLE else View.GONE

    val stickyHeaderView = holder.itemView.findViewById<TextView>(R.id.stickyHeaderView)
    val button = holder.itemView.findViewById<View>(R.id.button)

    val startPosition = groupingStrategy.getGroupIndex(position)
    stickyHeaderView.text = "Header:$startPosition"


    stickyHeaderView.setOnClickListener { view->
        Toast.makeText(holder.itemView.context,view.context.getString(R.string.click_position,position),Toast.LENGTH_SHORT).show()
    }
    button.setOnClickListener { view->
        Toast.makeText(holder.itemView.context,view.context.getString(R.string.click_button_position,position),Toast.LENGTH_SHORT).show()
    }
}
//Return the sticky header view
override fun onCreateStickyView(parent: ViewGroup, position: Int): View {
    return layoutInflater.inflate(R.layout.sticky_header_layout1, parent, false)
}

override fun onBindStickyView(view: View,viewType:Int, position: Int) {
    // Initialize the sticky header view.
    val stickyHeaderView=view.findViewById<TextView>(R.id.stickyHeaderView)
    val button = view.findViewById<View>(R.id.button)
    val startPosition = groupingStrategy.getGroupIndex(position)
    stickyHeaderView.text = "Header:$startPosition"
    stickyHeaderView.setOnClickListener {
        Toast.makeText(view.context,view.context.getString(R.string.click_position,position),Toast.LENGTH_SHORT).show()
    }

    button.setOnClickListener {
        Toast.makeText(view.context,view.context.getString(R.string.click_button_position,position),Toast.LENGTH_SHORT).show()
    }
}
```

### Here is how we group the adapter list.

```
// In adapter
override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    groupingStrategy.setCondition { _, position -> 0==position%5 }
}
```


### How ot group your adapter list

We use grouping strategy to group the adapter list.

Here are two different situations.

* The first one: group your adapter list by comparing every two different objects.

```
adapter.setCompareCondition { t1, t2 ->
    t1[0]!=t2[0]
}
```

* The second: Each object decides to group by itself.

```
adapter.setCondition { item, position ->
    item=="xxx"
}
```

This is only for StickyAdapter.

It's very easy to understand. But there is a little difference with the others.

We actually keep two same header views. I don't want to do that. So let take a look at StickyHeaderAapter.

```
val adapter = StickySimple2Adapter(this, list)
adapter.setCondition { item, _ -> 5 < item.position&&item.position%5==0 }
recyclerView.adapter= StickyWrapperAdapter(adapter)
```

That's it. Nothing changed.But in your adapter. you don't have to initialize the sticky header view twice.
And you don't have to add extra sticky view in your adapter layout xml file.

```
override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    super.onBindViewHolder(holder, position)
    val textView = holder.itemView.findViewById<TextView>(android.R.id.text1)
    val item = getItem(position)
    if (null != item) {
        textView.text = item.toString()
    }
}

override fun onCreateStickyView(parent: ViewGroup, position: Int): View {
    return layoutInflater.inflate(R.layout.sticky_header_layout2, parent, false)
}

override fun onBindStickyView(view: View,viewType:Int, position: Int) {
    val stickyHeaderView=view.findViewById<TextView>(R.id.stickyHeaderView)
    val startPosition = groupingStrategy.getGroupIndex(position)
    stickyHeaderView.text = "Header:$startPosition"
    stickyHeaderView.setOnClickListener {
        Toast.makeText(view.context,view.context.getString(R.string.click_position,position),Toast.LENGTH_SHORT).show()
    }
}
```


### Performance test.

Each time when we scroll and change the position. If we occur the sticky header we only create once. Whatever move forward or move backward

```
2020-03-26 22:00:56.340 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move forward firstVisibleItemPosition:10 lastStickyItemPosition:-1 newPosition:10
2020-03-26 22:00:57.747 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move forward firstVisibleItemPosition:16 lastStickyItemPosition:10 newPosition:16
2020-03-26 22:00:58.583 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move backward firstVisibleItemPosition:15 lastStickyItemPosition:16 newPosition:10
2020-03-26 22:00:59.986 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move forward firstVisibleItemPosition:16 lastStickyItemPosition:10 newPosition:16
2020-03-26 22:01:01.057 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move forward firstVisibleItemPosition:22 lastStickyItemPosition:16 newPosition:22
2020-03-26 22:01:01.792 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move forward firstVisibleItemPosition:28 lastStickyItemPosition:22 newPosition:28
2020-03-26 22:01:02.980 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move forward firstVisibleItemPosition:34 lastStickyItemPosition:28 newPosition:34
2020-03-26 22:01:04.418 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move forward firstVisibleItemPosition:40 lastStickyItemPosition:34 newPosition:40
2020-03-26 22:01:05.057 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move backward firstVisibleItemPosition:39 lastStickyItemPosition:40 newPosition:34
2020-03-26 22:01:06.041 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move backward firstVisibleItemPosition:33 lastStickyItemPosition:34 newPosition:28
2020-03-26 22:01:06.911 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move backward firstVisibleItemPosition:27 lastStickyItemPosition:28 newPosition:22
2020-03-26 22:01:07.582 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move backward firstVisibleItemPosition:21 lastStickyItemPosition:22 newPosition:16
2020-03-26 22:01:07.713 4103-4103/com.cz.widget.recyclerview.sample I/StickyRecyclerViewScrollListener: move backward firstVisibleItemPosition:15 lastStickyItemPosition:16 newPosition:10
```

When we have to change the sticky header view. We will recycler the last visible sticky view immediately.
And put the recycled header view to the next. If they are the same list view type.

```
View lastStickyView = stickyOverlay.findStickyView(lastStickyItemPosition);
if (null != lastStickyView) {
    int stickyViewType = adapter.getStickyViewType(lastStickyItemPosition);
    recyclerBin.recycler(stickyViewType,lastStickyView);
}
```

We have a recycler pool. So you don't have to worry about the performance.


