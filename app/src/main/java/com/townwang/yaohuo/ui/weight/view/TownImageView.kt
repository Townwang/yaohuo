package com.townwang.yaohuo.ui.weight.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.townwang.yaohuo.R

class TownImageView(
    context: Context,
    attrs: AttributeSet?
) : AppCompatImageView(context, attrs) {
    private val mType: Int
    private val mBorderColor: Int
    private val mBorderWidth: Int
    private val mRectRoundRadius: Int
    private val mPaintBitmap =
        Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintBorder =
        Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRectBorder = RectF()
    private val mRectBitmap = RectF()
    private var mRawBitmap: Bitmap? = null
    private var mShader: BitmapShader? = null
    private val mMatrix = Matrix()
    override fun onDraw(canvas: Canvas) {
        val rawBitmap = getBitmap(drawable)
        if (rawBitmap != null && mType != TYPE_NONE) {
            val viewWidth = width
            val viewHeight = height
            val viewMinSize = viewWidth.coerceAtMost(viewHeight)
            val dstWidth =
                if (mType == TYPE_CIRCLE) viewMinSize.toFloat() else viewWidth.toFloat()
            val dstHeight =
                if (mType == TYPE_CIRCLE) viewMinSize.toFloat() else viewHeight.toFloat()
            val halfBorderWidth = mBorderWidth / 2.0f
            val doubleBorderWidth = mBorderWidth * 2.toFloat()
            if (mShader == null || rawBitmap != mRawBitmap) {
                mRawBitmap = rawBitmap
                mShader = BitmapShader(mRawBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
            if (mShader != null) {
                mMatrix.setScale(
                    (dstWidth - doubleBorderWidth) / rawBitmap.width,
                    (dstHeight - doubleBorderWidth) / rawBitmap.height
                )
                mShader!!.setLocalMatrix(mMatrix)
            }
            mPaintBitmap.shader = mShader
            mPaintBorder.style = Paint.Style.STROKE
            mPaintBorder.strokeWidth = mBorderWidth.toFloat()
            mPaintBorder.color = if (mBorderWidth > 0) mBorderColor else Color.TRANSPARENT
            if (mType == TYPE_CIRCLE) {
                val radius = viewMinSize / 2.0f
                canvas.drawCircle(radius, radius, radius - halfBorderWidth, mPaintBorder)
                canvas.translate(mBorderWidth.toFloat(), mBorderWidth.toFloat())
                canvas.drawCircle(
                    radius - mBorderWidth,
                    radius - mBorderWidth,
                    radius - mBorderWidth,
                    mPaintBitmap
                )
            } else if (mType == TYPE_ROUNDED_RECT) {
                mRectBorder[halfBorderWidth, halfBorderWidth, dstWidth - halfBorderWidth] =
                    dstHeight - halfBorderWidth
                mRectBitmap[0.0f, 0.0f, dstWidth - doubleBorderWidth] =
                    dstHeight - doubleBorderWidth
                val borderRadius =
                    if (mRectRoundRadius - halfBorderWidth > 0.0f) mRectRoundRadius - halfBorderWidth else 0.0f
                val bitmapRadius =
                    if (mRectRoundRadius - mBorderWidth > 0.0f) (mRectRoundRadius - mBorderWidth).toFloat() else 0.0f
                canvas.drawRoundRect(mRectBorder, borderRadius, borderRadius, mPaintBorder)
                canvas.translate(mBorderWidth.toFloat(), mBorderWidth.toFloat())
                canvas.drawRoundRect(mRectBitmap, bitmapRadius, bitmapRadius, mPaintBitmap)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    private fun dip2px(dipVal: Int): Int {
        val scale = resources.displayMetrics.density
        return (dipVal * scale + 0.5f).toInt()
    }

    private fun getBitmap(drawable: Drawable): Bitmap? {
        return when (drawable) {
            is BitmapDrawable -> {
                drawable.bitmap
            }
            is ColorDrawable -> {
                val rect = drawable.getBounds()
                val width = rect.right - rect.left
                val height = rect.bottom - rect.top
                val color = drawable.color
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawARGB(
                    Color.alpha(color),
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
                )
                bitmap
            }
            else -> {
                null
            }
        }
    }

    companion object {
        /**
         * 不做处理
         */
        const val TYPE_NONE = 0

        /**
         *圆形
         */
        const val TYPE_CIRCLE = 1

        /**
         * 圆角矩形
         */
        const val TYPE_ROUNDED_RECT = 2
        private const val DEFAULT_TYPE = TYPE_NONE
        private const val DEFAULT_BORDER_COLOR = Color.TRANSPARENT
        private const val DEFAULT_BORDER_WIDTH = 0
        private const val DEFAULT_RECT_ROUND_RADIUS = 0
    }

    init {
        //取xml文件中设定的参数
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ImageViewPlus)
        mType = ta.getInt(
            R.styleable.ImageViewPlus_type,
            DEFAULT_TYPE
        )
        mBorderColor = ta.getColor(
            R.styleable.ImageViewPlus_borderColor,
            DEFAULT_BORDER_COLOR
        )
        mBorderWidth = ta.getDimensionPixelSize(
            R.styleable.ImageViewPlus_borderWidth,
            dip2px(DEFAULT_BORDER_WIDTH)
        )
        mRectRoundRadius = ta.getDimensionPixelSize(
            R.styleable.ImageViewPlus_rectRoundRadius,
            dip2px(DEFAULT_RECT_ROUND_RADIUS)
        )
        ta.recycle()
    }
}