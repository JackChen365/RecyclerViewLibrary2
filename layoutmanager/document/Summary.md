## Summary

* About how to change the scroll duration.

> We simply change the scroll duration in RecyclerView#smoothScrollBy(int dx, int dy, @Nullable Interpolator interpolator, int duration)

Here when we scroll the pager as a banner. We override this function and change the animation duration.

```
/**
 * We change the smooth scroll duration When we scroll the page automatically
 * @param dx
 * @param dy
 * @param interpolator
 * @param duration
 */
@Override
public void smoothScrollBy(int dx, int dy, @Nullable Interpolator interpolator, int duration) {
    if(autoScroll){
        super.smoothScrollBy(dx, dy, interpolator, scrollDuration);
    } else {
        super.smoothScrollBy(dx,dy,interpolator,duration);
    }
}
```