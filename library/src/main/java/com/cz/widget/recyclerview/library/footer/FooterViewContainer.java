package com.cz.widget.recyclerview.library.footer;

import androidx.annotation.IdRes;

/**
 * @author Created by cz
 * @date 2020-03-24 11:18
 * @email bingo110@126.com
 */
public interface FooterViewContainer {
    /**
     * Show a frame in this container by id
     * This id was not a view's view.
     * @param id
     */
    void setFooterFrame(@IdRes int id);

    /**
     * Check if it's current frame.
     * @param id
     * @return
     */
    boolean isFooterFrame(@IdRes int id);
}
