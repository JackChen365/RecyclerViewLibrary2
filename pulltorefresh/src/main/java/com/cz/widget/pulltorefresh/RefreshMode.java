package com.cz.widget.pulltorefresh;

/**
 * @author Created by cz
 * @date 2020-03-02 20:30
 * @email bingo110@126.com
 *
 * The different mode of the layout
 * You could both pull either top or bottom.
 */
public enum RefreshMode {
    BOTH, PULL_START, PULL_END, NONE;
    /**
     * @return isDisable refresh
     */
    public boolean isDisable(){
        return this == NONE;
    }

    /**
     * @return enable header refresh
     */
    public boolean isEnableStart(){
        return this==PULL_START||this==BOTH;
    }

    /**
     * @return enable footer refresh
     */
    public boolean isEnableEnd(){
        return this == PULL_END||this==BOTH;
    }
}
