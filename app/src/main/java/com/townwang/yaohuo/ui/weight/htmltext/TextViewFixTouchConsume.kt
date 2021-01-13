package com.townwang.yaohuo.ui.weight.htmltext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
class TextViewFixTouchConsume : TextView {
    private var dontConsumeNonUrlClicks = true
//    private var calculatedLines = false
    var linkHit = false
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

//    override fun onDraw(canvas: Canvas?) {
//        if (!calculatedLines) {
//            setLines(measuredHeight/lineHeight)
//            calculatedLines = true
//        }
//        super.onDraw(canvas)
//    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        linkHit = false
        val res = super.onTouchEvent(event)
        return if (dontConsumeNonUrlClicks) linkHit else res
    }

    class LocalLinkMovementMethod : LinkMovementMethod() {
        override fun onTouchEvent(
            widget: TextView,
            buffer: Spannable, event: MotionEvent
        ): Boolean {
            val action = event.action
            if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN
            ) {
                var x = event.x.toInt()
                var y = event.y.toInt()
                x -= widget.totalPaddingLeft
                y -= widget.totalPaddingTop
                x += widget.scrollX
                y += widget.scrollY
                val layout = widget.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())
                val link = buffer.getSpans(
                    off, off, ClickableSpan::class.java
                )
                return if (link.isNotEmpty()) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget)
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(
                            buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0])
                        )
                    }
                    if (widget is TextViewFixTouchConsume) {
                        widget.linkHit = true
                    }
                    true
                } else {
                    Selection.removeSelection(buffer)
                    Touch.onTouchEvent(widget, buffer, event)
                    false
                }
            }
            return Touch.onTouchEvent(widget, buffer, event)
        }

        companion object {
            val instance = LocalLinkMovementMethod()
        }
    }

    override fun hasFocus(): Boolean {
        return false
    }

    override fun performLongClick(): Boolean {
        return false
    }
}