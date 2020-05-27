package com.cz.widget.recyclerview.sample.adapter

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.db.SampleDatabaseHelper
import com.cz.widget.recyclerview.sample.adapter.db.SimpleCursorLoader
import com.cz.widget.recyclerview.sample.adapter.impl.CursorSampleAdapter
import kotlinx.android.synthetic.main.activity_adapter_cursor_sample.*


/**
 * This activity demonstrates how the CursorAdapter works.
 * Here we use LoaderManager to load data from the Database
 * In order to support LoaderManager we implement a custom loader.
 * @see com.cz.widget.recyclerview.sample.adapter.db.SimpleCursorLoader
 *
 * What's more. We define a URI to notify when the Database changed.
 * @see CursorSampleActivity.SAMPLE_URI
 *
 * When we use {@link LoaderManager} we don't have to close the cursor after the Activity or Fragment was destroyed.
 * The LoaderManager take really good care for us.
 * <pre>
 *     @Override
 *       protected void onReset() {
 *       super.onReset();
 *       // Ensure the loader is stopped
 *       onStopLoading();
 *
 *       if (mCursor != null && !mCursor.isClosed()) {
 *           mCursor.close();
 *       }
 *       mCursor = null;
 *   }
 * </pre>
 */
@SampleSourceCode
@SampleDocument("CursorAdapter.md")
@RefRegister(title=R.string.cursor_adapter,desc = R.string.cursor_adapter_desc,category = R.string.adapter)
class CursorSampleActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    companion object{
        private const val LOADER_ID=100
        /**
         * Be careful.We should have a Authority in this URI. It must be your package name.
         * Or it will cause a problem.
         * Caused by: java.lang.SecurityException: Failed to find provider xxx for user 0; expected to find a valid ContentProvider for this authority
         */
        private val SAMPLE_URI= Uri.parse("content://com.cz.widget.recyclerview.sample/sample_text")
    }
    private var sqLiteDatabase:SQLiteDatabase?=null
    private var loaderManager:LoaderManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adapter_cursor_sample)

        val sampleDatabaseHelper = SampleDatabaseHelper(applicationContext)
        val writableDatabase = sampleDatabaseHelper.getWritableDatabase()
        sqLiteDatabase=writableDatabase
        //We delete all the data in this database table.
        var queryCursor:Cursor?=null
        try {
            queryCursor = writableDatabase.query(SampleDatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
            if(null==queryCursor||!queryCursor.moveToFirst()) {
                //Add a bunch of test data. Never mind if we do all the work in main thread. It's just a test.
                writableDatabase.beginTransaction()
                for(i in 0 until 100){
                    writableDatabase.execSQL("insert into "+SampleDatabaseHelper.TABLE_NAME+"(log) values(?)", arrayOf("Log:$i"))
                }
                writableDatabase.setTransactionSuccessful()
                writableDatabase.endTransaction()
            }
        } finally {
            queryCursor?.close()
        }

        addButton.setOnClickListener {
            val adapter = recyclerView.adapter
            val itemCount = adapter?.itemCount ?: 0
            val contentValues=ContentValues()
            contentValues.put("log","Log:$itemCount")
            writableDatabase.insert(SampleDatabaseHelper.TABLE_NAME,null,contentValues)
            contentResolver.notifyChange(SAMPLE_URI,null)
        }

        removeButton.setOnClickListener {
            var cursor:Cursor?=null
            try {
                cursor = writableDatabase.query(SampleDatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
                if(null!=cursor&&cursor.moveToFirst()) {
                    val log = cursor.getString(cursor.getColumnIndex("log"))
                    writableDatabase.delete(SampleDatabaseHelper.TABLE_NAME, "log=?",  arrayOf(log))
                    contentResolver.notifyChange(SAMPLE_URI,null)
                }
            } finally {
                cursor?.close()
            }
        }
        loaderManager= LoaderManager.getInstance(this)
        loaderManager?.initLoader(LOADER_ID,null,this)
    }

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

}
