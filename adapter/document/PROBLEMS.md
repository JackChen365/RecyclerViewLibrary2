## Problems

* The DragWrapperAdapter

When the position moved. From an old position to a new position. But your finger still hold.
Here if you call function:notifyDataSetChanged(position) It's will drop automatically in AndroidX.
But It won't drop in Android v7. So I decide not to call this function.

```
//After you move to a new adapter position. If you want to refresh the position.
dragAdapter.move(adapterPosition, count + 1)
dragAdapter.notifyItemChanged(count+1)
```