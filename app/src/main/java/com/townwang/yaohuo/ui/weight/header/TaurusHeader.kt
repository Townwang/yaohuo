package com.townwang.yaohuo.ui.weight.header

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import androidx.annotation.ColorInt
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.scwang.smart.refresh.layout.simple.SimpleComponent
import com.scwang.smart.refresh.layout.util.SmartUtil
import com.townwang.yaohuo.R
import java.util.*
import kotlin.math.abs
import kotlin.math.pow


open class TaurusHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SimpleComponent(context, attrs, 0), RefreshHeader {
    private var mAirplane: Drawable
    private var mCloudCenter: Drawable
    private var mMatrix: Matrix
    private var mPercent = 0f
    private var mHeight = 0
    private var mHeaderHeight = 0
    private var mAnimation: Animation
    private var isRefreshing = false
    private var mLoadingAnimationTime = 0f
    private var mLastAnimationTime = 0f
    private var mRandom: Random

    //    protected boolean mEndOfRefreshing;
    //KEY: Y position, Value: X offset of wind
    private var mWinds: MutableMap<Float, Float>
    private var mWindPaint: Paint
    private var mWindLineWidth = 0f
    private var mNewWindSet = false
    private var mInverseDirection = false
    private var mFinishTransformation = 0f
    private var mBackgroundColor = 0
    private var mKernel: RefreshKernel? = null

    protected enum class AnimationPart {
        FIRST, SECOND, THIRD, FOURTH
    }

    //</editor-fold>
    //<editor-fold desc="RefreshHeader">
    override fun onInitialized(
        kernel: RefreshKernel,
        height: Int,
        maxDragHeight: Int
    ) {
        mKernel = kernel
        kernel.requestDrawBackgroundFor(this, mBackgroundColor)
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        mHeight = offset
        mPercent = percent
        mHeaderHeight = height
        if (isDragging) {
            mFinishTransformation = 0f
        }
        this.invalidate()
    }

    override fun onStartAnimator(
        layout: RefreshLayout,
        height: Int,
        maxDragHeight: Int
    ) {
        isRefreshing = true
        mFinishTransformation = 0f
        val thisView: View = this
        thisView.startAnimation(mAnimation)
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        val thisView: View = this
        thisView.clearAnimation()
        return if (success) {
            thisView.startAnimation(object : Animation() {
                override fun applyTransformation(
                    interpolatedTime: Float,
                    t: Transformation
                ) {
                    if (interpolatedTime == 1f) {
                        isRefreshing = false
                    }
                    mFinishTransformation = interpolatedTime
                    thisView.invalidate()
                }

                init {
                    super.setDuration(100)
                    super.setInterpolator(AccelerateInterpolator())
                }
            })
            200
        } else {
            isRefreshing = false
            0
        }
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     */
    @Deprecated("请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}")
    override fun setPrimaryColors(@ColorInt vararg colors: Int) {
//        final View thisView = this;
//        thisView.setBackgroundColor(colors[0]);
        mBackgroundColor = colors[0]
        if (mKernel != null) {
            mKernel!!.requestDrawBackgroundFor(this, mBackgroundColor)
        }
    }

    //</editor-fold>
    //<editor-fold desc="draw">
    override fun dispatchDraw(canvas: Canvas) {
        val thisView: View = this
        val width = thisView.width
        val height = mHeight //thisView.getHeight();
        val footer =
            this == mKernel?.refreshLayout?.refreshFooter?:false
        if (footer) {
            canvas.save()
            canvas.translate(0f, thisView.height - mHeight.toFloat())
        }
        drawWinds(canvas, width)
        drawAirplane(canvas, width, height)
        drawSideClouds(canvas, width, height)
        drawCenterClouds(canvas, width, height)
        if (footer) {
            canvas.restore()
        }
        super.dispatchDraw(canvas)
    }

    private fun drawWinds(canvas: Canvas, width: Int) {
        if (isRefreshing) {
            // Set up new set of wind
            while (mWinds.size < WIND_SET_AMOUNT) {
                var y =
                    (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT)).toFloat()
                val x = random(
                    MIN_WIND_X_OFFSET,
                    MAX_WIND_X_OFFSET
                )

                // Magic with checking interval between winds
                if (mWinds.size > 1) {
                    y = 0f
                    while (y == 0f) {
                        val tmp =
                            (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT)).toFloat()
                        for ((key) in mWinds) {
                            // We want that interval will be greater than fifth part of draggable distance
                            if (abs(key - tmp) > mHeaderHeight / RANDOM_Y_COEFFICIENT) {
                                y = tmp
                            } else {
                                y = 0f
                                break
                            }
                        }
                    }
                }
                mWinds[y] = x
                drawWind(canvas, y, x, width)
            }

            // Draw current set of wind
            if (mWinds.size >= WIND_SET_AMOUNT) {
                for ((key, value) in mWinds) {
                    drawWind(canvas, key, value, width)
                }
            }

            // We should to create new set of winds
            if (mInverseDirection && mNewWindSet) {
                mWinds.clear()
                mNewWindSet = false
                mWindLineWidth = random(
                    MIN_WIND_LINE_WIDTH,
                    MAX_WIND_LINE_WIDTH
                )
            }

            // needed for checking direction
            mLastAnimationTime = mLoadingAnimationTime
        }
    }

    /**
     * Draw wind on loading animation
     *
     * @param canvas  - area where we will draw
     * @param y       - y position fot one of lines
     * @param xOffset - x offset for on of lines
     */
    protected fun drawWind(canvas: Canvas, y: Float, xOffset: Float, width: Int) {
        /* We should multiply current animation time with this coefficient for taking all screen width in time
        Removing slowing of animation with dividing on {@LINK #SLOW_DOWN_ANIMATION_COEFFICIENT}
        And we should don't forget about distance that should "fly" line that depend on screen of device and x offset
        */
        val cof =
            (width + xOffset) / (1f * LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT)
        var time = mLoadingAnimationTime

        // HORRIBLE HACK FOR REVERS ANIMATION THAT SHOULD WORK LIKE RESTART ANIMATION
        if (mLastAnimationTime - mLoadingAnimationTime > 0) {
            mInverseDirection = true
            // take time from 0 to end of animation time
            time =
                1f * LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT - mLoadingAnimationTime
        } else {
            mNewWindSet = true
            mInverseDirection = false
        }

        // Taking current x position of drawing wind
        // For fully disappearing of line we should subtract wind line width
        val x = width - time * cof + xOffset - mWindLineWidth
        val xEnd = x + mWindLineWidth
        canvas.drawLine(x, y, xEnd, y, mWindPaint)
    }

    protected fun drawSideClouds(canvas: Canvas, width: Int, height: Int) {
        val matrix = mMatrix
        matrix.reset()
        val mCloudLeft = mCloudCenter
        val mCloudRight = mCloudCenter

        // Drag percent will newer get more then 1 here
        var dragPercent = 1f.coerceAtMost(abs(mPercent))
        val thisView: View = this
        if (thisView.isInEditMode) {
            dragPercent = 1f
            mHeaderHeight = height
        }
        val scale: Float
        val scalePercentDelta = dragPercent - SCALE_START_PERCENT
        scale = if (scalePercentDelta > 0) {
            val scalePercent =
                scalePercentDelta / (1.0f - SCALE_START_PERCENT)
            SIDE_CLOUDS_INITIAL_SCALE + (SIDE_CLOUDS_FINAL_SCALE - SIDE_CLOUDS_INITIAL_SCALE) * scalePercent
        } else {
            SIDE_CLOUDS_INITIAL_SCALE
        }

        // Current y position of clouds
        val dragYOffset = mHeaderHeight * (1.0f - dragPercent)

        // Position where clouds fully visible on screen and we should drag them with content of listView
//        int cloudsVisiblePosition = mHeaderHeight / 2 - mCloudCenter.height() / 2;

//        boolean needMoveCloudsWithContent = false;
//        if (dragYOffset < cloudsVisiblePosition) {
//            needMoveCloudsWithContent = true;
//        }
        var offsetLeftX = 0 - mCloudLeft.bounds.width() / 2f
        var offsetLeftY = //needMoveCloudsWithContent
            //? mHeaderHeight * dragPercent - mCloudLeftgetBounds().height() :
            dragYOffset
        var offsetRightX = width - mCloudRight.bounds.width() / 2f
        var offsetRightY = //needMoveCloudsWithContent
            //? mHeaderHeight * dragPercent - mCloudRightgetBounds().height() :
            dragYOffset

        // Magic with animation on loading process
        if (isRefreshing) {
            when {
                checkCurrentAnimationPart(AnimationPart.FIRST) -> {
                    offsetLeftX -= 2 * getAnimationPartValue(AnimationPart.FIRST) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += getAnimationPartValue(AnimationPart.FIRST) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                }
                checkCurrentAnimationPart(AnimationPart.SECOND) -> {
                    offsetLeftX -= 2 * getAnimationPartValue(AnimationPart.SECOND) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += getAnimationPartValue(AnimationPart.SECOND) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                }
                checkCurrentAnimationPart(AnimationPart.THIRD) -> {
                    offsetLeftX -= getAnimationPartValue(AnimationPart.THIRD) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += 2 * getAnimationPartValue(AnimationPart.THIRD) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                }
                checkCurrentAnimationPart(AnimationPart.FOURTH) -> {
                    offsetLeftX -= getAnimationPartValue(AnimationPart.FOURTH) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += 2 * getAnimationPartValue(AnimationPart.FOURTH) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                }
            }
        }
        if (offsetLeftY + scale * mCloudLeft.bounds.height() < height + 2) {
            offsetLeftY = height + 2 - scale * mCloudLeft.bounds.height()
        }
        if (offsetRightY + scale * mCloudRight.bounds.height() < height + 2) {
            offsetRightY = height + 2 - scale * mCloudRight.bounds.height()
        }
        val saveCount = canvas.saveCount
        canvas.save()
        canvas.translate(offsetLeftX, offsetLeftY)
        matrix.postScale(
            scale,
            scale,
            mCloudLeft.bounds.width() * 3 / 4f,
            mCloudLeft.bounds.height().toFloat()
        )
        canvas.concat(matrix)
        mCloudLeft.alpha = 100
        mCloudLeft.draw(canvas)
        mCloudLeft.alpha = 255
        canvas.restoreToCount(saveCount)
        canvas.save()
        canvas.translate(offsetRightX, offsetRightY)
        matrix.postScale(scale, scale, 0f, mCloudRight.bounds.height().toFloat())
        canvas.concat(matrix)
        mCloudRight.alpha = 100
        mCloudRight.draw(canvas)
        mCloudRight.alpha = 255
        canvas.restoreToCount(saveCount)
    }

    protected fun drawCenterClouds(canvas: Canvas, width: Int, height: Int) {
        val matrix = mMatrix
        matrix.reset()
        var dragPercent = 1f.coerceAtMost(abs(mPercent))
        val thisView: View = this
        if (thisView.isInEditMode) {
            dragPercent = 1f
            mHeaderHeight = height
        }
        val scale: Float
        var overDragPercent = 0f
        var overDrag = false
        if (mPercent > 1.0f) {
            overDrag = true
            // Here we want know about how mach percent of over drag we done
            overDragPercent = abs(1.0f - mPercent)
        }
        val scalePercentDelta = dragPercent - SCALE_START_PERCENT
        scale = if (scalePercentDelta > 0) {
            val scalePercent =
                scalePercentDelta / (1.0f - SCALE_START_PERCENT)
            CENTER_CLOUDS_INITIAL_SCALE + (CENTER_CLOUDS_FINAL_SCALE - CENTER_CLOUDS_INITIAL_SCALE) * scalePercent
        } else {
            CENTER_CLOUDS_INITIAL_SCALE
        }
        var parallaxPercent = 0f
        var parallax = false
        // Current y position of clouds
        val dragYOffset = mHeaderHeight * dragPercent
        // Position when should start parallax scrolling
        val startParallaxHeight = mHeaderHeight - mCloudCenter.bounds.height() / 2
        if (dragYOffset > startParallaxHeight) {
            parallax = true
            parallaxPercent = dragYOffset - startParallaxHeight
        }
        val offsetX = width / 2f - mCloudCenter.bounds.width() / 2f
        var offsetY = (dragYOffset
                - if (parallax) mCloudCenter.bounds
            .height() / 2f + parallaxPercent else mCloudCenter.bounds.height() / 2f)
        var sx = if (overDrag) scale + overDragPercent / 4 else scale
        var sy = if (overDrag) scale + overDragPercent / 2 else scale
        if (isRefreshing && !overDrag) {
            when {
                checkCurrentAnimationPart(AnimationPart.FIRST) -> {
                    sx =
                        scale - getAnimationPartValue(AnimationPart.FIRST) / LOADING_ANIMATION_COEFFICIENT / 8
                }
                checkCurrentAnimationPart(AnimationPart.SECOND) -> {
                    sx =
                        scale - getAnimationPartValue(AnimationPart.SECOND) / LOADING_ANIMATION_COEFFICIENT / 8
                }
                checkCurrentAnimationPart(AnimationPart.THIRD) -> {
                    sx =
                        scale + getAnimationPartValue(AnimationPart.THIRD) / LOADING_ANIMATION_COEFFICIENT / 6
                }
                checkCurrentAnimationPart(AnimationPart.FOURTH) -> {
                    sx =
                        scale + getAnimationPartValue(AnimationPart.FOURTH) / LOADING_ANIMATION_COEFFICIENT / 6
                }
            }
            sy = sx
        }
        matrix.postScale(sx, sy, mCloudCenter.bounds.width() / 2f, 0f)
        if (offsetY + sy * mCloudCenter.bounds.height() < height + 2) {
            offsetY = height + 2 - sy * mCloudCenter.bounds.height()
        }
        val saveCount = canvas.saveCount
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.concat(matrix)
        mCloudCenter.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    protected fun drawAirplane(canvas: Canvas, width: Int, height: Int) {
        val matrix = mMatrix
        matrix.reset()
        var dragPercent = mPercent
        var rotateAngle = 0f
        val thisView: View = this
        if (thisView.isInEditMode) {
            dragPercent = 1f
            mHeaderHeight = height
        }

        // Check overDrag
        if (dragPercent > 1.0f) {
            rotateAngle =
                20 * (1 - 100.0.pow(-(dragPercent - 1) / 2.toDouble())).toFloat()
            dragPercent = 1.0f
        }
        var offsetX = width * dragPercent / 2 - mAirplane.bounds.width() / 2f
        var offsetY =
            mHeaderHeight * (1 - dragPercent / 2) - mAirplane.bounds.height() / 2f
        if (mFinishTransformation > 0) {
            offsetY += (0 - offsetY) * mFinishTransformation
            offsetX += (width + mAirplane.bounds.width() - offsetX) * mFinishTransformation
        }
        if (isRefreshing) {
            when {
                checkCurrentAnimationPart(AnimationPart.FIRST) -> {
                    offsetY -= getAnimationPartValue(AnimationPart.FIRST)
                }
                checkCurrentAnimationPart(AnimationPart.SECOND) -> {
                    offsetY -= getAnimationPartValue(AnimationPart.SECOND)
                }
                checkCurrentAnimationPart(AnimationPart.THIRD) -> {
                    offsetY += getAnimationPartValue(AnimationPart.THIRD)
                }
                checkCurrentAnimationPart(AnimationPart.FOURTH) -> {
                    offsetY += getAnimationPartValue(AnimationPart.FOURTH)
                }
            }
        }
        if (rotateAngle > 0) {
            matrix.postRotate(
                rotateAngle,
                mAirplane.bounds.width() / 2f,
                mAirplane.bounds.height() / 2f
            )
        }
        val saveCount = canvas.saveCount
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.concat(matrix)
        mAirplane.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    //</editor-fold>
    //<editor-fold desc="protected">
    protected fun random(min: Int, max: Int): Float {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return (mRandom.nextInt(max - min + 1) + min).toFloat()
    }

    /**
     * We need a special value for different part of animation
     *
     * @param part - needed part
     * @return - value for needed part
     */
    protected fun getAnimationPartValue(part: AnimationPart?): Float {
        return when (part) {
            AnimationPart.FIRST -> {
                mLoadingAnimationTime
            }
            AnimationPart.SECOND -> {
                getAnimationTimePart(AnimationPart.FOURTH) - (mLoadingAnimationTime - getAnimationTimePart(
                    AnimationPart.FOURTH
                ))
            }
            AnimationPart.THIRD -> {
                mLoadingAnimationTime - getAnimationTimePart(AnimationPart.SECOND)
            }
            AnimationPart.FOURTH -> {
                getAnimationTimePart(AnimationPart.THIRD) - (mLoadingAnimationTime - getAnimationTimePart(
                    AnimationPart.FOURTH
                ))
            }
            else -> 0f
        }
    }

    /**
     * On drawing we should check current part of animation
     *
     * @param part - needed part of animation
     * @return - return true if current part
     */
    protected fun checkCurrentAnimationPart(part: AnimationPart?): Boolean {
        return when (part) {
            AnimationPart.FIRST -> {
                mLoadingAnimationTime < getAnimationTimePart(AnimationPart.FOURTH)
            }
            AnimationPart.SECOND, AnimationPart.THIRD -> {
                mLoadingAnimationTime < getAnimationTimePart(part)
            }
            AnimationPart.FOURTH -> {
                mLoadingAnimationTime > getAnimationTimePart(AnimationPart.THIRD)
            }
            else -> false
        }
    }

    /**
     * Get part of animation duration
     *
     * @param part - needed part of time
     * @return - interval of time
     */
    protected fun getAnimationTimePart(part: AnimationPart?): Int {
        return when (part) {
            AnimationPart.SECOND -> {
                LOADING_ANIMATION_COEFFICIENT / 2
            }
            AnimationPart.THIRD -> {
                getAnimationTimePart(AnimationPart.FOURTH) * 3
            }
            AnimationPart.FOURTH -> {
                LOADING_ANIMATION_COEFFICIENT / 4
            }
            else -> 0
        }
    } //</editor-fold>

    companion object {
        //<editor-fold desc="static">
         var airplanePaths = arrayOf(
            "m23.01,81.48c-0.21,-0.3 -0.38,-0.83 -0.38,-1.19 0,-0.55 0.24,-0.78 1.5,-1.48 1.78,-0.97 2.62,-1.94 2.24,-2.57 -0.57,-0.93 -1.97,-1.24 -11.64,-2.59 -5.35,-0.74 -10.21,-1.44 -10.82,-1.54l-1.09,-0.18 1.19,-0.91c0.99,-0.76 1.38,-0.91 2.35,-0.91 0.64,0 6.39,0.33 12.79,0.74 6.39,0.41 12.09,0.71 12.65,0.67l1.03,-0.07 -1.24,-2.19C30.18,66.77 15.91,42 15.13,40.68l-0.51,-0.87 4.19,-1.26c2.3,-0.69 4.27,-1.26 4.37,-1.26 0.1,0 5.95,3.85 13,8.55 14.69,9.81 17.1,11.31 19.7,12.31 4.63,1.78 6.45,1.69 12.94,-0.64 13.18,-4.73 25.22,-9.13 25.75,-9.4 0.69,-0.36 3.6,1.33 -24.38,-14.22L50.73,23.07 46.74,16.42 42.75,9.77 43.63,8.89c0.83,-0.83 0.91,-0.86 1.46,-0.52 0.32,0.2 3.72,3.09 7.55,6.44 3.83,3.34 7.21,6.16 7.5,6.27 0.29,0.11 13.6,2.82 29.58,6.03 15.98,3.21 31.86,6.4 35.3,7.1l6.26,1.26 3.22,-1.13c41.63,-14.63 67.88,-23.23 85.38,-28 14.83,-4.04 23.75,-4.75 32.07,-2.57 7.04,1.84 9.87,4.88 7.71,8.27 -1.6,2.5 -4.6,4.63 -10.61,7.54 -5.94,2.88 -10.22,4.46 -25.4,9.41 -8.15,2.66 -16.66,5.72 -39.01,14.02 -66.79,24.82 -88.49,31.25 -121.66,36.07 -14.56,2.11 -24.17,2.95 -34.08,2.95 -5.43,0 -5.52,-0.01 -5.89,-0.54z"
        )
         var airplaneColors = intArrayOf(
            -0x1
        )
         var cloudPaths = arrayOf(
            "M551.81,1.01A65.42,65.42 0,0 0,504.38 21.5A50.65,50.65 0,0 0,492.4 20A50.65,50.65 0,0 0,441.75 70.65A50.65,50.65 0,0 0,492.4 121.3A50.65,50.65 0,0 0,511.22 117.64A65.42,65.42 0,0 0,517.45 122L586.25,122A65.42,65.42 0,0 0,599.79 110.78A59.79,59.79 0,0 0,607.81 122L696.34,122A59.79,59.79 0,0 0,711.87 81.9A59.79,59.79 0,0 0,652.07 22.11A59.79,59.79 0,0 0,610.93 38.57A65.42,65.42 0,0 0,551.81 1.01zM246.2,1.71A54.87,54.87 0,0 0,195.14 36.64A46.78,46.78 0,0 0,167.77 27.74A46.78,46.78 0,0 0,120.99 74.52A46.78,46.78 0,0 0,167.77 121.3A46.78,46.78 0,0 0,208.92 96.74A54.87,54.87 0,0 0,246.2 111.45A54.87,54.87 0,0 0,268.71 106.54A39.04,39.04 0,0 0,281.09 122L327.6,122A39.04,39.04 0,0 0,343.38 90.7A39.04,39.04 0,0 0,304.34 51.66A39.04,39.04 0,0 0,300.82 51.85A54.87,54.87 0,0 0,246.2 1.71z",
            "m506.71,31.37a53.11,53.11 0,0 0,-53.11 53.11,53.11 53.11,0 0,0 15.55,37.5h75.12a53.11,53.11 0,0 0,1.88 -2.01,28.49 28.49,0 0,0 0.81,2.01h212.96a96.72,96.72 0,0 0,-87.09 -54.85,96.72 96.72,0 0,0 -73.14,33.52 28.49,28.49 0,0 0,-26.74 -18.74,28.49 28.49,0 0,0 -13.16,3.23 53.11,53.11 0,0 0,0.03 -0.66,53.11 53.11,0 0,0 -53.11,-53.11zM206.23,31.81a53.81,53.81 0,0 0,-49.99 34.03,74.91 74.91,0 0,0 -47.45,-17 74.91,74.91 0,0 0,-73.54 60.82,31.3 31.3,0 0,0 -10.17,-1.73 31.3,31.3 0,0 0,-26.09 14.05L300.86,121.98a37.63,37.63 0,0 0,0.2 -3.85,37.63 37.63,0 0,0 -37.63,-37.63 37.63,37.63 0,0 0,-3.65 0.21,53.81 53.81,0 0,0 -53.54,-48.9z",
            "m424.05,36.88a53.46,53.46 0,0 0,-40.89 19.02,53.46 53.46,0 0,0 -1.34,1.76 62.6,62.6 0,0 0,-5.39 -0.27,62.6 62.6,0 0,0 -61.36,50.17 62.6,62.6 0,0 0,-0.53 3.51,15.83 15.83,0 0,0 -10.33,-3.84 15.83,15.83 0,0 0,-8.06 2.23,21.1 21.1,0 0,0 -18.31,-10.67 21.1,21.1 0,0 0,-19.47 12.97,21.81 21.81,0 0,0 -6.56,-1.01 21.81,21.81 0,0 0,-19.09 11.32L522.84,122.07a43.61,43.61 0,0 0,-43.11 -37.35,43.61 43.61,0 0,0 -2.57,0.09 53.46,53.46 0,0 0,-53.11 -47.93zM129.08,38.4a50.29,50.29 0,0 0,-50.29 50.29,50.29 50.29,0 0,0 2.37,15.06 15.48,15.83 0,0 0,-5.87 1.68,15.48 15.83,0 0,0 -0.98,0.58 16.53,16.18 0,0 0,-0.19 -0.21,16.53 16.18,0 0,0 -11.86,-4.91 16.53,16.18 0,0 0,-16.38 14.13,20.05 16.18,0 0,0 -14.97,7.04L223.95,122.07a42.56,42.56 0,0 0,1.14 -9.56,42.56 42.56,0 0,0 -42.56,-42.56 42.56,42.56 0,0 0,-6.58 0.54,50.29 50.29,0 0,0 -0,-0.01 50.29,50.29 0,0 0,-46.88 -32.07zM631.67,82.61a64.01,64.01 0,0 0,-44.9 18.42,26.73 26.73,0 0,0 -10.67,-2.24 26.73,26.73 0,0 0,-22.72 12.71,16.88 16.88,0 0,0 -0.25,-0.12 16.88,16.88 0,0 0,-6.57 -1.33,16.88 16.88,0 0,0 -16.15,12.03h160.36a64.01,64.01 0,0 0,-59.1 -39.46z"
        )
         var cloudColors = intArrayOf(
            -0x5538230f, -0x22170c03, -0x20203
        )

        //</editor-fold>
        //<editor-fold desc="Field">
        protected  const val SCALE_START_PERCENT = 0.5f
        protected const val ANIMATION_DURATION = 1000
        protected const val SIDE_CLOUDS_INITIAL_SCALE = 0.6f //1.05f;
        protected const val SIDE_CLOUDS_FINAL_SCALE = 1f //1.55f;
        protected const val CENTER_CLOUDS_INITIAL_SCALE = 0.8f //0.8f;
        protected const val CENTER_CLOUDS_FINAL_SCALE = 1f //1.30f;
        protected val ACCELERATE_DECELERATE_INTERPOLATOR: Interpolator =
            AccelerateDecelerateInterpolator()

        // Multiply with this animation interpolator time
        protected const val LOADING_ANIMATION_COEFFICIENT = 80
        protected const val SLOW_DOWN_ANIMATION_COEFFICIENT = 6

        // Amount of lines when is going lading animation
        protected const val WIND_SET_AMOUNT = 10
        protected const val Y_SIDE_CLOUDS_SLOW_DOWN_COF = 4
        protected const val X_SIDE_CLOUDS_SLOW_DOWN_COF = 2
        protected const val MIN_WIND_LINE_WIDTH = 50
        protected const val MAX_WIND_LINE_WIDTH = 300
        protected const val MIN_WIND_X_OFFSET = 1000
        protected const val MAX_WIND_X_OFFSET = 2000
        protected const val RANDOM_Y_COEFFICIENT = 5
    }

    //</editor-fold>
    //<editor-fold desc="View">
    init {
        val thisView: View = this
        thisView.minimumHeight = SmartUtil.dp2px(100f)
        mMatrix = Matrix()
        mWinds = HashMap()
        mRandom = Random()
        mWindPaint = Paint()
        mWindPaint.color = -0x1
        mWindPaint.strokeWidth = SmartUtil.dp2px(3f).toFloat()
        mWindPaint.alpha = 50
        mSpinnerStyle = SpinnerStyle.FixedBehind

        //<editor-fold desc="setupAnimations">
        mAnimation = object : Animation() {
            public override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation
            ) {
                /*SLOW DOWN ANIMATION IN {@link #SLOW_DOWN_ANIMATION_COEFFICIENT} time */
                mLoadingAnimationTime =
                    LOADING_ANIMATION_COEFFICIENT * (interpolatedTime / SLOW_DOWN_ANIMATION_COEFFICIENT)
                thisView.invalidate()
            }
        }
        mAnimation.repeatCount = Animation.INFINITE
        mAnimation.repeatMode = Animation.REVERSE
        mAnimation.interpolator = ACCELERATE_DECELERATE_INTERPOLATOR
        mAnimation.duration = ANIMATION_DURATION.toLong()
        //</editor-fold>

        //<editor-fold desc="setupPathDrawable">
        val airplane = PathsDrawable()
        if (!airplane.parserPaths(*airplanePaths)) {
            airplane.declareOriginal(3, 3, 257, 79)
        }
        //        airplane.printOriginal("airplane");
        airplane.parserColors(*airplaneColors)
        val cloudCenter = PathsDrawable()
        if (!cloudCenter.parserPaths(*cloudPaths)) {
            cloudCenter.declareOriginal(-1, 1, 761, 121)
        }
        //        cloudCenter.printOriginal("cloudCenter");
        cloudCenter.parserColors(*cloudColors)
        mAirplane = airplane
        mCloudCenter = cloudCenter
        mAirplane.setBounds(0, 0, SmartUtil.dp2px(65f), SmartUtil.dp2px(20f))
        mCloudCenter.setBounds(0, 0, SmartUtil.dp2px(260f), SmartUtil.dp2px(45f))
        //</editor-fold>
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.TaurusHeader)
        val primaryColor =
            ta.getColor(R.styleable.TaurusHeader_thPrimaryColor, 0)
        mBackgroundColor = if (primaryColor != 0) {
            primaryColor
            //            thisView.setBackgroundColor(primaryColor);
        } else {
            -0xee4401
            //            thisView.setBackgroundColor(0xff11bbff);
        }
        ta.recycle()
    }
}