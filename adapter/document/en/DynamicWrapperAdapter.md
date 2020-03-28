## DynamicWrapperAdapter

> It's an enhanced adapter for the HeaderWrapperAdapter. The HeaderWrapperAdapter only allow you add view as a header or footer.
> This Adapter support you insert layout in anywhere.


#### Example classes:

* [DynamicSampleActivity](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/DynamicSampleActivity.kt)

#### Image
![image](../image/dynamic_adapter.gif)

### How does it work

As a wrapper adapter. We manipulate all the positions.

Here are an example:

```
The original adapter was ["a","b","c","d","e","f"]

If we want to insert a word in position:2. Then the list will turn to.
["a","b",|"xx"|,"c","d","e","f"]

So far it's not very difficult. But remember we are not add the word in this list.
We actually have our own list to store the word.

The list will looks like this one:["xx":2]
The original list didn't change:["a","b","c","d","e","f"]
```

The second step:

```
@Override
public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    //Check if the position was an insert one.
    int findPosition = findPosition(position);
    if(RecyclerView.NO_POSITION==findPosition){
        //If we insert a layout into position:3
        //The list was:0,1,2 {3} 4
        //So we have to find the extra view count. When the position was 4.
        //We have to know how many view we insert into the adapter. Here was only one. So we got three.
        int extrasViewCount = getExtraViewCount(position);
        //Here we reduce the extra view count.
        position-=extrasViewCount;
        super.onBindViewHolder(holder, position);
    }
}
```

The segment code above was how we keep the wrapped adapter orderly.

Let's dig it a little deeper.

```
The list will looks like this one:["xx":2]
The original list didn't change:["a","b","c","d","e","f"]

When the position keep increasing.
After position:two we got position:three.
We have to know there is one extra layout in front of it.
We changed the binary search a little. The algorithm was below.

So when the position is number:3, We could know there is one extra view.
Thus. We keep the adapter orderly.

```

### How to return the start extra view size.

```
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

### Usage

```
val simpleAdapter= SimpleAdapter(this, createList(30))
val dynamicAdapter = DynamicWrapperAdapter(simpleAdapter)
recyclerView.adapter = dynamicAdapter

//Add view to somewhere.
val itemView = getFullItemView(dynamicAdapter)
dynamicAdapter.addView(itemView,index)
//Remove view.
dynamicAdapter.removeView(0)
dynamicAdapter.removeView(view)
```

This adapter is actually a powerful adapter.
We won't change your own adapter but you could insert anything into the adapter.

There is one example cooperate with the DragWrapperAdapter.

The news channel example:[DragNewsSampleActivity](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/DragNewsSampleActivity.kt)

More detail check the class:[DynamicAdapterAdapter](adapter/src/main/java/com/cz/widget/recyclerview/adapter/wrapper/dynamic/DynamicWrapperAdapter.java )



