package com.townwang.yaohuo.ui.weight.game

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.scwang.smart.refresh.layout.api.RefreshContent
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.scwang.smart.refresh.layout.simple.SimpleComponent
import com.scwang.smart.refresh.layout.util.SmartUtil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * 游戏 header
 */
@SuppressLint("RestrictedApi")
abstract class FunGameBase(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : SimpleComponent(context, attrs, defStyleAttr), RefreshHeader {
    protected var mOffset = 0
    @JvmField
    protected var mHeaderHeight = 0
    protected var mScreenHeightPixels: Int
    protected var mTouchY = 0f
    protected var mIsFinish = false
    protected var mLastFinish = false
    @JvmField
    protected var mManualOperation = false
    protected var mState: RefreshState? = null
    protected var mRefreshKernel: RefreshKernel? = null
    protected var mRefreshContent: RefreshContent? = null


    init {
        minimumHeight = SmartUtil.dp2px(100f)
        mScreenHeightPixels = resources.displayMetrics.heightPixels
        mSpinnerStyle = SpinnerStyle.MatchLayout
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mState == RefreshState.Refreshing || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mState == RefreshState.Refreshing || mState == RefreshState.RefreshFinish) {
            if (!mManualOperation) {
                onManualOperationStart()
            }
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchY = event.rawY
                    mRefreshKernel!!.moveSpinner(0, true)
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = event.rawY - mTouchY
                    if (dy >= 0) {
                        val M = mHeaderHeight * 2.toDouble()
                        val H = mScreenHeightPixels * 2 / 3f.toDouble()
                        val x = max(0.0, dy * 0.5)
                        val y = min(
                            M * (1 - 100.0.pow(-x / H)),
                            x
                        ) // 公式 y = M(1-40^(-x/H))
                        mRefreshKernel!!.moveSpinner(max(1, y.toInt()), false)
                    } else {
                        mRefreshKernel!!.moveSpinner(1, false)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    onManualOperationRelease()
                    mTouchY = -1f
                    if (mIsFinish) {
                        mRefreshKernel!!.moveSpinner(mHeaderHeight, true)
                    }
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun onManualOperationStart() {
        if (!mManualOperation) {
            mManualOperation = true
            mRefreshContent = mRefreshKernel!!.refreshContent
            val contentView = mRefreshContent!!.view
            val params = contentView.layoutParams as MarginLayoutParams
            params.topMargin += mHeaderHeight
            contentView.layoutParams = params
        }
    }

    protected abstract fun onManualOperationMove(
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    )

    private fun onManualOperationRelease() {
        if (mIsFinish) {
            mManualOperation = false
            if (mTouchY != -1f) { //还没松手
                onFinish(mRefreshKernel!!.refreshLayout, mLastFinish)
                mRefreshKernel!!.setState(RefreshState.RefreshFinish)
                mRefreshKernel!!.animSpinner(0)
            } else {
                mRefreshKernel!!.moveSpinner(mHeaderHeight, true)
            }
            val contentView = mRefreshContent!!.view
            val params = contentView.layoutParams as MarginLayoutParams
            params.topMargin -= mHeaderHeight
            contentView.layoutParams = params
        } else {
            mRefreshKernel!!.moveSpinner(0, true)
        }
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        if (mManualOperation) onManualOperationMove(percent, offset, height, maxDragHeight) else {
            mOffset = offset
            val thisView: View = this
            thisView.translationY = mOffset - mHeaderHeight.toFloat()
        }
    }

    override fun onStartAnimator(
        refreshLayout: RefreshLayout,
        height: Int,
        maxDragHeight: Int
    ) {
        mIsFinish = false
        val thisView: View = this
        thisView.translationY = 0f
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        mState = newState
    }

    override fun onInitialized(
        kernel: RefreshKernel,
        height: Int,
        maxDragHeight: Int
    ) {
        mRefreshKernel = kernel
        mHeaderHeight = height
        val thisView: View = this
        if (!thisView.isInEditMode) {
            thisView.translationY = mOffset - mHeaderHeight.toFloat()
            kernel.requestNeedTouchEventFor(this, true)
            //            kernel.requestNeedTouchEventWhenRefreshing(true);
        }
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        mLastFinish = success
        if (!mIsFinish) {
            mIsFinish = true
            if (mManualOperation) {
                if (mTouchY == -1f) { //已经放手
                    onManualOperationRelease()
                    onFinish(layout, success)
                    return 0
                }
                return Int.MAX_VALUE
            }
        }
        return 0
    }
}