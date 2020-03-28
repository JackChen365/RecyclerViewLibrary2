## DragWrapperAdapter

> It's a drag adapter. It's actually an adapter support user to drag the layout.

#### Example classes:

* [DragGridSampleActivity](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/adapter/DragGridSampleActivity.kt)
* [DragNewsSampleActivity](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/adapter/DragNewsSampleActivity.kt)

#### Image
![drag_adapter](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/adapter/drag_adapter.gif?raw=true)

### Usage

```
val adapter = SimpleAdapter(this, list)
val dragAdapter = DragWrapperAdapter(adapter)
recyclerView.adapter = dragAdapter
```

That's all.

### How does it work

We use this callback: ItemTouchHelper.Callback control all the user gesture.
So we could have an abstract class:

```
public interface DragCallback{
    boolean isDragEnable(int position);
    /**
     * If a long pressed event enables the drag.
     * @return
     */
    boolean isLongPressDragEnabled();

    /**
     * Return the drag flag
     * @see ItemTouchHelper#DOWN
     * @see ItemTouchHelper#UP
     * @see ItemTouchHelper#LEFT
     * @see ItemTouchHelper#RIGHT
     * @return
     */
    int getDragFlag();
}

```

It's very easy. All the things are in the DragWrapperAdapter.

Here we attach the ItemTouchHelper to the RecyclerView.

```
@Override
public void onAttachedToRecyclerView(RecyclerView recyclerView) {
     super.onAttachedToRecyclerView(recyclerView);
     //Attach to the recycler view.
     RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
     if(layoutManager instanceof GridLayoutManager||layoutManager instanceof StaggeredGridLayoutManager){
         dragFlag=ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
     } else {
         dragFlag=ItemTouchHelper.UP|ItemTouchHelper.DOWN;
     }
     itemTouchHelper.attachToRecyclerView(recyclerView);
 }
```

### Manipulate all the views.

```
//2. Delegate the callback

//Here if you don't want the extra view drag.
dragAdapter.setFixedViewDragEnabled(false)

//We change the drag delegate and return drag status for each position.
dragAdapter.setDragDelegate(object : DefaultDragCallback() {
    override fun isDragEnable(position: Int): Boolean {
        //most important!!!
        val item=adapter.getItem(position)
        return item.use
    }
    //If you want to change the drag flag.
    override fun getDragFlag(): Int {
        return ItemTouchHelper.DOWN or ItemTouchHelper.UP
    }
})
```

### Cooperate with the DynamicWrapperAdapter

Because we have dynamic wrapper adapter. you could be able to add any views.
That's mean we could drag any views. Even though the view not in the your adapter.

```
val adapter = SimpleAdapter(this, list)
val dragAdapter = DragWrapperAdapter(adapter)
//Add extra view.
dragAdapter.addView(getFullItemView(dragAdapter),2)
recyclerView.adapter = dragAdapter

//Here if you don't want the extra view drag.
dragAdapter.setFixedViewDragEnabled(false)
```


