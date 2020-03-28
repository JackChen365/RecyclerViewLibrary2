package com.cz.widget.recyclerview.sample.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.cz.widget.recyclerview.sample.R;

/**
 * @author Created by cz
 * @date 2020-02-02 09:26
 * @email bingo110@126.com
 */
public class RadioButtonLayout extends RadioGroup {
    // holds the checked id; the selection is empty by default
    private int checkedId = View.NO_ID;

    public RadioButtonLayout(Context context) {
        this(context,null);
    }

    public RadioButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonLayout);
        setRadioArray(a.getTextArray(R.styleable.RadioButtonLayout_radio_array));
        a.recycle();
    }

    /**
     * Initialize button setCompareCondition by text array resources
     * @param arrayRes
     */
    public void setRadioArray(int arrayRes) {
        Resources resources = getResources();
        setRadioArray(resources.getStringArray(arrayRes));
    }

    /**
     * set radio setCompareCondition array.
     * @param textArray
     */
    public void setRadioArray(CharSequence[] textArray) {
        removeAllViews();
        Context context = getContext();
        if(null!=textArray){
            for(int i=0;i<textArray.length;i++){
                CharSequence text=textArray[i];
                Button button = new Button(context);
                button.setId(i);
                button.setAllCaps(false);
                button.setText(text);
                addView(button);
            }
        }
    }

    @Override
    public void onViewAdded(View child) {
//        super.onViewAdded(child);
        int id = child.getId();
        // generates an id if it's missing
        if (id == View.NO_ID) {
            id = View.generateViewId();
            child.setId(id);
        }
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                check(view.getId());
            }
        });
    }

    @Override
    public void setOnCheckedChangeListener(final OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(null!=listener){
                    listener.onCheckedChanged(radioGroup,i);
                    View lastCheckView = findViewById(checkedId);
                    if(null!=lastCheckView){
                        lastCheckView.setEnabled(true);
                    }
                    View checkedView = findViewById(i);
                    if(null!=checkedView){
                       checkedView.setEnabled(false);
                    }
                    checkedId=i;
                }
            }
        });
    }
}
