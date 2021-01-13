package com.townwang.yaohuo.ui.weight.htmltext

import android.graphics.drawable.Drawable

/**
 * 图片加载器
 */
interface HtmlImageLoader {
    /**
     * 图片加载回调
     */
    interface Callback {
        /**
         * 加载成功
         */
        fun onLoadComplete(bitmap: Drawable?)

        /**
         * 加载失败
         */
        fun onLoadFailed()
    }

    /**
     * 加载图片
     */
    fun loadImage(
        url: String?,
        callback: Callback?
    )

    /**
     * 加载中的占位图
     */
    val defaultDrawable: Drawable?

    /**
     * 加载失败的占位图
     */
    val errorDrawable: Drawable?

    /**
     * 图片最大宽度，即TextView最大宽度
     */
    val maxWidth: Int

    /**
     * 是否强制将图片等比例拉伸到最大宽度<br></br>
     * 如果返回true，则需要指定[.getMaxWidth]
     */
    fun fitWidth(): Boolean
}