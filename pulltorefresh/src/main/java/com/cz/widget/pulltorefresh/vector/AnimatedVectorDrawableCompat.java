package com.cz.widget.pulltorefresh.vector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Here are all the animatable attributes in {@link VectorDrawableCompat}:
 * <table border="2" align="center" cellpadding="5">
 *     <thead>
 *         <tr>
 *             <th>Element Name</th>
 *             <th>Animatable attribute name</th>
 *         </tr>
 *     </thead>
 *     <tr>
 *         <td>&lt;vector&gt;</td>
 *         <td>alpha</td>
 *     </tr>
 *     <tr>
 *         <td rowspan="7">&lt;group&gt;</td>
 *         <td>rotation</td>
 *     </tr>
 *     <tr>
 *         <td>pivotX</td>
 *     </tr>
 *     <tr>
 *         <td>pivotY</td>
 *     </tr>
 *     <tr>
 *         <td>scaleX</td>
 *     </tr>
 *     <tr>
 *         <td>scaleY</td>
 *     </tr>
 *     <tr>
 *         <td>translateX</td>
 *     </tr>
 *     <tr>
 *         <td>translateY</td>
 *     </tr>
 *     <tr>
 *         <td rowspan="8">&lt;path&gt;</td>
 *         <td>fillColor</td>
 *     </tr>
 *     <tr>
 *         <td>pathData</td>
 *     </tr>
 *     <tr>
 *         <td>strokeColor</td>
 *     </tr>
 *     <tr>
 *         <td>strokeWidth</td>
 *     </tr>
 *     <tr>
 *         <td>strokeAlpha</td>
 *     </tr>
 *     <tr>
 *         <td>fillAlpha</td>
 *     </tr>
 *     <tr>
 *         <td>trimPathStart</td>
 *     </tr>
 *     <tr>
 *         <td>trimPathOffset</td>
 *     </tr>
 * </table>
 * <p/>
 * You can always create a AnimatedVectorDrawableCompat object and use it as a Drawable by the Java
 * API. In order to refer to AnimatedVectorDrawableCompat inside a XML file, you can use
 * app:srcCompat attribute in AppCompat library's ImageButton or ImageView.
 * <p/>
 * Note that the animation in AnimatedVectorDrawableCompat now can support the following features:
 * <ul>
 * <li>Path Morphing (PathType evaluator). This is used for morphing one path into another.</li>
 * <li>Path Interpolation. This is used to defined a flexible interpolator (represented as a path)
 * instead of the system defined ones like LinearInterpolator.</li>
 * <li>Animating 2 values in one ObjectAnimator according to one path's X value and Y value. One
 * usage is moving one object in both X and Y dimensions along an path.</li>
 * </ul>
 */
class AnimatedVectorDrawableCompat extends Drawable implements Animatable2Compat {
    private static final String LOGTAG = "AnimatedVDCompat";

    private static final String ANIMATED_VECTOR = "animated-vector";
    private static final String TARGET = "target";

    private static final boolean DBG_ANIMATION_VECTOR_DRAWABLE = false;

    private AnimatedVectorDrawableCompatState mAnimatedVectorState;

    private Context mContext;

    private ArgbEvaluator mArgbEvaluator = null;
    // Use internal listener to support AVDC's callback.
    private Animator.AnimatorListener mAnimatorListener = null;

    // Use an array to keep track of multiple call back associated with one drawable.
    private ArrayList<Animatable2Compat.AnimationCallback> mAnimationCallbacks = null;

    private AnimatedVectorDrawableCompat(@Nullable Context context) {
        mContext = context;
        mAnimatedVectorState = new AnimatedVectorDrawableCompatState();
    }

    /**
     * mutate() will be effective only if the getConstantState() is returning non-null.
     * Otherwise, it just return the current object without modification.
     */
    @Override
    public Drawable mutate() {
        // For older platforms that there is no delegated drawable, we just return this without
        // any modification here, and the getConstantState() will return null in this case.
        return this;
    }


    /**
     * Create a AnimatedVectorDrawableCompat object.
     *
     * @param context the context for creating the animators.
     * @param resId   the resource ID for AnimatedVectorDrawableCompat object.
     * @return a new AnimatedVectorDrawableCompat or null if parsing error is found.
     */
    @Nullable
    public static AnimatedVectorDrawableCompat create(@NonNull Context context,
            @DrawableRes int resId) {
        Resources resources = context.getResources();
        try {
            //noinspection AndroidLintResourceType - Parse drawable as XML.
            final XmlPullParser parser = resources.getXml(resId);
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
                // Empty loop
            }
            if (type != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("No start tag found");
            }
            return createFromXmlInner(context, context.getResources(), parser, attrs,
                    context.getTheme());
        } catch (XmlPullParserException e) {
            Log.e(LOGTAG, "parser error", e);
        } catch (IOException e) {
            Log.e(LOGTAG, "parser error", e);
        }
        return null;
    }

    /**
     * Create a AnimatedVectorDrawableCompat from inside an XML document using an optional
     * {@link Theme}. Called on a parser positioned at a tag in an XML
     * document, tries to create a Drawable from that tag. Returns {@code null}
     * if the tag is not a valid drawable.
     */
    public static AnimatedVectorDrawableCompat createFromXmlInner(Context context, Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme)
            throws XmlPullParserException, IOException {
        final AnimatedVectorDrawableCompat drawable = new AnimatedVectorDrawableCompat(context);
        drawable.inflate(r, parser, attrs, theme);
        return drawable;
    }

    /**
     * {@inheritDoc}
     * <strong>Note</strong> that we don't support constant state when SDK < 24.
     * Make sure you check the return value before using it.
     */
    @Override
    public ConstantState getConstantState() {
        // We can't support constant state in older platform.
        // We need Context to create the animator, and we can't save the context in the constant
        // state.
        return null;
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mAnimatedVectorState.mChangingConfigurations;
    }

    @Override
    public void draw(Canvas canvas) {
        mAnimatedVectorState.mVectorDrawable.draw(canvas);
        if (mAnimatedVectorState.mAnimatorSet.isStarted()) {
            invalidateSelf();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mAnimatedVectorState.mVectorDrawable.setBounds(bounds);
    }

    @Override
    protected boolean onStateChange(int[] state) {
        return mAnimatedVectorState.mVectorDrawable.setState(state);
    }

    @Override
    protected boolean onLevelChange(int level) {
        return mAnimatedVectorState.mVectorDrawable.setLevel(level);
    }

    @Override
    public int getAlpha() {
        return mAnimatedVectorState.mVectorDrawable.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        mAnimatedVectorState.mVectorDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mAnimatedVectorState.mVectorDrawable.setColorFilter(colorFilter);
    }

    @Override
    public void setTint(int tint) {
        mAnimatedVectorState.mVectorDrawable.setTint(tint);
    }

    @Override
    public void setTintList(ColorStateList tint) {
        mAnimatedVectorState.mVectorDrawable.setTintList(tint);
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        mAnimatedVectorState.mVectorDrawable.setTintMode(tintMode);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        mAnimatedVectorState.mVectorDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override
    public boolean isStateful() {
        return mAnimatedVectorState.mVectorDrawable.isStateful();
    }

    @Override
    public int getOpacity() {
        return mAnimatedVectorState.mVectorDrawable.getOpacity();
    }

    @Override
    public int getIntrinsicWidth() {
        return mAnimatedVectorState.mVectorDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mAnimatedVectorState.mVectorDrawable.getIntrinsicHeight();
    }

    @Override
    public boolean isAutoMirrored() {
        return mAnimatedVectorState.mVectorDrawable.isAutoMirrored();
    }

    @Override
    public void setAutoMirrored(boolean mirrored) {
        mAnimatedVectorState.mVectorDrawable.setAutoMirrored(mirrored);
    }

    @Override
    public void inflate(Resources res, XmlPullParser parser, AttributeSet attrs, Theme theme)
            throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        final int innerDepth = parser.getDepth() + 1;

        // Parse everything until the end of the animated-vector element.
        while (eventType != XmlPullParser.END_DOCUMENT
                && (parser.getDepth() >= innerDepth || eventType != XmlPullParser.END_TAG)) {
            if (eventType == XmlPullParser.START_TAG) {
                final String tagName = parser.getName();
                if (DBG_ANIMATION_VECTOR_DRAWABLE) {
                    Log.v(LOGTAG, "tagName is " + tagName);
                }
                if (ANIMATED_VECTOR.equals(tagName)) {
                    final TypedArray a = TypedArrayUtils.obtainAttributes(res, theme, attrs, AndroidVectorResources.STYLEABLE_ANIMATED_VECTOR_DRAWABLE);

                    int drawableRes = a.getResourceId(
                            AndroidVectorResources.STYLEABLE_ANIMATED_VECTOR_DRAWABLE_DRAWABLE, 0);
                    if (DBG_ANIMATION_VECTOR_DRAWABLE) {
                        Log.v(LOGTAG, "drawableRes is " + drawableRes);
                    }
                    if (drawableRes != 0) {
                        VectorDrawableCompat vectorDrawable = VectorDrawableCompat.create(res, drawableRes, theme);
                        vectorDrawable.setCallback(mCallback);
                        if (mAnimatedVectorState.mVectorDrawable != null) {
                            mAnimatedVectorState.mVectorDrawable.setCallback(null);
                        }
                        mAnimatedVectorState.mVectorDrawable = vectorDrawable;
                    }
                    a.recycle();
                } else if (TARGET.equals(tagName)) {
                    final TypedArray a =
                            res.obtainAttributes(attrs,
                                    AndroidVectorResources.STYLEABLE_ANIMATED_VECTOR_DRAWABLE_TARGET);
                    final String target = a.getString(
                            AndroidVectorResources.STYLEABLE_ANIMATED_VECTOR_DRAWABLE_TARGET_NAME);

                    int id = a.getResourceId(
                            AndroidVectorResources.STYLEABLE_ANIMATED_VECTOR_DRAWABLE_TARGET_ANIMATION,
                            0);
                    if (id != 0) {
                        if (mContext != null) {
                            // There are some important features (like path morphing), added into
                            // Animator code to support AVD at API 21.
                            @SuppressLint("RestrictedApi") Animator objectAnimator = AnimatorInflaterCompat.loadAnimator(mContext,mContext.getResources(),mContext.getTheme(), id);
                            setupAnimatorsForTarget(target, objectAnimator);
                        } else {
                            a.recycle();
                            throw new IllegalStateException("Context can't be null when inflating" +
                                    " animators");
                        }
                    }
                    a.recycle();
                }
            }
            eventType = parser.next();
        }

        mAnimatedVectorState.setupAnimatorSet();
    }

    @Override
    public void inflate(Resources res, XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        inflate(res, parser, attrs, null);
    }

    @Override
    public void applyTheme(Theme t) {
        // TODO: support theming in older platform.
        return;
    }

    @Override
    public boolean canApplyTheme() {
        // TODO: support theming in older platform.
        return false;
    }

    private static class AnimatedVectorDrawableCompatState extends ConstantState {
        int mChangingConfigurations;
        VectorDrawableCompat mVectorDrawable;
        // Combining the array of Animators into a single AnimatorSet to hook up listener easier.
        AnimatorSet mAnimatorSet;
        private ArrayList<Animator> mAnimators;
        ArrayMap<Animator, String> mTargetNameMap;

        public AnimatedVectorDrawableCompatState() {
        }

        @Override
        public Drawable newDrawable() {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }

        @Override
        public Drawable newDrawable(Resources res) {
            throw new IllegalStateException("No constant state support for SDK < 24.");
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }

        public void setupAnimatorSet() {
            if (mAnimatorSet == null) {
                mAnimatorSet = new AnimatorSet();
            }
            mAnimatorSet.playTogether(mAnimators);
        }
    }

    /**
     * Utility function to fix color interpolation prior to Lollipop. Without this fix, colors
     * are evaluated as raw integers instead of as colors, which leads to artifacts during
     * fillColor animations.
     */
    private void setupColorAnimator(Animator animator) {
        if (animator instanceof AnimatorSet) {
            List<Animator> childAnimators = ((AnimatorSet) animator).getChildAnimations();
            if (childAnimators != null) {
                for (int i = 0; i < childAnimators.size(); ++i) {
                    setupColorAnimator(childAnimators.get(i));
                }
            }
        }
        if (animator instanceof ObjectAnimator) {
            ObjectAnimator objectAnim = (ObjectAnimator) animator;
            final String propertyName = objectAnim.getPropertyName();
            if ("fillColor".equals(propertyName) || "strokeColor".equals(propertyName)) {
                if (mArgbEvaluator == null) {
                    mArgbEvaluator = new ArgbEvaluator();
                }
                objectAnim.setEvaluator(mArgbEvaluator);
            }
        }
    }

    private void setupAnimatorsForTarget(String name, Animator animator) {
        Object target = mAnimatedVectorState.mVectorDrawable.getTargetByName(name);
        animator.setTarget(target);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setupColorAnimator(animator);
        }
        if (mAnimatedVectorState.mAnimators == null) {
            mAnimatedVectorState.mAnimators = new ArrayList<>();
            mAnimatedVectorState.mTargetNameMap = new ArrayMap<>();
        }
        mAnimatedVectorState.mAnimators.add(animator);
        mAnimatedVectorState.mTargetNameMap.put(animator, name);
        if (DBG_ANIMATION_VECTOR_DRAWABLE) {
            Log.v(LOGTAG, "add animator  for target " + name + " " + animator);
        }
    }

    @Override
    public boolean isRunning() {
        return mAnimatedVectorState.mAnimatorSet.isRunning();
    }

    @Override
    public void start() {
        // If any one of the animator has not ended, do nothing.
        if (mAnimatedVectorState.mAnimatorSet.isStarted()) {
            return;
        }
        // Otherwise, kick off animatorSet.
        mAnimatedVectorState.mAnimatorSet.start();
        invalidateSelf();
    }

    public AnimatorSet getVectorAnimator(){
        return mAnimatedVectorState.mAnimatorSet;
    }

    @Override
    public void stop() {
        mAnimatedVectorState.mAnimatorSet.end();
    }

    final Callback mCallback = new Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            invalidateSelf();
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            scheduleSelf(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            unscheduleSelf(what);
        }
    };

    @Override
    public void registerAnimationCallback(@NonNull Animatable2Compat.AnimationCallback
            callback) {
        if (callback == null) {
            return;
        }

        // Add listener accordingly.
        if (mAnimationCallbacks == null) {
            mAnimationCallbacks = new ArrayList<>();
        }

        if (mAnimationCallbacks.contains(callback)) {
            // If this call back is already in, then don't need to append another copy.
            return;
        }

        mAnimationCallbacks.add(callback);

        if (mAnimatorListener == null) {
            // Create a animator listener and trigger the callback events when listener is
            // triggered.
            mAnimatorListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ArrayList<Animatable2Compat.AnimationCallback> tmpCallbacks =
                            new ArrayList<>(mAnimationCallbacks);
                    int size = tmpCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        tmpCallbacks.get(i).onAnimationStart(AnimatedVectorDrawableCompat.this);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ArrayList<Animatable2Compat.AnimationCallback> tmpCallbacks =
                            new ArrayList<>(mAnimationCallbacks);
                    int size = tmpCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        tmpCallbacks.get(i).onAnimationEnd(AnimatedVectorDrawableCompat.this);
                    }
                }
            };
        }
        mAnimatedVectorState.mAnimatorSet.addListener(mAnimatorListener);
    }

    /**
     * A helper function to clean up the animator listener in the mAnimatorSet.
     */
    private void removeAnimatorSetListener() {
        if (mAnimatorListener != null) {
            mAnimatedVectorState.mAnimatorSet.removeListener(mAnimatorListener);
            mAnimatorListener = null;
        }
    }

    @Override
    public boolean unregisterAnimationCallback(
            @NonNull Animatable2Compat.AnimationCallback callback) {
        if (mAnimationCallbacks == null || callback == null) {
            // Nothing to be removed.
            return false;
        }
        boolean removed = mAnimationCallbacks.remove(callback);

        //  When the last call back unregistered, remove the listener accordingly.
        if (mAnimationCallbacks.size() == 0) {
            removeAnimatorSetListener();
        }
        return removed;
    }

    @Override
    public void clearAnimationCallbacks() {
        removeAnimatorSetListener();
        if (mAnimationCallbacks == null) {
            return;
        }

        mAnimationCallbacks.clear();
    }

    /**
     * Utility function to register callback to Drawable, when the drawable is created from XML and
     * referred in Java code, e.g: ImageView.getDrawable().
     * From API 24 on, the drawable is treated as an AnimatedVectorDrawable.
     * Otherwise, it is treated as AnimatedVectorDrawableCompat.
     */
    public static void registerAnimationCallback(Drawable dr,
                                                 Animatable2Compat.AnimationCallback callback) {
        if (dr == null || callback == null) {
            return;
        }
        if (!(dr instanceof Animatable)) {
            return;
        }
        ((AnimatedVectorDrawableCompat) dr).registerAnimationCallback(callback);
    }

    /**
     * Utility function to unregister animation callback from Drawable, when the drawable is
     * created from XML and referred in Java code, e.g: ImageView.getDrawable().
     * From API 24 on, the drawable is treated as an AnimatedVectorDrawable.
     * Otherwise, it is treated as AnimatedVectorDrawableCompat.
     */
    public static boolean unregisterAnimationCallback(Drawable dr,
                                                      Animatable2Compat.AnimationCallback callback) {
        if (dr == null || callback == null) {
            return false;
        }
        if (!(dr instanceof Animatable)) {
            return false;
        }
        return ((AnimatedVectorDrawableCompat) dr).unregisterAnimationCallback(callback);
    }

    /**
     * Utility function to clear animation callbacks from Drawable, when the drawable is
     * created from XML and referred in Java code, e.g: ImageView.getDrawable().
     * From API 24 on, the drawable is treated as an AnimatedVectorDrawable.
     * Otherwise, it is treated as AnimatedVectorDrawableCompat.
     */
    public static void clearAnimationCallbacks(Drawable dr) {
        if (dr == null || !(dr instanceof Animatable)) {
            return;
        }
        ((AnimatedVectorDrawableCompat) dr).clearAnimationCallbacks();

    }
}
