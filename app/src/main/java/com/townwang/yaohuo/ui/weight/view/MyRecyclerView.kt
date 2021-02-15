package com.townwang.yaohuo.ui.weight.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerView(
    context: Context,
    attrs: AttributeSet?
) : RecyclerView(context, attrs) {
    var mLastTouchPosition = -1
    private var mLastTouchItem: ScrollerLayout? = null
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action and MotionEvent.ACTION_MASK
        var intercept = super.onInterceptTouchEvent(ev)
        if (action == MotionEvent.ACTION_DOWN) {
            intercept = false //1.父View默认不在Down时拦截
            val x = ev.x
            val y = ev.y
            val childAdapterPosition = getChildAdapterPosition(findChildViewUnder(x, y)!!)
            //3.当这次点击的item和上次不是同一个item,且上一个点击的item是ScrollerLayout类型，并且这个滑动菜单是开着的，就关掉菜单并执行RecyclerView的操作
            if (childAdapterPosition != mLastTouchPosition && null != mLastTouchItem && mLastTouchItem!!.isOpen) {
                mLastTouchItem!!.close()
                intercept = true
            }
            if (intercept) {
                mLastTouchPosition = -1
                mLastTouchItem = null
            } else {
                mLastTouchPosition = childAdapterPosition
                val viewHolderForAdapterPosition =
                    findViewHolderForAdapterPosition(childAdapterPosition)
                //2.如果点击的条目是ScrollerLayout类型（支持滑动菜单的item），就赋值记录
                if (null != viewHolderForAdapterPosition && viewHolderForAdapterPosition.itemView is ScrollerLayout) {
                    mLastTouchItem = viewHolderForAdapterPosition.itemView as ScrollerLayout
                }
            }
        }
        return intercept
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_DRAGGING) {
            //滑动时关掉菜单
            if (null != mLastTouchItem && mLastTouchItem!!.isOpen) {
                mLastTouchItem!!.close()
            }
        }
    }
}