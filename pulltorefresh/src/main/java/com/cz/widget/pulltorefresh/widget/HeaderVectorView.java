package com.cz.widget.pulltorefresh.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.cz.widget.pulltorefresh.PullToRefreshContextHelper;
import com.cz.widget.pulltorefresh.PullToRefreshState;
import com.cz.widget.pulltorefresh.R;
import com.cz.widget.pulltorefresh.vector.VectorAnimatorCompat;

/**
 * @author Created by cz
 * @date 2020-03-05 17:07
 * @email bingo110@126.com
 */
public class HeaderVectorView extends AppCompatImageView {
    private VectorAnimatorCompat vectorAnimatorCompat;
    private VectorAnimatorCompat loadAnimator;
    private VectorAnimatorCompat completeAnimatorCompat;

    public HeaderVectorView(Context context) {
        this(context,null,R.style.HeaderVectorView);
    }

    public HeaderVectorView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,R.style.HeaderVectorView);
    }

    public HeaderVectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Context wrapperContext = PullToRefreshContextHelper.getWrapperContext(context);
        TypedArray a = wrapperContext.obtainStyledAttributes(attrs, R.styleable.HeaderVectorView, defStyleAttr, R.style.HeaderVectorView);
        setPullAnimator(context,a.getResourceId(R.styleable.HeaderVectorView_pull_animator,0));
        setLoadAnimator(context,a.getResourceId(R.styleable.HeaderVectorView_pull_loadAnimator,0));
        setCompleteAnimator(context,a.getResourceId(R.styleable.HeaderVectorView_pull_completeAnimator,0));
        a.recycle();
    }

    private void setPullAnimator(Context context,int resourceId) {
        vectorAnimatorCompat = VectorAnimatorCompat.create(context, resourceId);
        setImageDrawable(vectorAnimatorCompat.getDrawable());

    }

    private void setLoadAnimator(Context context,int resourceId) {
        loadAnimator  = VectorAnimatorCompat.create(context, resourceId);
    }

    private void setCompleteAnimator(Context context,int resourceId) {
        completeAnimatorCompat = VectorAnimatorCompat.create(context, resourceId);
    }

    public void onScrollOffset(float fraction) {
        vectorAnimatorCompat.setCurrentFraction(fraction);
    }

    public void onRefreshStateChange(PullToRefreshState refreshState) {
        switch (refreshState){
            case START_PULL:{
                setImageDrawable(vectorAnimatorCompat.getDrawable());
                vectorAnimatorCompat.setCurrentFraction(0f);
                break;
            }
            case REFRESHING:{
                setImageDrawable(loadAnimator.getDrawable());
                AnimatorSet vectorAnimator = loadAnimator.getVectorAnimator();
                vectorAnimator.addListener(new AnimatorListenerAdapter() {
                    private boolean cancelAnimator =false;
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        cancelAnimator =false;
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        cancelAnimator = true;
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if(!cancelAnimator){
                            animation.start();
                        }
                    }
                });
                loadAnimator.start();
                break;
            }
            case REFRESHING_COMPLETE:{
                AnimatorSet vectorAnimator = loadAnimator.getVectorAnimator();
                vectorAnimator.cancel();
                setImageDrawable(completeAnimatorCompat.getDrawable());
                completeAnimatorCompat.start();
                break;
            }
        }
    }

    public long completeAnimationDuration(){
        return completeAnimatorCompat.getTotalDuration();
    }

    public void onRefreshComplete() {

    }
}
