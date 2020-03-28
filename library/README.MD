## PullToRefresh

> This is a pull to refresh for recycler view to support the load data from footer.

Here are the typical features we supports:
* Use a footer frame layout to change different load status.
* Vector support.


<attr name="pull_listDivider" format="reference" />
        <attr name="pull_alwaysLoad" format="boolean"/>
        <attr name="layout_adapterView" format="enum">
            <enum name="header" value="0x00"/>
            <enum name="footer" value="0x01"/>
        </attr>
### PullToRefreshRecyclerView attributes:

| Attr | Note |
| ------ | ---- |
| attr name="pull_listDivider" format="resources" |  The divider drawable |
| attr name="pull_alwaysLoad" format="boolean"  | If the content not fill screen do we did to trigger the load event |
| attr name="layout_adapterView" format="resources" | Add footer or header view in XML |


![image1](image/image1.gif)

![image2](image/image2.gif)

### Usage


```
//in layout.
<com.cz.widget.recyclerview.library.PullToRefreshRecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/md_grey_200"
        app:pull_listDivider="@drawable/simple_list_divider"
        app:pull_alwaysLoad="true"
        app:pull_resistance="2"/>
```


#### Add header or footer view from XML

```
<com.cz.widget.recyclerview.library.PullToRefreshRecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/md_grey_200"
        app:pull_listDivider="@drawable/simple_list_divider"
        app:pull_alwaysLoad="true"
        app:pull_resistance="2">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="60dp"
            android:gravity="center"
            //Here this attribute let the container know if you are a header or footer.
            app:layout_adapterView="header/footer"
            android:text="@string/header_text"
            android:textSize="24sp" />

    </com.cz.widget.recyclerview.library.PullToRefreshRecyclerView>
```

#### Do I need to trigger the footer load when the content not fill the screen.

Sometimes for the not professional backend. They may return the list data unexpectedly.
For that reason, you should always check if there are more data after your list.

So if want to trigger the footer load event. I add an additional attribute:AlwaysLoad.

If you turn it on. We will trigger the footer event even if the content not fill the screen.
Otherwise we think that's all in the list.

```
<com.cz.widget.recyclerview.library.PullToRefreshRecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/md_grey_100"
    //This one.
    app:pull_alwaysLoad="true"/>
```


#### The footer.

We support the footer load by a special wrapper adapter.

Always keep our footer load view in the end of the adapter.

For user you don't operate this footer directly. That's it. If you want to load we show it or hide it.

```
public class RefreshWrapperAdapter extends DynamicWrapperAdapter {
    private View refreshFooterView = null;

    public RefreshWrapperAdapter(@Nullable RecyclerView.Adapter adapter) {
        super(adapter);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
//        if(viewHolder.itemView==refreshFooterView){
//            viewHolder.setIsRecyclable(false);
//        }
        return viewHolder;
    }

    public void addRefreshFooterView(View view){
        if (null == refreshFooterView) {
            refreshFooterView=view;
            int footerViewCount = getFooterViewCount();
            if(0==footerViewCount){
                super.addFooterView(view,0);
            } else {
                super.addFooterView(view,footerViewCount);
            }
        }
    }

    public void removeRefreshFooterView(View view){
        if(refreshFooterView==view){
            refreshFooterView=null;
        }
        super.removeFooterView(view);
    }

    @Override
    protected void addFooterView(@NonNull View view, int index) {
        if(null==refreshFooterView){
            super.addFooterView(view, index);
        } else {
            int footerViewCount = getFooterViewCount();
            if(0==footerViewCount){
                super.addFooterView(view,0);
            } else {
                super.addFooterView(view,footerViewCount-1);
            }
        }
    }

    @Override
    public void removeFooterView(int position) {
        View footerView = getFooterView(position);
        if(footerView!=refreshFooterView){
            super.removeFooterView(position);
        }
    }

}
```


### The refresh footer

```
recyclerView.setOnPullFooterToRefreshListener {
    //Fetch list data from some where.
    ...
    //After we've done the loading. We call this function to update the view.
    recyclerView.onRefreshFooterComplete()
}

//The different footer status.

//Loading
recyclerView.setRefreshFooterFrame(PullToRefreshRecyclerView.FOOTER_LOAD)
//load complete
recyclerView.setRefreshFooterFrame(PullToRefreshRecyclerView.FOOTER_COMPLETE)
//load error
recyclerView.setRefreshFooterFrame(PullToRefreshRecyclerView.FOOTER_ERROR)
```

### How to change the footer style

The basic style.

```
<style name="PullToRefresh">
    //For the pull to refresh recycler view.
    <item name="pullToRefreshRecyclerView">@style/PullToRefreshRecyclerView</item>
    //For the footer frame layout.
    <item name="refreshFooterFrameLayout">@style/RefreshFooterFrameLayout</item>

    //For three different fotter layout style.

    <!-- the load container style -->
    <item name="footerLoadLayout">@style/FooterLoadLayout</item>
    <!-- the load hint text style -->
    <item name="footerLoadProgress">@style/FooterLoadProgress</item>
    <!-- the load text style -->
    <item name="footerLoadText">@style/FooterLoadText</item>

    <!-- the error container style -->
    <item name="footerErrorLayout">@style/FooterErrorLayout</item>
    <!-- the error hint text style -->
    <item name="footerErrorText">@style/FooterErrorText</item>
    <!-- the error button style -->
    <item name="footerErrorButton">@style/FooterErrorButton</item>

    <!-- the complete container style -->
    <item name="footerCompleteLayout">@style/FooterCompleteLayout</item>
    <!-- the complete text style -->
    <item name="footerCompleteText">@style/FooterCompleteText</item>
</style>
```