package com.townwang.yaohuo.ui.weight.button

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.*
import android.graphics.drawable.ShapeDrawable.ShaderFactory
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatImageButton
import com.townwang.yaohuo.R

open class FloatingActionButton : AppCompatImageButton {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        SIZE_NORMAL,
        SIZE_MINI
    )
    annotation class FAB_SIZE

    @JvmField
    var mColorNormal = 0
    @JvmField
    var mColorPressed = 0
    var mColorDisabled = 0
    var mTitle: String? = null

    @DrawableRes
    private var mIcon = 0
    private var mIconDrawable: Drawable? = null
    private var mSize = 0
    private var mCircleSize = 0f
    private var mShadowRadius = 0f
    private var mShadowOffset = 0f
    private var mDrawableSize = 0
    @JvmField
    var mStrokeVisible = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(
        context,
        attrs
    ) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    open fun init(
        context: Context,
        attributeSet: AttributeSet?
    ) {
        val attr = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.FloatingActionButton,
            0,
            0
        )
        mColorNormal = attr.getColor(
            R.styleable.FloatingActionButton_fab_colorNormal,
            getColor(android.R.color.holo_blue_dark)
        )
        mColorPressed = attr.getColor(
            R.styleable.FloatingActionButton_fab_colorPressed,
            getColor(android.R.color.holo_blue_light)
        )
        mColorDisabled = attr.getColor(
            R.styleable.FloatingActionButton_fab_colorDisabled,
            getColor(android.R.color.darker_gray)
        )
        mSize = attr.getInt(
            R.styleable.FloatingActionButton_fab_size,
            SIZE_NORMAL
        )
        mIcon = attr.getResourceId(R.styleable.FloatingActionButton_fab_icon, 0)
        mTitle = attr.getString(R.styleable.FloatingActionButton_fab_title)
        mStrokeVisible = attr.getBoolean(
            R.styleable.FloatingActionButton_fab_stroke_visible,
            true
        )
        attr.recycle()
        updateCircleSize()
        mShadowRadius = getDimension(R.dimen.fab_shadow_radius)
        mShadowOffset = getDimension(R.dimen.fab_shadow_offset)
        updateDrawableSize()
        updateBackground()
    }

    private fun updateDrawableSize() {
        mDrawableSize = (mCircleSize + 2 * mShadowRadius).toInt()
    }

    private fun updateCircleSize() {
        mCircleSize =
            getDimension(if (mSize == SIZE_NORMAL) R.dimen.fab_size_normal else R.dimen.fab_size_mini)
    }

    @get:FAB_SIZE
    var size: Int
        get() = mSize
        set(size) {
            require(!(size != SIZE_MINI && size != SIZE_NORMAL)) { "Use @FAB_SIZE constants only!" }
            if (mSize != size) {
                mSize = size
                updateCircleSize()
                updateDrawableSize()
                updateBackground()
            }
        }

    open fun setIcon(@DrawableRes icon: Int) {
        if (mIcon != icon) {
            mIcon = icon
            mIconDrawable = null
            updateBackground()
        }
    }

    /**
     * @return the current Color for normal state.
     */
    var colorNormal: Int
        get() = mColorNormal
        set(color) {
            if (mColorNormal != color) {
                mColorNormal = color
                updateBackground()
            }
        }

    /**
     * @return the current color for pressed state.
     */
    var colorPressed: Int
        get() = mColorPressed
        set(color) {
            if (mColorPressed != color) {
                mColorPressed = color
                updateBackground()
            }
        }



    /**
     * @return the current color for disabled state.
     */
    var colorDisabled: Int
        get() = mColorDisabled
        set(color) {
            if (mColorDisabled != color) {
                mColorDisabled = color
                updateBackground()
            }
        }
    var isStrokeVisible: Boolean
        get() = mStrokeVisible
        set(visible) {
            if (mStrokeVisible != visible) {
                mStrokeVisible = visible
                updateBackground()
            }
        }

    fun getColor(@ColorRes id: Int): Int {
        return resources.getColor(id)
    }

    fun getDimension(@DimenRes id: Int): Float {
        return resources.getDimension(id)
    }

    val labelView: TextView
        get() = getTag(R.id.fab_label) as TextView

    var title: String?
        get() = mTitle
        set(title) {
            mTitle = title
            val label = labelView
            label.text = title
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mDrawableSize, mDrawableSize)
    }

    open fun updateBackground() {
        val strokeWidth = getDimension(R.dimen.fab_stroke_width)
        val halfStrokeWidth = strokeWidth / 2f
        @SuppressLint("UseCompatLoadingForDrawables") val layerDrawable =
            LayerDrawable(
                arrayOf(
                    resources.getDrawable(if (mSize == SIZE_NORMAL) R.drawable.t_bg else R.drawable.t_bg),
                    createFillDrawable(strokeWidth),
                    createOuterStrokeDrawable(strokeWidth),
                    iconDrawable
                )
            )
        val iconOffset =
            (mCircleSize - getDimension(R.dimen.fab_icon_size)).toInt() / 2
        val circleInsetHorizontal = mShadowRadius.toInt()
        val circleInsetTop = (mShadowRadius - mShadowOffset).toInt()
        val circleInsetBottom = (mShadowRadius + mShadowOffset).toInt()
        layerDrawable.setLayerInset(
            1,
            circleInsetHorizontal,
            circleInsetTop,
            circleInsetHorizontal,
            circleInsetBottom
        )
        layerDrawable.setLayerInset(
            2,
            (circleInsetHorizontal - halfStrokeWidth).toInt(),
            (circleInsetTop - halfStrokeWidth).toInt(),
            (circleInsetHorizontal - halfStrokeWidth).toInt(),
            (circleInsetBottom - halfStrokeWidth).toInt()
        )
        layerDrawable.setLayerInset(
            3,
            circleInsetHorizontal + iconOffset,
            circleInsetTop + iconOffset,
            circleInsetHorizontal + iconOffset,
            circleInsetBottom + iconOffset
        )
        setBackgroundCompat(layerDrawable)
    }

    open var iconDrawable: Drawable
        @SuppressLint("UseCompatLoadingForDrawables")
        get() = mIconDrawable
            ?: if (mIcon != 0) {
                resources.getDrawable(mIcon)
            } else {
                ColorDrawable(Color.TRANSPARENT)
            }
        set(iconDrawable) {
            if (mIconDrawable !== iconDrawable) {
                mIcon = 0
                mIconDrawable = iconDrawable
                updateBackground()
            }
        }

    private fun createFillDrawable(strokeWidth: Float): StateListDrawable {
        val drawable =
            StateListDrawable()
        drawable.addState(
            intArrayOf(-android.R.attr.state_enabled),
            createCircleDrawable(mColorDisabled, strokeWidth)
        )
        drawable.addState(
            intArrayOf(android.R.attr.state_pressed),
            createCircleDrawable(mColorPressed, strokeWidth)
        )
        drawable.addState(intArrayOf(), createCircleDrawable(mColorNormal, strokeWidth))
        return drawable
    }

    private fun createCircleDrawable(color: Int, strokeWidth: Float): Drawable {
        val alpha = Color.alpha(color)
        val opaqueColor = opaque(color)
        val fillDrawable = ShapeDrawable(OvalShape())
        val paint = fillDrawable.paint
        paint.isAntiAlias = true
        paint.color = opaqueColor
        val layers = arrayOf(
            fillDrawable,
            createInnerStrokesDrawable(opaqueColor, strokeWidth)
        )
        val drawable =
            if (alpha == 255 || !mStrokeVisible) LayerDrawable(layers) else TranslucentLayerDrawable(
                alpha,
                *layers
            )
        val halfStrokeWidth = (strokeWidth / 2f).toInt()
        drawable.setLayerInset(
            1,
            halfStrokeWidth,
            halfStrokeWidth,
            halfStrokeWidth,
            halfStrokeWidth
        )
        return drawable
    }

    private class TranslucentLayerDrawable(private val mAlpha: Int, vararg layers: Drawable?) :
        LayerDrawable(layers) {
        override fun draw(canvas: Canvas) {
            val bounds = bounds
            canvas.saveLayerAlpha(
                bounds.left.toFloat(),
                bounds.top.toFloat(),
                bounds.right.toFloat(),
                bounds.bottom.toFloat(),
                mAlpha,
                Canvas.ALL_SAVE_FLAG
            )
            super.draw(canvas)
            canvas.restore()
        }

    }

    private fun createOuterStrokeDrawable(strokeWidth: Float): Drawable {
        val shapeDrawable = ShapeDrawable(OvalShape())
        val paint = shapeDrawable.paint
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.alpha = opacityToAlpha(0.02f)
        return shapeDrawable
    }

    private fun opacityToAlpha(opacity: Float): Int {
        return (255f * opacity).toInt()
    }

    private fun darkenColor(argb: Int): Int {
        return adjustColorBrightness(argb, 0.9f)
    }

    private fun lightenColor(argb: Int): Int {
        return adjustColorBrightness(argb, 1.1f)
    }

    private fun adjustColorBrightness(argb: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(argb, hsv)
        hsv[2] = Math.min(hsv[2] * factor, 1f)
        return Color.HSVToColor(Color.alpha(argb), hsv)
    }

    private fun halfTransparent(argb: Int): Int {
        return Color.argb(
            Color.alpha(argb) / 2,
            Color.red(argb),
            Color.green(argb),
            Color.blue(argb)
        )
    }

    private fun opaque(argb: Int): Int {
        return Color.rgb(
            Color.red(argb),
            Color.green(argb),
            Color.blue(argb)
        )
    }

    private fun createInnerStrokesDrawable(color: Int, strokeWidth: Float): Drawable {
        if (!mStrokeVisible) {
            return ColorDrawable(Color.TRANSPARENT)
        }
        val shapeDrawable = ShapeDrawable(OvalShape())
        val bottomStrokeColor = darkenColor(color)
        val bottomStrokeColorHalfTransparent = halfTransparent(bottomStrokeColor)
        val topStrokeColor = lightenColor(color)
        val topStrokeColorHalfTransparent = halfTransparent(topStrokeColor)
        val paint = shapeDrawable.paint
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        shapeDrawable.shaderFactory = object : ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                return LinearGradient(
                    width / 2f,
                    0f,
                    width / 2f,
                    height.toFloat(),
                    intArrayOf(
                        topStrokeColor,
                        topStrokeColorHalfTransparent,
                        color,
                        bottomStrokeColorHalfTransparent,
                        bottomStrokeColor
                    ),
                    floatArrayOf(0f, 0.2f, 0.5f, 0.8f, 1f),
                    TileMode.CLAMP
                )
            }
        }
        return shapeDrawable
    }
    private fun setBackgroundCompat(drawable: Drawable) {
        background = drawable
    }

    override fun setVisibility(visibility: Int) {
        val label = labelView
        label.visibility = visibility
        super.setVisibility(visibility)
    }

    companion object {
        const val SIZE_NORMAL = 0
        const val SIZE_MINI = 1
    }
}