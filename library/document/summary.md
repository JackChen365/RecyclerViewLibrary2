## Summary

* Find a view by the point x,y

```
View findViewInternal(float x,float y){
    View findView=null;
    int childCount = getChildCount();
    for(int i=0;i<childCount;i++){
        View childView = getChildAt(i);
        //inside the rect.
        if(childView.getLeft() <= x && childView.getTop() <= y &&
                childView.getRight() >= x && childView.getBottom() >= y) {
            findView=childView;
            break;
        }
    }
    return findView;
}
```


* Find a clickable view.

```
private View findClickableViewInternal(View view,float x,float y){
    View findView=null;
    if(view.isClickable()||view.isLongClickable()){
        return view;
    }
    if(view instanceof ViewGroup){
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        //We reverse traversal this list. Because of the order of the view.
        for(int i=childCount-1;i>=0;i--){
            View childView = viewGroup.getChildAt(i);
            //inside the rect.
            if(childView.getLeft() <= x && childView.getTop() <= y &&
                    childView.getRight() >= x && childView.getBottom() >= y) {
                findView=findClickableViewInternal(childView,x,y);
                break;
            }
        }
    }
    return findView;
}
```

