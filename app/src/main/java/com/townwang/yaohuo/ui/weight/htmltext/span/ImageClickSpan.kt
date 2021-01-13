package com.townwang.yaohuo.ui.weight.htmltext.span

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.townwang.yaohuo.ui.weight.htmltext.OnTagClickListener

class ImageClickSpan(
    private val context: Context,
    private val imageUrls: List<String>,
    private val position: Int
) : ClickableSpan() {
    private var listener: OnTagClickListener? = null
    fun setListener(listener: OnTagClickListener?) {
        this.listener = listener
    }

    override fun onClick(widget: View) {
            listener?.onImageClick(context, imageUrls, position)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = ds.linkColor
        ds.isUnderlineText = false
    }

}