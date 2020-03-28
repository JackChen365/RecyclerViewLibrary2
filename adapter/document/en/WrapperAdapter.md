## WrapperAdapter

SomeThing about how to wrap a adapter.

We use a interface called:WrapperAdapter to wrap the original adapter.

The implementation we already have:
* DragWrapperAdapter
* DynamicWrapperAdapter
* SelectWrapperAdapter
* StickyWrapperAdapter
* HeaderWrapperAdapter.

The headerWrapperAdapter implement all the things from the interface:WrapperAdapter

The most important functions were:

```
/**
 * This function return a position that plus the extra view count.
 * If your position was:1. When you use {@link HeaderWrapperAdapter} we have 2 header views. We will return position:3.
 * @see androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
 * @param position
 * @return
 */
int getOffsetPosition(int position);

/**
 * Return the extra view count that behind this position
 * For example when you are use the {@link HeaderWrapperAdapter} when your position is:1
 * But we have two extra layout as the header layouts.
 * So the parameter was:1 we will return 2 cause they are two extra header layouts.
 *
 * It's so important for a wrapper adapter. We also use this function for {@link DynamicWrapperAdapter}
 * This function let the original adapter knows how much extra view that behind this position.
 * @param position
 * @return
 */
int getExtraViewCount(int position);
```

### For example
Try to implement a HeaderWrapperAdapter. We all know that we should take care of the wrapped adapter's position.

So in function:getExtraViewCount you just return the header's count.
And another function:getOffsetPosition we simply plus the header's count.

