package com.cz.widget.pulltorefresh;

/**
 * @author Created by cz
 * @date 2020-03-02 20:32
 * @email bingo110@126.com
 * Different refresh event.
 */
public enum PullToRefreshState {
    /**
     * none. do nothing.
     */
    NONE,
    /**
     * The finger start the drag.
     */
    START_PULL,
    /**
     * Release drag but in a shorter distance. So we cancel the event.
     */
    RELEASE_TO_CANCEL,
    /**
     * Release but with a long distance. We will trigger refresh event.
     */
    RELEASE_TO_REFRESHING,
    /**
     * Somehow we can't get the data. Keep refreshing.
     */
    REFRESHING,
    /**
     * It keep refreshing. But when you allow user to drag while refreshing. The state may change many times.
     * So We have this specially state.
     */
    REFRESHING_DRAGGING,
    /**
     * Refresh completed.
     */
    REFRESHING_COMPLETE
}
