package com.townwang.yaohuo.ui.weight.consecutivescroller

import android.view.View

interface IConsecutiveScroller {
    /**
     * 返回当前需要滑动的下级view。在一个时间点里只能有一个view可以滑动。
     * @return
     */
    val currentScrollerView: View?

    /**
     * 返回所有可以滑动的子view。由于ConsecutiveScrollerLayout允许它的子view包含多个可滑动的子view，所以返回一个view列表。
     * @return
     */
    val scrolledViews: List<View?>?
}