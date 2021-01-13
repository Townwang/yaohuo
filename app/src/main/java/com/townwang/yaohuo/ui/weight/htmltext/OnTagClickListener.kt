package com.townwang.yaohuo.ui.weight.htmltext

import android.content.Context

interface OnTagClickListener {
    /**
     * 图片被点击
     */
    fun onImageClick(
        context: Context,
        imageUrlList: List<String>,
        position: Int
    )

    /**
     * 链接被点击
     */
    fun onLinkClick(context: Context, url: String)
}