## Wheel

> This is wheel view. It's based on AbsCenterLayout. We allow you layout view infinite naturally.

Be careful. If there are less than three views.
We will create the view twice. Like this example: [A B] <br>
If you want to loop this data. We will created each item twice.<br>
The first time looks like this:[B,A,B] Then after you move to the next position. It will like this:[A,B,A]<br>



#### Example classes:

* [WheelSampleActivity](app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/wheel/WheelSampleActivity.kt)
* [WheelVerticalDateAdapter](app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/wheel/WheelVerticalDateAdapter.kt)
* [WheelHorizontalDateAdapter](app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/wheel/WheelHorizontalDateAdapter.kt)
* [CenterItemDecoration](app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/wheel/CenterItemDecoration.kt)

#### Image
![wheel](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/layoutmanager/wheel.gif?raw=true)

### Usage

```
//Change the current position manually
wheel1.setCurrentItem(seekYear.progress)

//Add the item decration.
wheel1.addItemDecoration(CenterItemDecoration(this))

val onSelectPositionChangedListener = object : AbsCycleLayout.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        if(-1!=wheel1.currentItem&&-1!=wheel2.currentItem&&-1!=wheel3.currentItem){
            val year = dateAdapter1.getItem(wheel1.currentItem)
            val month = dateAdapter2.getItem(wheel2.currentItem)
            val day = dateAdapter3.getItem(wheel3.currentItem)
            dateText.text = getString(
                R.string.date_value, year,
                formatter.format(month),
                formatter.format(day)
            )
        }
    }
}
//Add scroll change listener.
wheel1.addOnPageChangeCallback(onSelectPositionChangedListener)


//The page transform
val pageTransformer = AbsCycleLayout.PageTransformer { page, fraction ->
    Log.i("test","fraction:"+fraction)
    val textView = page as TextView
    textView.scaleX=1f+0.4f*fraction
    textView.scaleY=1f+0.4f*fraction
    textView.setTextColor(evaluatorCompat.evaluate(fraction,Color.WHITE, Color.RED))
}
wheel1.setPageTransformer(pageTransformer)
```

For a wheel it's not like the another view.

Cause we re-measure the size by wheel count.

```
The code is more this:
int orientation = getOrientation();
if (0 != itemCount&&!state.isPreLayout()) {
    OrientationHelper orientationHelper = getOrientationHelper();
    for(int i=0;i<wheelCount;i++){
        View view = recycler.getViewForPosition(i);
        //We measure the view by different orientation.
        if(HORIZONTAL==orientation){
            //Only measure the width unspecified. But measure the child view's height by parent's mode.
            measurePreLayoutChild(view, View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            measureContentWidth+=orientationHelper.getDecoratedMeasurement(view);
            measureContentHeight=orientationHelper.getDecoratedMeasurementInOther(view);
        } else {
            //Only measure the height unspecified. But measure the child view's width by parent's mode.
            measurePreLayoutChild(view, View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            measureContentWidth=orientationHelper.getDecoratedMeasurementInOther(view);
            measureContentHeight+=orientationHelper.getDecoratedMeasurement(view);
        }
        recycler.recycleView(view);
        //Since we recycle this view. But for next time.If want this view re-measured. We should request layout.
        //Take a look at:RecyclerView.LayoutManager.shouldMeasureChild
        view.requestLayout();
    }
}
```

It's base on AbsCenterLayout. So take a look at that class file.
