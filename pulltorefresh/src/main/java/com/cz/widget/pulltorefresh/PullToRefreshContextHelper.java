package com.cz.widget.pulltorefresh;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import androidx.appcompat.view.ContextThemeWrapper;

/**
 * @author Created by cz
 * @date 2020-03-06 23:20
 * @email bingo110@126.com
 */
public class PullToRefreshContextHelper {

    /**
     * If we don't have a custom style. Here we apply the default style.
     */
    private static void applyThemeIfNeeded(Context context){
        TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.pullToRefresh});
        if (!a.hasValue(0)) {
            Log.w("PullToRefresh","You have no configuration prompt style with this activity!");
            Resources.Theme theme = context.getTheme();
            theme.applyStyle(R.style.PullToRefresh,true);
        }
        a.recycle();
    }


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
}
