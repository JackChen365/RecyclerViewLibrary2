package com.cz.widget.pulltorefresh.vector;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;

import org.xmlpull.v1.XmlPullParser;

/**
 * Compat methods for accessing TypedArray values.
 *
 * All the getNamed*() functions added the attribute name match, to take care of potential ID
 * collision between the private attributes in older OS version (OEM) and the attributes existed in
 * the newer OS version.
 * For example, if an private attribute named "abcdefg" in Kitkat has the
 * same id value as "android:pathData" in Lollipop, we need to match the attribute's namefirst.
 *
 */
class TypedArrayUtils {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    /**
     * @return Whether the current node ofthe  {@link XmlPullParser} has an attribute with the
     * specified {@code attrName}.
     */
    public static boolean hasAttribute(@NonNull XmlPullParser parser, @NonNull String attrName) {
        return parser.getAttributeValue(NAMESPACE, attrName) != null;
    }

    /**
     * Retrieves a float attribute value. In addition to the styleable resource ID, we also make
     * sure that the attribute name matches.
     *
     * @return a float value in the {@link TypedArray} with the specified {@code resId}, or
     * {@code defaultValue} if it does not exist.
     */
    public static float getNamedFloat(@NonNull TypedArray a, @NonNull XmlPullParser parser,
                                      @NonNull String attrName, @StyleableRes int resId, float defaultValue) {
        final boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        } else {
            return a.getFloat(resId, defaultValue);
        }
    }

    /**
     * Retrieves a boolean attribute value. In addition to the styleable resource ID, we also make
     * sure that the attribute name matches.
     *
     * @return a boolean value in the {@link TypedArray} with the specified {@code resId}, or
     * {@code defaultValue} if it does not exist.
     */
    public static boolean getNamedBoolean(@NonNull TypedArray a, @NonNull XmlPullParser parser,
                                          String attrName, @StyleableRes int resId, boolean defaultValue) {
        final boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        } else {
            return a.getBoolean(resId, defaultValue);
        }
    }

    /**
     * Retrieves an int attribute value. In addition to the styleable resource ID, we also make
     * sure that the attribute name matches.
     *
     * @return an int value in the {@link TypedArray} with the specified {@code resId}, or
     * {@code defaultValue} if it does not exist.
     */
    public static int getNamedInt(@NonNull TypedArray a, @NonNull XmlPullParser parser,
                                  String attrName, @StyleableRes int resId, int defaultValue) {
        final boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        } else {
            return a.getInt(resId, defaultValue);
        }
    }

    /**
     * Retrieves a color attribute value. In addition to the styleable resource ID, we also make
     * sure that the attribute name matches.
     *
     * @return a color value in the {@link TypedArray} with the specified {@code resId}, or
     * {@code defaultValue} if it does not exist.
     */
    @ColorInt
    public static int getNamedColor(@NonNull TypedArray a, @NonNull XmlPullParser parser,
                                    String attrName, @StyleableRes int resId, @ColorInt int defaultValue) {
        final boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        } else {
            return a.getColor(resId, defaultValue);
        }
    }

    /**
     * Obtains styled attributes from the theme, if available, or unstyled
     * resources if the theme is null.
     */
    public static TypedArray obtainAttributes(
            Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

}
