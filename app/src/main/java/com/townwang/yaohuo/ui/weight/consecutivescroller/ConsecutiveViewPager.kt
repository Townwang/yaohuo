package com.townwang.yaohuo.ui.weight.consecutivescroller

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.townwang.yaohuo.ui.weight.consecutivescroller.ScrollUtils.isConsecutiveScrollerChild
import java.util.*

class ConsecutiveViewPager : ViewPager, IConsecutiveScroller {
    private var mAdjustHeight = 0

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        if (isConsecutiveParentAndBottom && mAdjustHeight > 0) {
            val height = View.getDefaultSize(0, heightMeasureSpec) - mAdjustHeight
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec))
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun addView(
        child: View,
        index: Int,
        params: ViewGroup.LayoutParams
    ) {
        super.addView(child, index, params)

        // 去掉子View的滚动条。选择在这里做这个操作，而不是在onFinishInflate方法中完成，是为了兼顾用代码add子View的情况
        if (isConsecutiveScrollerChild(this)) {
            disableChildScroll(child)
            if (child is ViewGroup) {
                child.clipToPadding = false
            }
        }
    }

    /**
     * 禁用子view的一下滑动相关的属性
     *
     * @param child
     */
    private fun disableChildScroll(child: View) {
        child.isVerticalScrollBarEnabled = false
        child.isHorizontalScrollBarEnabled = false
        child.overScrollMode = View.OVER_SCROLL_NEVER
        ViewCompat.setNestedScrollingEnabled(child, false)
    }

    /**
     * 是否在ConsecutiveScrollerLayout的底部
     *
     * @return
     */
    private val isConsecutiveParentAndBottom: Boolean
        private get() {
            val parent = parent
            if (parent is ConsecutiveScrollerLayout) {
                val layout = parent
                return layout.indexOfChild(this) == layout.childCount - 1
            }
            return false
        }

    var adjustHeight: Int
        get() = mAdjustHeight
        set(adjustHeight) {
            if (mAdjustHeight != adjustHeight) {
                mAdjustHeight = adjustHeight
                requestLayout()
            }
        }

    /**
     * 返回当前需要滑动的view。
     *
     * @return
     */
    override val currentScrollerView: View
        get() {
            val count = childCount
            for (i in 0 until count) {
                val view = getChildAt(i)
                if (view.x == scrollX + paddingLeft.toFloat()) {
                    return view
                }
            }
            return this
        }

    /**
     * 返回全部需要滑动的下级view
     *
     * @return
     */
    override val scrolledViews: List<View>
        get() {
            val views: MutableList<View> = ArrayList()
            val count = childCount
            if (count > 0) {
                for (i in 0 until count) {
                    views.add(getChildAt(i))
                }
            }
            return views
        }
}