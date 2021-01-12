package com.townwang.yaohuo.ui.weight.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.util.SmartUtil
import com.townwang.yaohuo.R
import kotlin.math.max

abstract class FunGameView<T>(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : FunGameBase(context, attrs, defStyleAttr) {
    /**
     * 分割线默认宽度大小
     */
    protected var DIVIDING_LINE_SIZE = 1f
    protected var mShadowView: View
    protected var mMaskViewTop: TextView
    protected var mMaskViewBottom: TextView
    var mMaskTextBottom: String?
    var mMaskTextTopPull: String?
    var mMaskTextTopRelease: String?
    protected var mHalfHeaderHeight = 0
    var mTextGameOver: String?
    var mTextLoading: String?
    var mTextLoadingFinish: String?
    var mTextLoadingFailed: String?
    @JvmField
    protected var mPaint: Paint
    protected var mPaintText: Paint
    @JvmField
    protected var controllerPosition: Float
    @JvmField
    protected var controllerSize = 0
    @JvmField
    protected var status = STATUS_GAME_PREPARE
    @JvmField
    protected var lModelColor: Int
    @JvmField
    protected var rModelColor: Int
    @JvmField
    protected var mModelColor: Int
    protected var mBackColor: Int
    protected var mBoundaryColor = -0x9f9fa0
    protected fun createMaskView(
        context: Context?,
        text: String?,
        textSize: Int,
        gravity: Int
    ): TextView {
        val maskView = TextView(context)
        maskView.setTextColor(Color.BLACK)
        maskView.gravity = gravity or Gravity.CENTER_HORIZONTAL
        maskView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        maskView.text = text
        val view: View = maskView
        view.setBackgroundColor(Color.WHITE)
        return maskView
    }

    //<editor-fold desc="绘制方法">
    protected abstract fun drawGame(canvas: Canvas?, width: Int, height: Int)
    override fun dispatchDraw(canvas: Canvas) {
        val thisView: View = this
        val width = thisView.width
        val height = mHeaderHeight
        drawBoundary(canvas, width, height)
        drawText(canvas, width, height)
        drawGame(canvas, width, height)
        super.dispatchDraw(canvas)
    }

    /**
     * 绘制分割线
     * @param canvas 默认画布
     */
    protected fun drawBoundary(canvas: Canvas, width: Int, height: Int) {
        mPaint.color = mBackColor
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
        mPaint.color = mBoundaryColor
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, mPaint)
        canvas.drawLine(
            0f, height - DIVIDING_LINE_SIZE,
            width.toFloat(), height - DIVIDING_LINE_SIZE, mPaint
        )
    }

    /**
     * 绘制文字内容
     * @param canvas 默认画布
     */
    protected fun drawText(canvas: Canvas, width: Int, height: Int) {
        when (status) {
            STATUS_GAME_PREPARE, STATUS_GAME_PLAY -> {
                mPaintText.textSize = SmartUtil.dp2px(25f).toFloat()
                promptText(canvas, mTextLoading, width, height)
            }
            STATUS_GAME_FINISHED -> {
                mPaintText.textSize = SmartUtil.dp2px(20f).toFloat()
                promptText(canvas, mTextLoadingFinish, width, height)
            }
            STATUS_GAME_FAIL -> {
                mPaintText.textSize = SmartUtil.dp2px(20f).toFloat()
                promptText(canvas, mTextLoadingFailed, width, height)
            }
            STATUS_GAME_OVER -> {
                mPaintText.textSize = SmartUtil.dp2px(25f).toFloat()
                promptText(canvas, mTextGameOver, width, height)
            }
        }
    }

    /**
     * 提示文字信息
     * @param canvas 默认画布
     * @param text 相关文字字符串
     */
    protected fun promptText(
        canvas: Canvas,
        text: String?,
        width: Int,
        height: Int
    ) {
        val textX = (width - mPaintText.measureText(text)) * .5f
        val textY = height * .5f - (mPaintText.ascent() + mPaintText.descent()) * .5f
        canvas.drawText(text!!, textX, textY, mPaintText)
    }

    //</editor-fold>
    //<editor-fold desc="控制方法">
    protected abstract fun resetConfigParams()

    /**
     * 更新当前控件状态
     * @param status 状态码
     */
    fun postStatus(status: Int) {
        this.status = status
        if (status == STATUS_GAME_PREPARE) {
            resetConfigParams()
        }
        val thisView: View = this
        thisView.postInvalidate()
    }

    /**
     * 移动控制器（控制器对象为具体控件中的右边图像模型）
     */
    override fun onManualOperationMove(
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        val thisView: View = this
        var distance = Math.max(offset, 0).toFloat()
        val maxDistance = mHeaderHeight - 2 * DIVIDING_LINE_SIZE - controllerSize
        if (distance > maxDistance) {
            distance = maxDistance
        }
        controllerPosition = distance
        thisView.postInvalidate()
    }

    //</editor-fold>
    //<editor-fold desc="生命周期">
    override fun onInitialized(
        kernel: RefreshKernel,
        height: Int,
        maxDragHeight: Int
    ) {
        val thisView: View = this
        if (mHeaderHeight != height && !thisView.isInEditMode) {
            val topView: View = mMaskViewTop
            val bottomView: View = mMaskViewBottom
            mHalfHeaderHeight = (height /* - 2 * DIVIDING_LINE_SIZE*/ * .5f).toInt()
            val lpTop =
                topView.layoutParams as LayoutParams
            val lpBottom =
                bottomView.layoutParams as LayoutParams
            lpBottom.height = mHalfHeaderHeight
            lpTop.height = lpBottom.height
            lpBottom.topMargin = height - mHalfHeaderHeight
            topView.layoutParams = lpTop
            bottomView.layoutParams = lpBottom
        }
        super.onInitialized(kernel, height, maxDragHeight)
        postStatus(STATUS_GAME_PREPARE)
    }

    @Deprecated("")
    override fun setPrimaryColors(@ColorInt vararg colors: Int) {
        super.setPrimaryColors(*colors)
        if (colors.size > 0) {
            mMaskViewTop.setTextColor(colors[0])
            mMaskViewBottom.setTextColor(colors[0])
            mBackColor = colors[0]
            mBoundaryColor = mBackColor
            if (mBackColor == 0 || mBackColor == -0x1) {
                mBoundaryColor = -0x9f9fa0
            }
            if (colors.size > 1) {
                val topView: View = mMaskViewTop
                val bottomView: View = mMaskViewBottom
                val shadowView = mShadowView
                shadowView.setBackgroundColor(colors[1])
                topView.setBackgroundColor(colors[1])
                bottomView.setBackgroundColor(colors[1])
                mModelColor = colors[1]
                lModelColor = ColorUtils.setAlphaComponent(colors[1], 225)
                rModelColor = ColorUtils.setAlphaComponent(colors[1], 200)
                mPaintText.color = ColorUtils.setAlphaComponent(colors[1], 150)
            }
        }
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        super.onStateChanged(refreshLayout, oldState, newState)
        when (newState) {
            RefreshState.PullDownToRefresh -> mMaskViewTop.text = mMaskTextTopPull
            RefreshState.ReleaseToRefresh -> mMaskViewTop.text = mMaskTextTopRelease
            else -> Log.d(FunGameView.toString(),newState.name)
        }
    }

    override fun onStartAnimator(
        refreshLayout: RefreshLayout,
        height: Int,
        maxDragHeight: Int
    ) {
        super.onStartAnimator(refreshLayout, height, maxDragHeight)
        val topView: View = mMaskViewTop
        val shadowView = mShadowView
        val bottomView: View = mMaskViewBottom
        val animatorSet = AnimatorSet()
        animatorSet.play(
            ObjectAnimator.ofFloat(
                topView,
                "translationY",
                topView.translationY,
                -mHalfHeaderHeight.toFloat()
            )
        )
            .with(
                ObjectAnimator.ofFloat(
                    bottomView,
                    "translationY",
                    bottomView.translationY,
                    mHalfHeaderHeight.toFloat()
                )
            )
            .with(ObjectAnimator.ofFloat(shadowView, "alpha", shadowView.alpha, 0f))
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                topView.visibility = View.GONE
                bottomView.visibility = View.GONE
                shadowView.visibility = View.GONE
                postStatus(STATUS_GAME_PLAY)
            }
        })
        animatorSet.duration = 800
        animatorSet.startDelay = 200
        animatorSet.start()
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        if (mManualOperation) {
            postStatus(if (success) STATUS_GAME_FINISHED else STATUS_GAME_FAIL)
        } else {
            postStatus(STATUS_GAME_PREPARE)
            val topView: View = mMaskViewTop
            val bottomView: View = mMaskViewBottom
            val shadowView = mShadowView
            topView.translationY = topView.translationY + mHalfHeaderHeight
            bottomView.translationY = bottomView.translationY - mHalfHeaderHeight
            shadowView.alpha = 1f
            topView.visibility = View.VISIBLE
            bottomView.visibility = View.VISIBLE
            shadowView.visibility = View.VISIBLE
        }
        return super.onFinish(layout, success)
    } //</editor-fold>

    companion object {
        //</editor-fold>
        //<editor-fold desc="Field - Arena">
         const val STATUS_GAME_PREPARE = 0
         const val STATUS_GAME_PLAY = 1
         const val STATUS_GAME_OVER = 2
         const val STATUS_GAME_FINISHED = 3
         const val STATUS_GAME_FAIL = 4
    }

    //</editor-fold>
    init {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.FunGameView)
        mMaskTextBottom = resources
            .getString(R.string.fgh_mask_bottom) //"拖动控制游戏";//"Scroll to move handle";
        mMaskTextTopPull = resources
            .getString(R.string.fgh_mask_top_pull) //"下拉即将展开";//"Pull To Break Out!";
        mMaskTextTopRelease = resources
            .getString(R.string.fgh_mask_top_release) //"放手即将展开";//"Release To Break Out!";
        if (ta.hasValue(R.styleable.FunGameView_srlMaskTextTop)) {
            mMaskTextTopRelease =
                ta.getString(R.styleable.FunGameView_srlMaskTextTop)
            mMaskTextTopPull = mMaskTextTopRelease
        }
        if (ta.hasValue(R.styleable.FunGameView_srlMaskTextTopPull)) {
            mMaskTextTopPull =
                ta.getString(R.styleable.FunGameView_srlMaskTextTopPull)
        }
        if (ta.hasValue(R.styleable.FunGameView_srlMaskTextTopRelease)) {
            mMaskTextTopRelease =
                ta.getString(R.styleable.FunGameView_srlMaskTextTopRelease)
        }
        if (ta.hasValue(R.styleable.FunGameView_srlMaskTextBottom)) {
            mMaskTextBottom =
                ta.getString(R.styleable.FunGameView_srlMaskTextBottom)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextTop)) {
            mMaskTextTopRelease =
                ta.getString(R.styleable.FunGameView_fghMaskTextTop)
            mMaskTextTopPull = mMaskTextTopRelease
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextTopPull)) {
            mMaskTextTopPull =
                ta.getString(R.styleable.FunGameView_fghMaskTextTopPull)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextTopRelease)) {
            mMaskTextTopRelease =
                ta.getString(R.styleable.FunGameView_fghMaskTextTopRelease)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextBottom)) {
            mMaskTextBottom =
                ta.getString(R.styleable.FunGameView_fghMaskTextBottom)
        }
        val metrics = resources.displayMetrics
        var maskTextSizeTop = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            16f,
            metrics
        ).toInt()
        var maskTextSizeBottom = maskTextSizeTop * 14 / 16
        maskTextSizeTop = ta.getDimensionPixelSize(
            R.styleable.FunGameView_srlMaskTextSizeTop,
            maskTextSizeTop
        )
        maskTextSizeBottom = ta.getDimensionPixelSize(
            R.styleable.FunGameView_srlMaskTextSizeBottom,
            maskTextSizeBottom
        )
        maskTextSizeTop = ta.getDimensionPixelSize(
            R.styleable.FunGameView_fghMaskTextSizeTop,
            maskTextSizeTop
        )
        maskTextSizeBottom = ta.getDimensionPixelSize(
            R.styleable.FunGameView_fghMaskTextSizeBottom,
            maskTextSizeBottom
        )
        val curtainLayout: ViewGroup = RelativeLayout(context)
        mShadowView = RelativeLayout(context)
        mShadowView.setBackgroundColor(-0xc5c5c6)
        mMaskViewTop = createMaskView(context, mMaskTextTopPull, maskTextSizeTop, Gravity.BOTTOM)
        mMaskViewBottom = createMaskView(context, mMaskTextBottom, maskTextSizeBottom, Gravity.TOP)
        if (!isInEditMode) {
            val height = SmartUtil.dp2px(100f)
            val maskLp = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
            )
            addView(mShadowView, maskLp)
            addView(curtainLayout, maskLp)
            mHalfHeaderHeight = (height /* - 2 * DIVIDING_LINE_SIZE*/ * .5f).toInt()
            val lpTop = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mHalfHeaderHeight
            )
            val lpBottom = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mHalfHeaderHeight
            )
            lpBottom.topMargin = height - mHalfHeaderHeight
            curtainLayout.addView(mMaskViewTop, lpTop)
            curtainLayout.addView(mMaskViewBottom, lpBottom)
        }
        //</editor-fold>

        //<editor-fold desc="init - Arena">
        DIVIDING_LINE_SIZE = max(1, SmartUtil.dp2px(0.5f)).toFloat()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.strokeWidth = DIVIDING_LINE_SIZE
        controllerPosition = DIVIDING_LINE_SIZE
        mPaintText = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mPaintText.color = -0x3e3d3e
        mTextGameOver = context.getString(R.string.fgh_text_game_over)
        mTextLoading = context.getString(R.string.fgh_text_loading)
        mTextLoadingFinish = context.getString(R.string.fgh_text_loading_finish)
        mTextLoadingFailed = context.getString(R.string.fgh_text_loading_failed)
        mBackColor = ta.getColor(R.styleable.FunGameView_srlBackColor, 0)
        lModelColor =
            ta.getColor(R.styleable.FunGameView_srlLeftColor, Color.BLACK)
        mModelColor =
            ta.getColor(R.styleable.FunGameView_srlMiddleColor, Color.BLACK)
        rModelColor =
            ta.getColor(R.styleable.FunGameView_srlRightColor, -0x5a5a5b)
        if (ta.hasValue(R.styleable.FunGameView_fghBackColor)) {
            mBackColor = ta.getColor(R.styleable.FunGameView_fghBackColor, 0)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghLeftColor)) {
            lModelColor =
                ta.getColor(R.styleable.FunGameView_fghLeftColor, Color.BLACK)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMiddleColor)) {
            mModelColor =
                ta.getColor(R.styleable.FunGameView_fghMiddleColor, Color.BLACK)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghRightColor)) {
            rModelColor =
                ta.getColor(R.styleable.FunGameView_fghRightColor, -0x5a5a5b)
        }
        if (ta.hasValue(R.styleable.FunGameView_srlTextGameOver)) {
            mTextGameOver =
                ta.getString(R.styleable.FunGameView_srlTextGameOver)
        }
        if (ta.hasValue(R.styleable.FunGameView_srlTextLoading)) {
            mTextLoading = ta.getString(R.styleable.FunGameView_srlTextLoading)
        }
        if (ta.hasValue(R.styleable.FunGameView_srlTextLoadingFinished)) {
            mTextLoadingFinish =
                ta.getString(R.styleable.FunGameView_srlTextLoadingFinished)
        }
        if (ta.hasValue(R.styleable.FunGameView_srlTextLoadingFailed)) {
            mTextLoadingFailed =
                ta.getString(R.styleable.FunGameView_srlTextLoadingFailed)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghTextGameOver)) {
            mTextGameOver =
                ta.getString(R.styleable.FunGameView_fghTextGameOver)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghTextLoading)) {
            mTextLoading = ta.getString(R.styleable.FunGameView_fghTextLoading)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghTextLoadingFinished)) {
            mTextLoadingFinish =
                ta.getString(R.styleable.FunGameView_fghTextLoadingFinished)
        }
        if (ta.hasValue(R.styleable.FunGameView_fghTextLoadingFailed)) {
            mTextLoadingFailed =
                ta.getString(R.styleable.FunGameView_fghTextLoadingFailed)
        }
        //</editor-fold>
        ta.recycle()
    }
}