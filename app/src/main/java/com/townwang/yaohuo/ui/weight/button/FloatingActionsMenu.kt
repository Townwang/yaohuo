package com.townwang.yaohuo.ui.weight.button

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.annotation.ColorRes
import com.townwang.yaohuo.R

class FloatingActionsMenu : ViewGroup {
    private var mAddButtonPlusColor = 0
    private var mAddButtonColorNormal = 0
    private var mAddButtonColorPressed = 0
    private var mAddButtonSize = 0
    private var mAddButtonStrokeVisible = false
    private var mExpandDirection = 0
    private var mButtonSpacing = 0
    private var mLabelsMargin = 0
    private var mLabelsVerticalOffset = 0
    var isExpanded = false
        private set
    private val mExpandAnimation =
        AnimatorSet().setDuration(ANIMATION_DURATION.toLong())
    private val mCollapseAnimation =
        AnimatorSet().setDuration(ANIMATION_DURATION.toLong())
    private var mAddButton: AddFloatingActionButton? = null
    private var mRotatingDrawable: RotatingDrawable? = null
    private var mMaxButtonWidth = 0
    private var mMaxButtonHeight = 0
    private var mLabelsStyle = 0
    private var mLabelsPosition = 0
    private var mButtonsCount = 0
    private var mTouchDelegateGroup: TouchDelegateGroup? = null
    private var mListener: OnFloatingActionsMenuUpdateListener? = null

    interface OnFloatingActionsMenuUpdateListener {
        fun onMenuExpanded()
        fun onMenuCollapsed()
    }

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

    private fun init(
        context: Context,
        attributeSet: AttributeSet?
    ) {
        mButtonSpacing =
            (resources.getDimension(R.dimen.fab_actions_spacing) - resources.getDimension(
                R.dimen.fab_shadow_radius
            ) - resources.getDimension(R.dimen.fab_shadow_offset)).toInt()
        mLabelsMargin =
            resources.getDimensionPixelSize(R.dimen.fab_labels_margin)
        mLabelsVerticalOffset =
            resources.getDimensionPixelSize(R.dimen.fab_shadow_offset)
        mTouchDelegateGroup = TouchDelegateGroup(this)
        touchDelegate = mTouchDelegateGroup
        val attr = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.FloatingActionsMenu,
            0,
            0
        )
        mAddButtonPlusColor = attr.getColor(
            R.styleable.FloatingActionsMenu_fab_addButtonPlusIconColor,
            getColor(android.R.color.black)
        )
        mAddButtonColorNormal = attr.getColor(
            R.styleable.FloatingActionsMenu_fab_addButtonColorNormal,
            getColor(android.R.color.transparent)
        )
        mAddButtonColorPressed = attr.getColor(
            R.styleable.FloatingActionsMenu_fab_addButtonColorPressed,
            getColor(android.R.color.transparent)
        )
        mAddButtonSize = attr.getInt(
            R.styleable.FloatingActionsMenu_fab_addButtonSize,
            FloatingActionButton.SIZE_NORMAL
        )
        mAddButtonStrokeVisible = attr.getBoolean(
            R.styleable.FloatingActionsMenu_fab_addButtonStrokeVisible,
            true
        )
        mExpandDirection = attr.getInt(
            R.styleable.FloatingActionsMenu_fab_expandDirection,
            EXPAND_UP
        )
        mLabelsStyle = attr.getResourceId(
            R.styleable.FloatingActionsMenu_fab_labelStyle,
            0
        )
        mLabelsPosition = attr.getInt(
            R.styleable.FloatingActionsMenu_fab_labelsPosition,
            LABELS_ON_LEFT_SIDE
        )
        attr.recycle()
        check(!(mLabelsStyle != 0 && expandsHorizontally())) { "Action labels in horizontal expand orientation is not supported." }
        createAddButton(context)
    }

    fun setOnFloatingActionsMenuUpdateListener(listener: OnFloatingActionsMenuUpdateListener?) {
        mListener = listener
    }

    private fun expandsHorizontally(): Boolean {
        return mExpandDirection == EXPAND_LEFT || mExpandDirection == EXPAND_RIGHT
    }

    private class RotatingDrawable(drawable: Drawable) :
        LayerDrawable(arrayOf(drawable)) {
        private var mRotation = 0f
        var rotation: Float
            get() = mRotation
            set(rotation) {
                mRotation = rotation
                invalidateSelf()
            }

        override fun draw(canvas: Canvas) {
            canvas.save()
            canvas.rotate(
                mRotation,
                bounds.centerX().toFloat(),
                bounds.centerY().toFloat()
            )
            super.draw(canvas)
            canvas.restore()
        }
    }

    private fun createAddButton(context: Context) {
        mAddButton = object : AddFloatingActionButton(context) {
            override fun updateBackground() {
                mPlusColor = mAddButtonPlusColor
                mColorNormal = mAddButtonColorNormal
                mColorPressed = mAddButtonColorPressed
                mStrokeVisible = mAddButtonStrokeVisible
                super.updateBackground()
            }
            override var iconDrawable: Drawable = RotatingDrawable(super.dgetIconDrawable()).apply {
                mRotatingDrawable = this
                val interpolator = OvershootInterpolator()
                val collapseAnimator = ObjectAnimator.ofFloat(
                    this,
                    "rotation",
                    EXPANDED_PLUS_ROTATION,
                    COLLAPSED_PLUS_ROTATION
                )
                val expandAnimator = ObjectAnimator.ofFloat(
                    this,
                    "rotation",
                    COLLAPSED_PLUS_ROTATION,
                    EXPANDED_PLUS_ROTATION
                )
                collapseAnimator.interpolator = interpolator
                expandAnimator.interpolator = interpolator
                mExpandAnimation.play(expandAnimator)
                mCollapseAnimation.play(collapseAnimator)
            }
        }
        mAddButton?.id = R.id.fab_expand_menu_button
        mAddButton?.size = mAddButtonSize
        mAddButton?.setOnClickListener { v: View? -> toggle() }
        addView(mAddButton, super.generateDefaultLayoutParams())
        mButtonsCount++
    }

    fun addButton(button: FloatingActionButton?) {
        addView(button, mButtonsCount - 1)
        mButtonsCount++
        if (mLabelsStyle != 0) {
            createLabels()
        }
    }

    fun removeButton(button: FloatingActionButton) {
        removeView(button.labelView)
        removeView(button)
        button.setTag(R.id.fab_label, null)
        mButtonsCount--
    }

    private fun getColor(@ColorRes id: Int): Int {
        return resources.getColor(id)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        var width = 0
        var height = 0
        mMaxButtonWidth = 0
        mMaxButtonHeight = 0
        var maxLabelWidth = 0
        for (i in 0 until mButtonsCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            when (mExpandDirection) {
                EXPAND_UP, EXPAND_DOWN -> {
                    mMaxButtonWidth = Math.max(mMaxButtonWidth, child.measuredWidth)
                    height += child.measuredHeight
                }
                EXPAND_LEFT, EXPAND_RIGHT -> {
                    width += child.measuredWidth
                    mMaxButtonHeight =
                        Math.max(mMaxButtonHeight, child.measuredHeight)
                }
            }
            if (!expandsHorizontally()) {
                val label = child.getTag(R.id.fab_label)
                if (label is TextView) {
                    maxLabelWidth = Math.max(maxLabelWidth, label.measuredWidth)
                }
            }
        }
        if (!expandsHorizontally()) {
            width = mMaxButtonWidth + if (maxLabelWidth > 0) maxLabelWidth + mLabelsMargin else 0
        } else {
            height = mMaxButtonHeight
        }
        when (mExpandDirection) {
            EXPAND_UP, EXPAND_DOWN -> {
                height += mButtonSpacing * (mButtonsCount - 1)
                height = adjustForOvershoot(height)
            }
            EXPAND_LEFT, EXPAND_RIGHT -> {
                width += mButtonSpacing * (mButtonsCount - 1)
                width = adjustForOvershoot(width)
            }
        }
        setMeasuredDimension(width, height)
    }

    private fun adjustForOvershoot(dimension: Int): Int {
        return dimension * 12 / 10
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        when (mExpandDirection) {
            EXPAND_UP, EXPAND_DOWN -> {
                val expandUp =
                    mExpandDirection == EXPAND_UP
                if (changed) {
                    mTouchDelegateGroup!!.clearTouchDelegates()
                }
                val addButtonY = if (expandUp) b - t - mAddButton!!.measuredHeight else 0
                // Ensure mAddButton is centered on the line where the buttons should be
                val buttonsHorizontalCenter =
                    if (mLabelsPosition == LABELS_ON_LEFT_SIDE) r - l - mMaxButtonWidth / 2 else mMaxButtonWidth / 2
                val addButtonLeft = buttonsHorizontalCenter - mAddButton!!.measuredWidth / 2
                mAddButton!!.layout(
                    addButtonLeft,
                    addButtonY,
                    addButtonLeft + mAddButton!!.measuredWidth,
                    addButtonY + mAddButton!!.measuredHeight
                )
                val labelsOffset = mMaxButtonWidth / 2 + mLabelsMargin
                val labelsXNearButton =
                    if (mLabelsPosition == LABELS_ON_LEFT_SIDE) buttonsHorizontalCenter - labelsOffset else buttonsHorizontalCenter + labelsOffset
                var nextY =
                    if (expandUp) addButtonY - mButtonSpacing else addButtonY + mAddButton!!.measuredHeight + mButtonSpacing
                var i = mButtonsCount - 1
                while (i >= 0) {
                    val child = getChildAt(i)
                    if (child === mAddButton || child.visibility == View.GONE) {
                        i--
                        continue
                    }
                    val childX = buttonsHorizontalCenter - child.measuredWidth / 2
                    val childY = if (expandUp) nextY - child.measuredHeight else nextY
                    child.layout(
                        childX,
                        childY,
                        childX + child.measuredWidth,
                        childY + child.measuredHeight
                    )
                    val collapsedTranslation = addButtonY - childY.toFloat()
                    val expandedTranslation = 0f
                    child.translationY =
                        if (isExpanded) expandedTranslation else collapsedTranslation
                    child.alpha = if (isExpanded) 1f else 0f
                    val params =
                        child.layoutParams as LayoutParams
                    params.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation)
                    params.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation)
                    params.setAnimationsTarget(child)
                    val label =
                        child.getTag(R.id.fab_label) as View
                    val labelXAwayFromButton =
                        if (mLabelsPosition == LABELS_ON_LEFT_SIDE) labelsXNearButton - label.measuredWidth else labelsXNearButton + label.measuredWidth
                    val labelLeft =
                        if (mLabelsPosition == LABELS_ON_LEFT_SIDE) labelXAwayFromButton else labelsXNearButton
                    val labelRight =
                        if (mLabelsPosition == LABELS_ON_LEFT_SIDE) labelsXNearButton else labelXAwayFromButton
                    val labelTop =
                        childY - mLabelsVerticalOffset + (child.measuredHeight - label.measuredHeight) / 2
                    label.layout(
                        labelLeft,
                        labelTop,
                        labelRight,
                        labelTop + label.measuredHeight
                    )
                    val touchArea = Rect(
                        Math.min(childX, labelLeft),
                        childY - mButtonSpacing / 2,
                        Math.max(childX + child.measuredWidth, labelRight),
                        childY + child.measuredHeight + mButtonSpacing / 2
                    )
                    mTouchDelegateGroup!!.addTouchDelegate(TouchDelegate(touchArea, child))
                    label.translationY =
                        if (isExpanded) expandedTranslation else collapsedTranslation
                    label.alpha = if (isExpanded) 1f else 0f
                    val labelParams =
                        label.layoutParams as LayoutParams
                    labelParams.mCollapseDir.setFloatValues(
                        expandedTranslation,
                        collapsedTranslation
                    )
                    labelParams.mExpandDir.setFloatValues(
                        collapsedTranslation,
                        expandedTranslation
                    )
                    labelParams.setAnimationsTarget(label)
                    nextY =
                        if (expandUp) childY - mButtonSpacing else childY + child.measuredHeight + mButtonSpacing
                    i--
                }
            }
            EXPAND_LEFT, EXPAND_RIGHT -> {
                val expandLeft =
                    mExpandDirection == EXPAND_LEFT
                val addButtonX = if (expandLeft) r - l - mAddButton!!.measuredWidth else 0
                // Ensure mAddButton is centered on the line where the buttons should be
                val addButtonTop =
                    b - t - mMaxButtonHeight + (mMaxButtonHeight - mAddButton!!.measuredHeight) / 2
                mAddButton!!.layout(
                    addButtonX,
                    addButtonTop,
                    addButtonX + mAddButton!!.measuredWidth,
                    addButtonTop + mAddButton!!.measuredHeight
                )
                var nextX =
                    if (expandLeft) addButtonX - mButtonSpacing else addButtonX + mAddButton!!.measuredWidth + mButtonSpacing
                var i = mButtonsCount - 1
                while (i >= 0) {
                    val child = getChildAt(i)
                    if (child === mAddButton || child.visibility == View.GONE) {
                        i--
                        continue
                    }
                    val childX = if (expandLeft) nextX - child.measuredWidth else nextX
                    val childY =
                        addButtonTop + (mAddButton!!.measuredHeight - child.measuredHeight) / 2
                    child.layout(
                        childX,
                        childY,
                        childX + child.measuredWidth,
                        childY + child.measuredHeight
                    )
                    val collapsedTranslation = addButtonX - childX.toFloat()
                    val expandedTranslation = 0f
                    child.translationX =
                        if (isExpanded) expandedTranslation else collapsedTranslation
                    child.alpha = if (isExpanded) 1f else 0f
                    val params =
                        child.layoutParams as LayoutParams
                    params.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation)
                    params.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation)
                    params.setAnimationsTarget(child)
                    nextX =
                        if (expandLeft) childX - mButtonSpacing else childX + child.measuredWidth + mButtonSpacing
                    i--
                }
            }
        }
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(super.generateDefaultLayoutParams())
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(
            super.generateLayoutParams(
                attrs
            )
        )
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(
            super.generateLayoutParams(
                p
            )
        )
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return super.checkLayoutParams(p)
    }

    private inner class LayoutParams(source: ViewGroup.LayoutParams?) :
        ViewGroup.LayoutParams(source) {
        val mExpandDir = ObjectAnimator()
        private val mExpandAlpha = ObjectAnimator()
        val mCollapseDir = ObjectAnimator()
        private val mCollapseAlpha = ObjectAnimator()
        private var animationsSetToPlay = false
        fun setAnimationsTarget(view: View) {
            mCollapseAlpha.target = view
            mCollapseDir.target = view
            mExpandAlpha.target = view
            mExpandDir.target = view

            // Now that the animations have targets, set them to be played
            if (!animationsSetToPlay) {
                addLayerTypeListener(mExpandDir, view)
                addLayerTypeListener(mCollapseDir, view)
                mCollapseAnimation.play(mCollapseAlpha)
                mCollapseAnimation.play(mCollapseDir)
                mExpandAnimation.play(mExpandAlpha)
                mExpandAnimation.play(mExpandDir)
                animationsSetToPlay = true
            }
        }

        private fun addLayerTypeListener(
            animator: Animator,
            view: View
        ) {
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.setLayerType(View.LAYER_TYPE_NONE, null)
                }

                override fun onAnimationStart(animation: Animator) {
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                }
            })
        }

        init {
            mExpandDir.interpolator = sExpandInterpolator
            mExpandAlpha.interpolator = sAlphaExpandInterpolator
            mCollapseDir.interpolator = sCollapseInterpolator
            mCollapseAlpha.interpolator = sCollapseInterpolator
            mCollapseAlpha.setProperty(View.ALPHA)
            mCollapseAlpha.setFloatValues(1f, 0f)
            mExpandAlpha.setProperty(View.ALPHA)
            mExpandAlpha.setFloatValues(0f, 1f)
            when (mExpandDirection) {
                EXPAND_UP, EXPAND_DOWN -> {
                    mCollapseDir.setProperty(View.TRANSLATION_Y)
                    mExpandDir.setProperty(View.TRANSLATION_Y)
                }
                EXPAND_LEFT, EXPAND_RIGHT -> {
                    mCollapseDir.setProperty(View.TRANSLATION_X)
                    mExpandDir.setProperty(View.TRANSLATION_X)
                }
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        bringChildToFront(mAddButton)
        mButtonsCount = childCount
        if (mLabelsStyle != 0) {
            createLabels()
        }
    }

    private fun createLabels() {
        val context: Context =
            ContextThemeWrapper(context, mLabelsStyle)
        for (i in 0 until mButtonsCount) {
            val button =
                getChildAt(i) as FloatingActionButton
            val title = button.title
            if (button === mAddButton || title == null || button.getTag(R.id.fab_label) != null
            ) continue
            val label = TextView(context)
            label.setTextAppearance(getContext(), mLabelsStyle)
            label.text = button.title
            addView(label)
            button.setTag(R.id.fab_label, label)
        }
    }

    fun collapse() {
        collapse(false)
    }

    fun collapseImmediately() {
        collapse(true)
    }

    private fun collapse(immediately: Boolean) {
        if (isExpanded) {
            isExpanded = false
            mTouchDelegateGroup!!.setEnabled(false)
            mCollapseAnimation.duration = if (immediately) 0 else ANIMATION_DURATION.toLong()
            mCollapseAnimation.start()
            mExpandAnimation.cancel()
            if (mListener != null) {
                mListener!!.onMenuCollapsed()
            }
        }
    }

    fun toggle() {
        if (isExpanded) {
            collapse()
        } else {
            expand()
        }
    }

    fun expand() {
        if (!isExpanded) {
            isExpanded = true
            mTouchDelegateGroup!!.setEnabled(true)
            mCollapseAnimation.cancel()
            mExpandAnimation.start()
            if (mListener != null) {
                mListener!!.onMenuExpanded()
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mAddButton!!.isEnabled = enabled
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState =
            SavedState(superState)
        savedState.mExpanded = isExpanded
        return savedState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            isExpanded = state.mExpanded
            mTouchDelegateGroup!!.setEnabled(isExpanded)
            if (mRotatingDrawable != null) {
                mRotatingDrawable!!.rotation =
                    if (isExpanded) EXPANDED_PLUS_ROTATION else COLLAPSED_PLUS_ROTATION
            }
            super.onRestoreInstanceState(state.superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    class SavedState : BaseSavedState {
        var mExpanded = false

        constructor(parcel: Parcelable?) : super(parcel) {}
        private constructor(`in`: Parcel) : super(`in`) {
            mExpanded = `in`.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(if (mExpanded) 1 else 0)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> =
                object :
                    Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState? {
                        return SavedState(
                            `in`
                        )
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(
                            size
                        )
                    }
                }
        }
    }

    companion object {
        const val EXPAND_UP = 0
        const val EXPAND_DOWN = 1
        const val EXPAND_LEFT = 2
        const val EXPAND_RIGHT = 3
        const val LABELS_ON_LEFT_SIDE = 0
        const val LABELS_ON_RIGHT_SIDE = 1
        private const val ANIMATION_DURATION = 300
        private const val COLLAPSED_PLUS_ROTATION = 0f
        private const val EXPANDED_PLUS_ROTATION = 90f + 45f
        private val sExpandInterpolator: Interpolator =
            OvershootInterpolator()
        private val sCollapseInterpolator: Interpolator =
            DecelerateInterpolator(3f)
        private val sAlphaExpandInterpolator: Interpolator =
            DecelerateInterpolator()
    }
}