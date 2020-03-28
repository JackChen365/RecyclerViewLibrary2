package com.cz.widget.pulltorefresh.vector;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cz
 * @date 2020-03-04 20:34
 * @email bingo110@126.com
 * This class is for Vector who want to manually change the fraction of AnimatedVectorDrawableCompat
 * It took us three steps to approach this function.
 * First of all. We totally change the VectorDrawable from v7. Almost copy all the related classes from v7
 * Secondly. We changed a little function that support this class. Like return the animator set.
 *  Actually here we could support more functions. There is one project that impressed me:https://github.com/tarek360/RichPath
 *   This guy just does the same thing. But exposing all the path object instead
 *   So you could be able to get all these complicated paths. and do some things
 *
 * Finally. We come here. Remember that we just want to change the fraction of animation.
 *  So We traversal all the animator. Make sure we don't have to deal with some animators.
 *  We got the total duration of the animation. And re-calculate the animation fraction for each animator.
 *
 * @see VectorAnimatorCompat#setCurrentFraction(float) We could call this function to change the fraction for the whole animation
 *
 * Task a look at {@link AnimatedVectorDrawableCompat}
 *
 */
public class VectorAnimatorCompat implements Animatable2Compat {
    private final List<ObjectAnimator> animatorList;
    private final AnimatedVectorDrawableCompat animatedVectorDrawable;
    private final long totalDuration;

    public static VectorAnimatorCompat create(@NonNull Context context,
                                                      @DrawableRes int resId) {
        AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(context, resId);
        return new VectorAnimatorCompat(animatedVectorDrawableCompat);
    }

    private VectorAnimatorCompat(AnimatedVectorDrawableCompat animatedVectorDrawable){
        this.animatorList=new ArrayList<>();
        this.animatedVectorDrawable=animatedVectorDrawable;
        AnimatorSet vectorAnimator = animatedVectorDrawable.getVectorAnimator();
        //1. Traversal all the animator tree.
        traversalAnimator(animatorList,vectorAnimator);
        //2. Here we got the total animation duration.
        //We need a total duration to calculate the animation fraction.
        totalDuration = getVectorAnimatorTotalDuration(animatorList);
    }

    public AnimatedVectorDrawableCompat getAnimatedVectorDrawable() {
        return animatedVectorDrawable;
    }

    public AnimatorSet getVectorAnimator(){
        return animatedVectorDrawable.getVectorAnimator();
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    /**
     * Traversal all the animator and put them into a list.
     * Cause we don't need animator set.
     * @param animatorList
     * @param animator
     */
    private void traversalAnimator(List<ObjectAnimator> animatorList, Animator animator){
        if(!(animator instanceof AnimatorSet)){
            ObjectAnimator objectAnimator= (ObjectAnimator) animator;
            animatorList.add(objectAnimator);
        } else {
            AnimatorSet animatorSet = (AnimatorSet) animator;
            for(Animator childAnimator : animatorSet.getChildAnimations()){
                traversalAnimator(animatorList,childAnimator);
            }
        }
    }

    /**
     * Here we return the total animation duration.
     * We need a total duration to calculate the animation fraction.
     * It's usually like this:
     * Animator:A startOffset=100 duration=300
     * So we start to calculate this animator's fraction from 100 to 400.
     * @param animatorList
     * @return
     */
    private long getVectorAnimatorTotalDuration(List<ObjectAnimator> animatorList){
        long totalDuration=0L;
        for(ObjectAnimator animator:animatorList){
            long startDelay = animator.getStartDelay();
            long duration = animator.getDuration();
            long animatorDuration = startDelay + duration;
            if(totalDuration<animatorDuration){
                totalDuration=animatorDuration;
            }
        }
        return totalDuration;
    }


    /**
     * Change the fraction of animation manually.
     * @param fraction
     */
    public void setCurrentFraction(float fraction){
        for(ObjectAnimator animator:animatorList){
            long currentTime = (long) (totalDuration * fraction);
            long startDelay = animator.getStartDelay();
            long duration = animator.getDuration();
            if(startDelay<=currentTime&& currentTime<(startDelay+duration)){
                //This animator will start to run, and this is each animator's fraction.
                TimeInterpolator interpolator = animator.getInterpolator();
                float animationFraction = interpolator.getInterpolation(fraction);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    animator.setCurrentFraction(animationFraction);
                } else {
                    animator.setCurrentPlayTime(currentTime-startDelay);
                }
                animatedVectorDrawable.invalidateSelf();
            }
        }
    }

    public Drawable getDrawable(){
        return animatedVectorDrawable;
    }

    @Override
    public void registerAnimationCallback(@NonNull AnimationCallback callback) {
        animatedVectorDrawable.registerAnimationCallback(callback);
    }

    @Override
    public boolean unregisterAnimationCallback(@NonNull AnimationCallback callback) {
        return animatedVectorDrawable.unregisterAnimationCallback(callback);
    }

    @Override
    public void clearAnimationCallbacks() {
        animatedVectorDrawable.clearAnimationCallbacks();
    }

    @Override
    public void start() {
        animatedVectorDrawable.start();
    }

    @Override
    public void stop() {
        animatedVectorDrawable.stop();
    }

    @Override
    public boolean isRunning() {
        return animatedVectorDrawable.isRunning();
    }
}
