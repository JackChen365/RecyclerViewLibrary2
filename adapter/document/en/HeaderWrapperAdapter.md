## HeaderWrapperAdapter

> It's a wrapper adapter to add extra header and a footer view.

#### Example classes:

* [HeaderSampleActivity](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/HeaderSampleActivity.kt)

#### Image
![image](../image/header_adapter.gif)

#### Usage

```
val adapter = SimpleAdapter(this, list)
//wrap the adapter by HeaderWrapperAdapter.
val wrapperAdapter=HeaderWrapperAdapter(adapter)
//Add the extra header or footer view.
wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter))
wrapperAdapter.addFooterView(getFooterView(wrapperAdapter))
recyclerView.adapter=wrapperAdapter
```

Before we get started. We are not only implementing one adapter. We actually have a few adapters to support a bunch of functions.

For only one adapter. You care about one thing. The header and footer view. But in this adapter. We did a little more.

First for a wrapper adapter. We change the real position in class: RecyclerView.AdapterDataObserver

```
public class WrapperAdapterDataObserver<E extends WrapperAdapter> extends RecyclerView.AdapterDataObserver {
    private E wrapperAdapter;

    public WrapperAdapterDataObserver(E mWrapAdapter) {
        this.wrapperAdapter = mWrapAdapter;
    }

    @Override
    public void onChanged() {
        wrapperAdapter.onChanged();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        //Here we change the start position.
        wrapperAdapter.itemRangeInsert(positionStart, itemCount);
    }
    ...
}
```

Make a long story short. If you want to wrap an adapter and support more function. We have to know the real adapter position.

#### Functions

| Function prototype | Sample | Note |
| ------ | ------ | ------ |
| HeaderWrapperAdapter.addHeaderView(view,index) | adapter.addHeaderView(view,0) | add a new header view |
| HeaderWrapperAdapter.addFooterView(view,index) | adapter.addFooterView(view,0) | add a new footer view |


#### How did I do this?

We made an interface. It has two functions.

1. WrapperAdapter#getOffsetPosition(int position)

This functions return an adapter position plus the extra view count.
For example:

```
header1
header2
data1
data2
...
```

If you call adapter.getOffsetPosition(1). Here the position:1 was data2. Because you are in your own adapter.
you didn't aware that you wrap by the HeaderWrapperAdapter. Here we call getOffsetPosition(1) it return 3.

2. WrapperAdapter#getExtraViewCount(int position)

At the same place. If you want to know how many extra adapters view behind you. Call this function. It only returns the size of the extra view.
In that case. The HeaderWrapperAdapter will return the header view size.
But for the sub-class:[DynamicWrapperAdapter](DynamicWrapperAdapter.md). It could override this function and return some extra views.

```
@Override
public int getExtraViewCount(int position){
    int extraViewCount = super.getExtraViewCount(position);
    int start = 0;
    int result = RecyclerView.NO_POSITION;
    if(null!=itemArray){
        int end = itemArray.length - 1;
        while (start <= end) {
            int middle = (start + end) / 2;
            if (position == itemArray[middle]) {
                result = middle;
                break;
            } else if (position < itemArray[middle]) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
    }
    if (RecyclerView.NO_POSITION == result) {
        result = start;
    }
    return extraViewCount+result;
}
```

3. We keep a negative counter to allocate the view type. There are two list to store the extra views.

```
private final int TYPE_EXTRAS = -1;//We decrease the number from negative one.
private final List<FixedViewInfo> headerViewArray=new ArrayList<>();
private final List<FixedViewInfo> footerViewArray=new ArrayList<>();
/**
 * We use this value as a counter.
 */
private int fixedViewCount=0;
```


### The class:WrapperAdapter prototype looks like this code below.


```
public interface WrapperAdapter {
    /**
     * Return the original adapter.
     * @return
     */
    RecyclerView.Adapter getAdapter();

    /**
     * If we move the position somehow. We should return the real position when it changed.
     * @see androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
     * @param position
     * @return
     */
    int getOffsetPosition(int position);

    int getExtraViewCount(int position);

    void onChanged();
    ...
}
```

Other things we should consider.

If we put this adapter in the GridLayoutManager. we want to keep the extra layout full the line.

```
RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
if (manager instanceof GridLayoutManager) {
    final GridLayoutManager gridManager = ((GridLayoutManager) manager);
    gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return isExtraPosition(position) ? gridManager.getSpanCount() : 1;
        }
    });
}

/**
 * For a {@link androidx.recyclerview.widget.GridLayoutManager} You might want to add a special column that is full.
 * So override this function and return true
 * @param position
 * @return
 */
protected boolean isExtraPosition(int position){
    return isHeaderPosition(position) || isFooterPosition(position);
}
```


#### Manipulate the footer view

Here if we want to have a bottom-line view or a bottom loading view.

You can extend from this class. keep the bottom view. and never remove it.