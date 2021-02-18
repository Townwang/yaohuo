package com.townwang.yaohuo.common.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabBehavior(
    context: Context?,
    attrs: AttributeSet?
) : CoordinatorLayout.Behavior<View?>() {
    private var visible = true //是否可见
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
        if (child is FloatingActionButton) {
            if (dyConsumed > 0 && visible) {
                //show
                visible = false
                onHide(child)
            } else if (dyConsumed < 0) {
                //hide
                visible = true
                onShow(child)
            }
        }
    }

    fun onHide(fab: FloatingActionButton?) {
        ViewCompat.animate(fab!!).scaleX(0f).scaleY(0f).start()
    }

    fun onShow(fab: FloatingActionButton?) {
        ViewCompat.animate(fab!!).scaleX(1f).scaleY(1f).start()
    }
}