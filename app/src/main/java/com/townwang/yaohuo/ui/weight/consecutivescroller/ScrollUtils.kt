package com.townwang.yaohuo.ui.weight.consecutivescroller

import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.ScrollingView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

object ScrollUtils {
    @JvmStatic
    fun computeVerticalScrollOffset(view: View): Int {
        val scrolledView = getScrolledView(view)
        if (scrolledView is ScrollingView) {
            return (scrolledView as ScrollingView).computeVerticalScrollOffset()
        }
        try {
            val method =
                View::class.java.getDeclaredMethod("computeVerticalScrollOffset")
            method.isAccessible = true
            return method.invoke(scrolledView) as Int
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return scrolledView.scrollY
    }

    @JvmStatic
    fun computeVerticalScrollRange(view: View): Int {
        val scrolledView = getScrolledView(view)
        if (scrolledView is ScrollingView) {
            return (scrolledView as ScrollingView).computeVerticalScrollRange()
        }
        try {
            val method =
                View::class.java.getDeclaredMethod("computeVerticalScrollRange")
            method.isAccessible = true
            return method.invoke(scrolledView) as Int
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return scrolledView.height
    }

    fun computeVerticalScrollExtent(view: View): Int {
        val scrolledView = getScrolledView(view)
        if (scrolledView is ScrollingView) {
            return (scrolledView as ScrollingView).computeVerticalScrollExtent()
        }
        try {
            val method =
                View::class.java.getDeclaredMethod("computeVerticalScrollExtent")
            method.isAccessible = true
            return method.invoke(scrolledView) as Int
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return scrolledView.height
    }

    /**
     * 获取View滑动到自身顶部的偏移量
     *
     * @param view
     * @return
     */
    @JvmStatic
    fun getScrollTopOffset(view: View): Int {
        return if (isConsecutiveScrollerChild(view) && canScrollVertically(
                view,
                -1
            )
        ) {
            Math.min(-computeVerticalScrollOffset(view), -1)
        } else {
            0
        }
    }

    /**
     * 获取View滑动到自身底部的偏移量
     *
     * @param view
     * @return
     */
    @JvmStatic
    fun getScrollBottomOffset(view: View): Int {
        return if (isConsecutiveScrollerChild(view) && canScrollVertically(
                view,
                1
            )
        ) {
            Math.max(
                computeVerticalScrollRange(view) - computeVerticalScrollOffset(
                    view
                )
                        - computeVerticalScrollExtent(view), 1
            )
        } else {
            0
        }
    }

    /**
     * 是否是可以垂直滚动View。(内容可以滚动，或者本身就是个滚动布局)
     *
     * @param view
     * @return
     */
    fun canScrollVertically(view: View): Boolean {
        return isConsecutiveScrollerChild(view) && (canScrollVertically(
            view,
            1
        ) || canScrollVertically(view, -1))
    }

    private val mBounds = Rect()

    /**
     * 判断是否可以滑动
     *
     * @param view
     * @param direction
     * @return
     */
    @JvmStatic
    fun canScrollVertically(view: View, direction: Int): Boolean {
        val scrolledView = getScrolledView(view)
        if (scrolledView.visibility == View.GONE) {
            // 隐藏状态，不能滑动
            return false
        }
        return if (scrolledView is AbsListView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                scrolledView.canScrollList(direction)
            } else {
                // 低版本手机(android 19以下)，AbsListView不支持滑动
                false
            }
        } else {
            // RecyclerView通过canScrollVertically方法判断滑动到边界不准确，需要单独处理
            if (scrolledView is RecyclerView) {
                val recyclerView = scrolledView
                if (recyclerView.canScrollHorizontally(1) || recyclerView.canScrollVertically(-1)) {
                    // 如果recyclerView可以水平滑动，并且使用canScrollVertically判断不能垂直滑动，这认定是不能垂直滑动的。
                    // 这样做既兼顾了recyclerView同时水平、垂直滑动的情况，又保证了垂直滑动的判断是通过自定义的方式判断的。
                    if (!recyclerView.canScrollVertically(direction)) {
                        return false
                    }
                }
                val layoutManager = recyclerView.layoutManager
                val adapter = recyclerView.adapter
                if (layoutManager != null && adapter != null && adapter.itemCount > 0) {
                    val itemView =
                        layoutManager.findViewByPosition(if (direction > 0) adapter.itemCount - 1 else 0)
                            ?: return true
                } else {
                    return false
                }
                val count = recyclerView.childCount
                return if (direction > 0) {
                    for (i in count - 1 downTo 0) {
                        val child = recyclerView.getChildAt(i)
                        recyclerView.getDecoratedBoundsWithMargins(child, mBounds)
                        if (mBounds.bottom > recyclerView.height - recyclerView.paddingBottom) {
                            return true
                        }
                    }
                    false
                } else {
                    for (i in 0 until count) {
                        val child = recyclerView.getChildAt(i)
                        recyclerView.getDecoratedBoundsWithMargins(child, mBounds)
                        if (mBounds.top < recyclerView.paddingTop) {
                            return true
                        }
                    }
                    false
                }
            }
            scrolledView.canScrollVertically(direction)
        }
    }

    /**
     * 获取当前触摸点下的View
     *
     * @param rootView
     * @param touchX
     * @param touchY
     * @return
     */
    fun getTouchViews(
        rootView: View,
        touchX: Int,
        touchY: Int
    ): List<View> {
        val views: MutableList<View> = ArrayList()
        addTouchViews(views, rootView, touchX, touchY)
        return views
    }

    private fun addTouchViews(
        views: MutableList<View>,
        view: View,
        touchX: Int,
        touchY: Int
    ) {
        if (isConsecutiveScrollerChild(view) && isTouchPointInView(
                view,
                touchX,
                touchY
            )
        ) {
            views.add(view)
            if (view is ViewGroup) {
                val count = view.childCount
                for (i in 0 until count) {
                    addTouchViews(views, view.getChildAt(i), touchX, touchY)
                }
            }
        }
    }

    /**
     * 判断触摸点是否在View内
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    @JvmStatic
    fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        val position = IntArray(2)
        view.getLocationOnScreen(position)
        val left = position[0]
        val top = position[1]
        val right = left + view.measuredWidth
        val bottom = top + view.measuredHeight
        return x in left..right && y >= top && y <= bottom
    }

    @JvmStatic
    fun getRawX(rootView: View, ev: MotionEvent, pointerIndex: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ev.getRawX(pointerIndex).toInt()
        } else {
            val position = IntArray(2)
            rootView.getLocationOnScreen(position)
            val left = position[0]
            (left + ev.getX(pointerIndex)).toInt()
        }
    }

    @JvmStatic
    fun getRawY(rootView: View, ev: MotionEvent, pointerIndex: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ev.getRawY(pointerIndex).toInt()
        } else {
            val position = IntArray(2)
            rootView.getLocationOnScreen(position)
            val top = position[1]
            (top + ev.getY(pointerIndex)).toInt()
        }
    }

    fun getScrollOffsetForViews(views: List<View>): List<Int> {
        val offsets: MutableList<Int> = ArrayList()
        for (view in views) {
            offsets.add(computeVerticalScrollOffset(view))
        }
        return offsets
    }

    fun equalsOffsets(
        offsets1: List<Int>,
        offsets2: List<Int>
    ): Boolean {
        if (offsets1.size != offsets2.size) {
            return false
        }
        val size = offsets1.size
        for (i in 0 until size) {
            if (offsets1[i] != offsets2[i]) {
                return false
            }
        }
        return true
    }

    /**
     * 判断View是否是支持连续滚动的
     *
     * @param view
     * @return
     */
    @JvmStatic
    fun isConsecutiveScrollerChild(view: View?): Boolean {
        if (view != null) {
            val lp = view.layoutParams
            return if (lp is ConsecutiveScrollerLayout.LayoutParams) {
                lp.isConsecutive
            } else true
        }
        return false
    }

    /**
     * 返回需要滑动的view，如果没有，就返回本身。
     *
     * @param view
     * @return
     */
    @JvmStatic
    fun getScrolledView(view: View): View {
        var consecutiveView: View?
        var scrolledView = view
        while (scrolledView is IConsecutiveScroller) {
            consecutiveView = scrolledView
            scrolledView =scrolledView.currentScrollerView!!
            if (consecutiveView === scrolledView) {
                break
            }
        }
        return scrolledView
    }

    @JvmStatic
    fun startInterceptRequestLayout(view: RecyclerView): Boolean {
        if ("InterceptRequestLayout" == view.tag) {
            try {
                val method =
                    RecyclerView::class.java.getDeclaredMethod("startInterceptRequestLayout")
                method.isAccessible = true
                method.invoke(view)
                return true
            } catch (e: Exception) {
            }
        }
        return false
    }

    @JvmStatic
    fun stopInterceptRequestLayout(view: RecyclerView) {
        if ("InterceptRequestLayout" == view.tag) {
            try {
                val method = RecyclerView::class.java.getDeclaredMethod(
                    "stopInterceptRequestLayout",
                    Boolean::class.javaPrimitiveType
                )
                method.isAccessible = true
                method.invoke(view, false)
            } catch (e: Exception) {
            }
        }
    }

    /**
     * 判断父级容器是否是isConsecutive：true.判断到最近的ConsecutiveScrollerLayout容器
     *
     * @param view
     * @return
     */
    @JvmStatic
    fun isConsecutiveScrollParent(view: View): Boolean {
        var child = view
        while (child.parent is ViewGroup && child.parent !is ConsecutiveScrollerLayout) {
            child = child.parent as View
        }
        return if (child.parent is ConsecutiveScrollerLayout) {
            isConsecutiveScrollerChild(child)
        } else {
            false
        }
    }

    /**
     * 判断当前触摸点下是否有view可以水平滑动
     *
     * @param rootView
     * @param touchX
     * @param touchY
     * @return
     */
    @JvmStatic
    fun isHorizontalScroll(rootView: View, touchX: Int, touchY: Int): Boolean {
        val views =
            getTouchViews(rootView, touchX, touchY)
        for (view in views) {
            if (view.canScrollHorizontally(1) || view.canScrollHorizontally(-1)) {
                return true
            }
        }
        return false
    }

    /**
     * 是否触摸吸顶view并且不能触发布局滑动
     *
     * @return
     */
    @JvmStatic
    fun isTouchNotTriggerScrollStick(
        rootView: View,
        touchX: Int,
        touchY: Int
    ): Boolean {
        val csLayouts =
            getInTouchCSLayout(rootView, touchX, touchY)
        val size = csLayouts.size
        for (i in size - 1 downTo 0) {
            val csl = csLayouts[i]
            val topView = getTopViewInTouch(csl, touchX, touchY)
            if (topView != null && csl.isStickyView(topView) && csl.theChildIsStick(topView)) {
                val lp =
                    topView.layoutParams as ConsecutiveScrollerLayout.LayoutParams
                if (!lp.isTriggerScroll) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 获取触摸点下的所有ConsecutiveScrollerLayout
     *
     * @param rootView
     * @param touchX
     * @param touchY
     * @return
     */
    fun getInTouchCSLayout(
        rootView: View,
        touchX: Int,
        touchY: Int
    ): List<ConsecutiveScrollerLayout> {
        val csLayouts: MutableList<ConsecutiveScrollerLayout> =
            ArrayList()
        val views =
            getTouchViews(rootView, touchX, touchY)
        for (view in views) {
            if (view is ConsecutiveScrollerLayout) {
                csLayouts.add(view)
            }
        }
        return csLayouts
    }

    fun getTopViewInTouch(
        csl: ConsecutiveScrollerLayout,
        touchX: Int,
        touchY: Int
    ): View? {
        val count = csl.childCount
        var topTouchView: View? = null
        for (i in 0 until count) {
            val child = csl.getChildAt(i)
            if (child.visibility == View.VISIBLE && isTouchPointInView(
                    child,
                    touchX,
                    touchY
                )
            ) {
                if (topTouchView == null) {
                    topTouchView = child
                    continue
                }
                if (ViewCompat.getZ(child) > ViewCompat.getZ(topTouchView) // 判断View的Z高度
                    || csl.getDrawingPosition(child) > csl.getDrawingPosition(topTouchView)
                ) { // 判断绘制顺序
                    topTouchView = child
                }
            }
        }
        return topTouchView
    }
}