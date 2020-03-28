package com.cz.widget.recyclerview.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;

import androidx.annotation.AttrRes;
import androidx.appcompat.view.ContextThemeWrapper;

/**
 * @author Created by cz
 * @date 2020-03-24 10:12
 * @email bingo110@126.com
 */
public class ContextHelper {
    /**
     * Check our style if it doesn't exist. We group for a default style.
     * @param context
     */
    private static void applyThemeIfNeeded(Context context){
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.pullToRefresh});
        if (!typedArray.hasValue(0)) {
            Log.w("PullToRefresh","You have no configuration pull to refresh style with this activity!");
            Resources.Theme theme = context.getTheme();
            theme.applyStyle(R.style.PullToRefresh,true);
        }
        typedArray.recycle();
    }


    /**
     * Wrap the context of our theme
     * @return
     */
    public static Context getWrapperContext(Context context){
        Context wrapperContext;
        TypedArray typedArray = context.obtainStyledAttributes(null, new int[]{R.attr.pullToRefresh});
        int themeResId = typedArray.getResourceId(0, 0);
        if (themeResId != 0) {
            //wrap the context
            wrapperContext = new ContextThemeWrapper(context, themeResId);
        } else {
            wrapperContext = new ContextThemeWrapper(context, R.style.PullToRefresh);
        }
        return wrapperContext;
    }

    /**
     * Wrap the context of a specific theme
     * @return
     */
    public static Context getWrapperContext(Context context,@AttrRes int attr){
        Context wrapperContext=context;
        TypedArray typedArray = context.obtainStyledAttributes(null, new int[]{attr});
        int themeResId = typedArray.getResourceId(0, 0);
        if (themeResId != 0) {
            //wrap the context
            wrapperContext = new ContextThemeWrapper(context, themeResId);
        }
        return wrapperContext;
    }
}
