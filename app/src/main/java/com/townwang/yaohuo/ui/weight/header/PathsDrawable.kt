package com.townwang.yaohuo.ui.weight.header

import android.graphics.*
import android.graphics.drawable.Drawable
import com.scwang.smart.drawable.PaintDrawable
import com.townwang.yaohuo.ui.weight.header.PathParser.createPathFromPathData
import com.townwang.yaohuo.ui.weight.header.PathParser.transformScale

/**
 * 路径
 */
class PathsDrawable : PaintDrawable() {
    protected var mWidth = 1
    protected var mHeight = 1
    protected var mStartX = 0
    protected var mStartY = 0
    protected var mOriginWidth = 0
    protected var mOriginHeight = 0
    protected var mPaths: List<Path>? = null
    protected var mColors = arrayListOf<Int>()
    protected var mltOriginPath = arrayListOf<Path>()
    protected var mltOriginSvg = arrayListOf<String>()
    protected fun onMeasure(): Boolean {
        var top: Int? = null
        var left: Int? = null
        var right: Int? = null
        var bottom: Int? = null
        if (mPaths != null) {
            for (path in mPaths!!) {
                REGION.setPath(path, MAX_CLIP)
                val bounds = REGION.bounds
                top = (top ?: bounds.top).coerceAtMost(bounds.top)
                left = (left ?: bounds.left).coerceAtMost(bounds.left)
                right = (right ?: bounds.right).coerceAtLeast(bounds.right)
                bottom = (bottom ?: bounds.bottom).coerceAtLeast(bounds.bottom)
            }
        }
        mStartX = left ?: 0
        mStartY = top ?: 0
        mWidth = if (right == null) 0 else right - mStartX
        mHeight = if (bottom == null) 0 else bottom - mStartY
        if (mOriginWidth == 0) {
            mOriginWidth = mWidth
        }
        if (mOriginHeight == 0) {
            mOriginHeight = mHeight
        }
        val drawable: Drawable = this@PathsDrawable
        val bounds = drawable.bounds
        return if (mWidth == 0 || mHeight == 0) { //测量失败
            if (mOriginWidth == 0) {
                mOriginWidth = 1
            }
            if (mOriginHeight == 0) {
                mOriginHeight = 1
            }
            mHeight = 1
            mWidth = mHeight
            false
        } else {
            super.setBounds(bounds.left, bounds.top, bounds.left + mWidth, bounds.top + mHeight)
            true
        }
    }

    override fun setBounds(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val width = right - left
        val height = bottom - top
        if (mltOriginPath.size > 0 && (width != mWidth || height != mHeight)) {
            val ox = mStartX
            val oy = mStartY
            val ratioWidth = 1f * width / mOriginWidth
            val ratioHeight = 1f * height / mOriginHeight
            mPaths = transformScale(
                ratioWidth,
                ratioHeight,
                mltOriginPath
            )
            if (!onMeasure()) {
                mWidth = width
                mHeight = height
                mStartX = (1f * ox * width / mOriginWidth).toInt()
                mStartY = (1f * oy * height / mOriginHeight).toInt()
                super.setBounds(left, top, right, bottom)
            }
        } else {
            super.setBounds(left, top, right, bottom)
        }
    }

    override fun setBounds(bounds: Rect) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun parserPaths(vararg paths: String?): Boolean {
        mOriginHeight = 0
        mOriginWidth = mOriginHeight
        mPaths = mltOriginPath
        for (path in paths) {
            path?.also {
                mltOriginSvg.add(path)
                createPathFromPathData(path)?.also {
                    mltOriginPath.add(it)
                }
            }
        }
        return onMeasure()
    }

    fun declareOriginal(
        startX: Int,
        startY: Int,
        width: Int,
        height: Int
    ) {
        mStartX = startX
        mStartY = startY
        mWidth = width
        mOriginWidth = mWidth
        mHeight = height
        mOriginHeight = mHeight
        val drawable: Drawable = this@PathsDrawable
        val bounds = drawable.bounds
        super.setBounds(bounds.left, bounds.top, bounds.left + width, bounds.top + height)
    }

    fun parserColors(vararg colors: Int) {
        for (color in colors) {
            mColors.add(color)
        }
    }

    //<editor-fold desc="Drawable">
    override fun draw(canvas: Canvas) {
        val drawable: Drawable = this@PathsDrawable
        val bounds = drawable.bounds
        val width = bounds.width()
        val height = bounds.height()
        if (mPaint.alpha == 0xFF) {
            canvas.save()
            canvas.translate(bounds.left - mStartX.toFloat(), bounds.top - mStartY.toFloat())
            if (mPaths != null) {
                for (i in mPaths!!.indices) {
                    if (mColors.isNotEmpty() && i < mColors.size) {
                        mPaint.color = mColors[i]
                    }
                    canvas.drawPath(mPaths!![i], mPaint)
                }
                mPaint.alpha = 0xFF
            }
            canvas.restore()
        } else {
            createCachedBitmapIfNeeded(width, height)
            if (mCacheDirty) {
                mCachedBitmap!!.eraseColor(Color.TRANSPARENT)
                val tmpCanvas = Canvas(mCachedBitmap!!)
                drawCachedBitmap(tmpCanvas)
                // Use shallow copy here and shallow comparison in canReuseCache(),
                // likely hit cache miss more, but practically not much difference.
                mCacheDirty = false
            }
            canvas.drawBitmap(mCachedBitmap!!, bounds.left.toFloat(), bounds.top.toFloat(), mPaint)
        }
    }

    //</editor-fold>
    //<editor-fold desc="API">
    fun setGeometricWidth(width: Int) {
        val drawable: Drawable = this@PathsDrawable
        val bounds = drawable.bounds
        val rate = 1f * width / bounds.width()
        setBounds(
            (bounds.left * rate).toInt(),
            (bounds.top * rate).toInt(),
            (bounds.right * rate).toInt(),
            (bounds.bottom * rate).toInt()
        )
    }

    fun setGeometricHeight(height: Int) {
        val drawable: Drawable = this@PathsDrawable
        val bounds = drawable.bounds
        val rate = 1f * height / bounds.height()
        setBounds(
            (bounds.left * rate).toInt(),
            (bounds.top * rate).toInt(),
            (bounds.right * rate).toInt(),
            (bounds.bottom * rate).toInt()
        )
    }

    //</editor-fold>
    //<editor-fold desc="CachedBitmap">
    private var mCachedBitmap: Bitmap? = null
    private var mCacheDirty = false
    private fun drawCachedBitmap(canvas: Canvas) {
        canvas.translate(-mStartX.toFloat(), -mStartY.toFloat())
        if (mPaths != null) {
            for (i in mPaths!!.indices) {
                if (mColors.isNotEmpty() && i < mColors.size) {
                    mPaint.color = mColors[i]
                }
                canvas.drawPath(mPaths!![i], mPaint)
            }
        }
    }

    private fun createCachedBitmapIfNeeded(width: Int, height: Int) {
        if (mCachedBitmap == null || width != mCachedBitmap!!.width || height != mCachedBitmap!!.height) {
            mCachedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mCacheDirty = true
        }
    } //</editor-fold>

    companion object {
        protected val REGION = Region()
        protected val MAX_CLIP = Region(
            Int.MIN_VALUE,
            Int.MIN_VALUE,
            Int.MAX_VALUE,
            Int.MAX_VALUE
        )
    }
}