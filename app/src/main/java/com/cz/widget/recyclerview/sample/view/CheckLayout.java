package com.cz.widget.recyclerview.sample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.cz.widget.recyclerview.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020-03-22 12:10
 * @email bingo110@126.com
 */
public class CheckLayout extends LinearLayout {
    private List<Integer> checkItems=new ArrayList<>();
    private OnCheckedListener listener = null;

    public CheckLayout(Context context) {
        this(context,null,0);
    }

    public CheckLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CheckLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckLayout);
        setCheckArray(context,a.getTextArray(R.styleable.CheckLayout_check_array));
        a.recycle();
    }

    private void setCheckArray(Context context,CharSequence[] textArray) {
        removeAllViews();
        if (null != textArray) {
            checkItems.clear();
            for(int i=0;i<textArray.length;i++){
                CharSequence item = textArray[i];
                AppCompatCheckBox appCompatCheckBox = new AppCompatCheckBox(context);
                appCompatCheckBox.setText(item);
                final int finalIndex = i;
                appCompatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            checkItems.add(finalIndex);
                        } else {
                            checkItems.remove(Integer.valueOf(finalIndex));
                        }
                        if (null != listener) {
                            listener.onChecked(buttonView, checkItems, finalIndex);
                        }
                    }
                });
                addView(appCompatCheckBox);
            }
        }
    }

    List<Integer> getCheckedPosotions(){
        return checkItems;
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedListener {
        void onChecked(View v,List<Integer> items,int position);
    }

}
