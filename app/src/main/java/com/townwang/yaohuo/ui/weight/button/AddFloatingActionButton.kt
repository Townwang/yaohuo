package com.townwang.yaohuo.ui.weight.button

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import com.townwang.yaohuo.R

open class AddFloatingActionButton : FloatingActionButton {
    @JvmField
    var mPlusColor = 0

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

     final override fun init(
        context: Context,
        attributeSet: AttributeSet?
    ) {
        val attr = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.AddFloatingActionButton,
            0,
            0
        )
        mPlusColor = attr.getColor(
            R.styleable.AddFloatingActionButton_fab_plusIconColor,
            getColor(android.R.color.black)
        )
        attr.recycle()
        super.init(context, attributeSet)
    }

    /**
     * @return the current Color of plus icon.
     */
    var plusColor: Int
        get() = mPlusColor
        set(color) {
            if (mPlusColor != color) {
                mPlusColor = color
                updateBackground()
            }
        }
    override fun setIcon(@DrawableRes icon: Int) {
        throw UnsupportedOperationException("Use FloatingActionButton if you want to use custom icon")
    }

     fun dgetIconDrawable(): Drawable {
        val iconSize = getDimension(R.dimen.fab_icon_size)
        val iconHalfSize = iconSize / 2f
        val plusSize = getDimension(R.dimen.fab_plus_icon_size)
        val plusHalfStroke =
            getDimension(R.dimen.fab_plus_icon_stroke) / 2f
        val plusOffset = (iconSize - plusSize) / 2f
        val shape: Shape =
            object : Shape() {
                override fun draw(
                    canvas: Canvas,
                    paint: Paint
                ) {
                    canvas.drawRect(
                        plusOffset,
                        iconHalfSize - plusHalfStroke,
                        iconSize - plusOffset,
                        iconHalfSize + plusHalfStroke,
                        paint
                    )
                    canvas.drawRect(
                        iconHalfSize - plusHalfStroke,
                        plusOffset,
                        iconHalfSize + plusHalfStroke,
                        iconSize - plusOffset,
                        paint
                    )
                }
            }
        val drawable = ShapeDrawable(shape)
        val paint = drawable.paint
        paint.color = mPlusColor
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        return drawable
    }
}