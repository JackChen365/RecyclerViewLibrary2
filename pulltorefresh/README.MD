## PullToRefresh

> This is a pull to refresh the layout. Not very complicated but powerful.

Here are the typical features we supports:
* Pull Strategy that you could be able to easily have your own pull to refresh layout.
* Vector support.



### PullToRefreshLayout attributes:

| Attr | Note |
| ------ | ---- |
| attr name="pull_resistance" format="float" |  The resistance of the pull distance. default resistance was 1f |
| attr name="pull_maximumScrollOffset" format="dimension"  | How far we would move |
| attr name="pull_minimumRefreshDuration" format="integer" | Sometime we don't want the refresh process to quickly |
| attr name="pull_refreshFreeze" format="boolean"  | when we start to refresh, if you want to freeze the view |
| attr name="pull_headerStrategy" | the header strategy. |
| attr name="pull_headerStyle"  | if you want to have your own header. use vector |
| attr name="pull_refreshMode"  |  Refresh mode. start/end/both/none |

### Usage


```
//in layout.
<com.cz.widget.pulltorefresh.PullToRefreshLayout
        android:id="@+id/pullToRefreshLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:pull_maximumScrollOffset="100dp"
        app:pull_minimumRefreshDuration="2000"
        app:pull_refreshFreeze="true/false"
        app:pull_headerStrategy="follow/front/overlay/scroll"
        app:pull_refreshMode="both/start/end/none"
        app:pull_headerStyle="material/vector"
        app:pull_resistance="1.0">

        <!-- Here we use nested scroll. So make mare you child view implement NestedChild -->
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </com.cz.widget.pulltorefresh.PullToRefreshLayout>
```

* If you triggered refresh event.

```
//Fetch data from somewhere
pullToRefreshLayout.setOnPullToRefreshListener {
    adapter.addItem(0,"NewItem")
    //If content changed for a view like RecyclerView, otherwise it won't move to the first position.
    recyclerView.scrollToPosition(0)
    pullToRefreshLayout.onRefreshComplete()
}
```

* Here are some special functions.

    1. app:pull_minimumRefreshDuration: The minimumRefreshDuration

    One day if you have a fantastic refresh header.You may want the users to watch the animation for a while
    So we got this one. I will postpone the callback until you want to load data.

    2. app:pull_refreshFreeze: If you don't want the user to ruin something while fetching data. enable this function we will freeze the list.

* Different Strategies

    1. FollowStrategy
    2. OverlayStrategy
    3. FrontStrategy
    4. Display


* FollowStrategy
> The most popular strategy that the header will follow the content.

![image1](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/pulltorefresh/image1.gif?raw=true)


* OverlayStrategy
> Only allow content scroll, and stick the header in the back.

![image](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/pulltorefresh/image2.gif?raw=true)

* FrontStrategy
> The official style. Like: SwipeRefreshLayout. The header follows the finger but the content was stilling

![image](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/pulltorefresh/image3.gif?raw=true)


* How does vector work?

When I want to support the vector animation. I actually feel existed.
But there are some problems.

1. The class in the V7 didn't expose the class outside. I have no idea how to make it worked.
So I just take them all out, and change them all.

Take a look at the package:pulltorefresh/src/main/java/com/cz/widget/pulltorefresh/vector

*The first thing I did was:*

```
//1. Traversal all the animator set.
traversalAnimator(animatorList,vectorAnimator);
//2. Here we got the total animation duration.
//We need a total duration to calculate the animation fraction.
totalDuration = getVectorAnimatorTotalDuration(animatorList);
```

So we could manually change animation fraction. Now we could completely control the vector animator.

Also take a look at this program library:[RichPath](https://github.com/tarek360/RichPath)

This is an example for vector header

![image](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/pulltorefresh/image4.gif?raw=true)


### About the style
We have a default style. if you don't have your customise style. We will apply the style automatically.

```
/**
 * Return a context wrapper object.
 */
public static Context getWrapperContext(Context context){
    applyThemeIfNeeded(context);
    TypedArray a = context.obtainStyledAttributes(null, new int[]{R.attr.pullToRefresh});
    int themeResId = a.getResourceId(0, 0);
    Context wrapperContext = context;
    if (themeResId != 0) {
        wrapperContext = new ContextThemeWrapper(context, themeResId);
    }
    return wrapperContext;
}
```


Here are the style. There is one need we have to consider. How to use the vector header.

```
<declare-styleable name="HeaderVectorView">
    <attr name="pull_animator" format="reference"/>
    <attr name="pull_loadAnimator" format="reference"/>
    <attr name="pull_completeAnimator" format="reference"/>
</declare-styleable>
```

And the style is for a view: HeaderVectorView.
But this view is actually in the VectorRefreshHeader.
So we have all those styles. Which allow you configure the vector in style.

```
<style name="PullToRefresh">
    <item name="pullToRefreshLayout">@style/PullToRefreshLayout</item>
    <item name="headerVectorView">@style/HeaderVectorView</item>
    <item name="materialHeaderView">@style/MaterialHeaderView</item>
    <!-- The vector view. -->
    <item name="headerVectorLayout">@layout/xxx</item>
</style>

<style name="PullToRefreshLayout">
    <item name="pull_resistance">1</item>
    <item name="pull_maximumScrollOffset">200dp</item>
    <item name="pull_minimumRefreshDuration">0</item>
    <item name="pull_headerStyle">material</item>
    ...
</style>

<style name="HeaderVectorView">
    ...
</style>

<style name="MaterialHeaderView">
    <item name="materialPaddingTop">24dp</item>
    <item name="materialPaddingBottom">24dp</item>
    ...
</style>

The vector layout looks like this:
<com.cz.widget.pulltorefresh.widget.HeaderVectorView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/headerVectorView"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:layout_gravity="center"
    <!-- you should we three different vector animator -->
    app:pull_animator="@drawable/vector_pull_animator_drawable"
    app:pull_loadAnimator="@drawable/vector_load_animator_drawable"
    app:pull_completeAnimator="@drawable/vector_complete_animator_drawable"/>

```