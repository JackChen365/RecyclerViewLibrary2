## CursorAdapter

> This is actually from the ListView's cursor adapter. Because of the RecyclerView we don't have one.
  So here I changed a little in CursorAdapter to have an adapter that we could be able to use for RecyclerView.

#### Example classes:

* [CursorSampleAdapter](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/impl/CursorSampleAdapter.java)
* [Sample](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/CursorSampleActivity.java)

#### Image
![image](../image/cursor_adapter.gif)

### Usage

This adapter usually associate with the LoaderManager. In that case You don't have to take care of the lifecycle of the Cursor.
For example:

```
private var loaderManager:LoaderManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderManager= LoaderManager.getInstance(this)
        loaderManager?.initLoader(LOADER_ID,null,this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(...)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        val adapter = recyclerView.adapter
        if(null==adapter){
            recyclerView.layoutManager = LinearLayoutManager(this)
            val cursorAdapter= CursorSampleAdapter(this, cursor)
            recyclerView.adapter = cursorAdapter
        } else if(adapter is CursorSampleAdapter){
            adapter.changeCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        val adapter = recyclerView.adapter
        if(null!=adapter&&adapter is CursorSampleAdapter){
            adapter.changeCursor(null)
        }
    }
```

That's it. It takes really good care for CursorAdapter on how to load the cursor and when to release the Cursor.

When the LifeCycleOwner destroyed. It release the cursor in this function.

```
// androidx.loader.content.CursorLoader.onReset
@Override
protected void onReset() {
    super.onReset();

    // Ensure the loader is stopped
    onStopLoading();
    //Here it releases the cursor automatically
    if (mCursor != null && !mCursor.isClosed()) {
        mCursor.close();
    }
    mCursor = null;
}
```

So We don't recommend you to use CursorAdapter alone. If you forgot to release the Cursor after the Activity is finished.
It may cause some problems.

### How to use LoaderManager without ContentProvider

Here is an solution for LoaderManager without ContentProvider.

We user this loader:[SimpleCursorLoader](app/src/main/java/com/cz/widget/recyclerview/sample/adapter/db/SimpleCursorLoader.java) instead of CursorLoader.

The function create loader it's more like:

```
override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
    return object :SimpleCursorLoader(applicationContext){
        private val mObserver = ForceLoadContentObserver()

        override fun loadInBackground(): Cursor? {
            val cursor = sqLiteDatabase?.query(SampleDatabaseHelper.TABLE_NAME, arrayOf("_id,log"), null,null, null, null, null);
            if(null!=cursor){
                cursor.registerContentObserver(mObserver)
                cursor.setNotificationUri(contentResolver, SAMPLE_URI)
            }
            return cursor
        }
    }
}
```

But there are few problems:
* The NotificationUri
* When we should notify the content has changed.

First of all. We could make an fake uri for the cursor. Here is mine.

```
/**
 * Be careful.We should have a Authority in this URI. no matter what it is.
 * Or it will cause a problem.
 * Caused by: java.lang.SecurityException: Failed to find provider xxx for user 0; expected to find a valid ContentProvider for this authority
 */
private val SAMPLE_URI= Uri.parse("content://com.cz.widget.recyclerview.sample/sample_text")
```

Secondly. I notify the change manually.

```
addButton.setOnClickListener {
    val adapter = recyclerView.adapter
    val itemCount = adapter?.itemCount ?: 0
    val contentValues=ContentValues()
    contentValues.put("log","Log:$itemCount")
    writableDatabase.insert(SampleDatabaseHelper.TABLE_NAME,null,contentValues)
    contentResolver.notifyChange(SAMPLE_URI,null)
}
```

Just an example for the loader. I highly recommend you to use the CursorLoader.