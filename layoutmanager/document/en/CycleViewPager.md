## CycleViewPager

> This view pager only for loop like a banner. We could control the scroll duration and infinite loop.

#### Example classes:

* [CycleViewPagerSampleActivity](app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/viewpager/CycleViewPagerSampleActivity.kt)
* [ViewPagerImageAdapter](app/src/main/java/com/cz/widget/recyclerview/sample/layoutmanager/viewpager/adapter/ViewPagerImageAdapter.kt)

#### Image
![viewpager](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/layoutmanager/viewpager.gif?raw=true)

### Usage

```
//Change the current position manually
viewpager.setCurrentItem(seekYear.progress)

//Enable or disable the user input.
disableCheckbox.setOnCheckedChangeListener { _, isDisabled ->
    viewPager.isUserInputEnabled = !isDisabled
}
//Change the orientation.
viewPager.orientation = position

viewPager.isInfiniteCycle=isChecked

val onSelectPositionChangedListener = object : AbsCycleLayout.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)

    }
}
//Add scroll change listener.
viewpager.addOnPageChangeCallback(onSelectPositionChangedListener)


//The page transform
val pageTransformer = AbsCycleLayout.PageTransformer { page, fraction ->
    Log.i("test","fraction:"+fraction)
    val textView = page as TextView
    textView.scaleX=1f+0.4f*fraction
    textView.scaleY=1f+0.4f*fraction
    textView.setTextColor(evaluatorCompat.evaluate(fraction,Color.WHITE, Color.RED))
}
viewpager.setPageTransformer(pageTransformer)
```

We could easily use this as a banner.

```
//start to loop
cycleViewPager.startAutoScroll()
//stop loop
cycleViewPager.stopAutoScroll()
```

How to change the attributes.

```
<com.cz.widget.recyclerview.layoutmanager.viewpager.CycleViewPager
        android:id="@+id/cycleViewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        <!-- The orientation. -->
        android:oritation="horizontal/vertical"
        <!-- How long from one page scroll to another.-->
        app:pager_autoScrollDuration="3000"
        <!-- The page scroll interval time.-->
        app:pager_autoScrollInterval="5000"/>
```

About fake drag

```
dragView.setOnTouchListener { _, event ->
    handleOnTouchEvent(event)
}

private fun handleOnTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            lastValue = getValue(event)
            cycleViewPager.beginFakeDrag()
        }

        MotionEvent.ACTION_MOVE -> {
            val value = getValue(event)
            val delta = value - lastValue
            cycleViewPager.fakeDragBy(if (cycleViewPager.isHorizontal) mirrorInRtl(delta) else delta)
            lastValue = value
        }

        MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
            cycleViewPager.endFakeDrag()
        }
    }
    return true
}
```

Because we implement the layout manager by myself. So We do have a few flaws.

For ViewPager2, it use LinearLayoutManager to support the page limit.


```
androidx recycler view
Version 1.1.0-alpha05

API changes

Add API to retrieve DividerItemDecoration drawable (aosp/937282)
Deprecate LinearLayout.getExtraLayoutSpace(RecyclerVew.State) in favor of a new mechanism that allows to have custom extra layout space on both sides. The new method is LinearLayout.calculateExtraLayoutSpace(RecyclerView.state, int[]) (aosp/931259)


```
We use this function calculate the extra space. Unfortunately I didn't support this feature.
