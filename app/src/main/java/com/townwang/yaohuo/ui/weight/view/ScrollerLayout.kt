package com.townwang.yaohuo.ui.weight.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import androidx.core.view.ViewConfigurationCompat
import kotlin.math.abs

class ScrollerLayout(
    context: Context?,
    attrs: AttributeSet?
) : ViewGroup(context, attrs) {
    /**
     * 用于完成滚动操作的实例
     */
    private val mScroller: Scroller = Scroller(context)

    /**
     * 判定为拖动的最小移动像素数
     */
    private val mTouchSlop: Int

    /**
     * 手机按下时的屏幕坐标
     */
    private var mXDown = 0f

    /**
     * 手机当时所处的屏幕坐标
     */
    private var mXMove = 0f

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private var mXLastMove = 0f

    /**
     * 界面可滚动的左边界
     */
    private var leftBorder = 0

    /**
     * 界面可滚动的右边界
     */
    private var rightBorder = 0
    private var mSwipViewWidth = 0
    private var scrolledX = 0
    private var xDown = 0f
    private var yDown = 0f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measuredHeight = 0
        val childCount = childCount
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            // 为ScrollerLayout中的每一个子控件测量大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            measuredHeight = measuredHeight.coerceAtLeast(childView.measuredHeight)
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        if (changed) {
            val childCount = childCount
            check(childCount <= 2) { "you can have at most two child views!" }
            if (childCount <= 0) {
                return
            }
            var width = 0
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
                val measuredWidth = childView.measuredWidth
                childView.layout(width, 0, width + measuredWidth, childView.measuredHeight)
                width += measuredWidth
                if (i == childCount - 1) {
                    mSwipViewWidth = measuredWidth
                }
            }
            // 初始化左右边界值
            leftBorder = getChildAt(0).left
            rightBorder = getChildAt(getChildCount() - 1).right
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                xDown = ev.x
                yDown = ev.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> if (abs(xDown - ev.x) > abs(
                    yDown - ev.y
                )
            ) {
                parent.requestDisallowInterceptTouchEvent(true)
            } else {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mXDown = ev.rawX
                mXLastMove = mXDown
            }
            MotionEvent.ACTION_MOVE -> {
                mXMove = ev.rawX
                val diff = abs(mXMove - mXDown)
                mXLastMove = mXMove
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mXMove = event.rawX
                scrolledX = (mXLastMove - mXMove).toInt()
                if (scrollX + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0)
                    return true
                } else if (scrollX + width + scrolledX > rightBorder) {
                    scrollTo(rightBorder - width, 0)
                    return true
                }
                scrollBy(scrolledX, 0)
                mXLastMove = mXMove
            }
            MotionEvent.ACTION_UP -> {
                val dx: Int
                val targetX = if (scrolledX > 0) mSwipViewWidth / 5 else mSwipViewWidth * 4 / 5
                //判断打开还是关闭
                dx = if (scrollX > targetX) {
                    mSwipViewWidth - scrollX
                } else {
                    -scrollX
                }
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                mScroller.startScroll(scrollX, 0, dx, 0)
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }

    fun open() {
        if (scrollX > 0) {
            return
        }
        mScroller.startScroll(0, 0, mSwipViewWidth, 0)
        invalidate()
    }

    fun close() {
        if (scrollX <= 0) {
            return
        }
        mScroller.startScroll(scrollX, 0, -scrollX, 0)
        invalidate()
    }

    val isOpen: Boolean
        get() = scrollX > 0

    init {
        // 第一步，创建Scroller的实例
        val configuration = ViewConfiguration.get(context)
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)
        isClickable = true
        isFocusable = true
    }
}