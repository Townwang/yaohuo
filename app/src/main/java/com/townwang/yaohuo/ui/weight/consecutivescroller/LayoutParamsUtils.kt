package com.townwang.yaohuo.ui.weight.consecutivescroller

import android.view.ViewGroup.MarginLayoutParams

object LayoutParamsUtils {
    /**
     * 使子view的topMargin和bottomMargin属性无效
     *
     * @param params
     */
    @JvmStatic
    fun invalidTopAndBottomMargin(params: MarginLayoutParams?) {
        if (params != null) {
            params.topMargin = 0
            params.bottomMargin = 0
        }
    }
}