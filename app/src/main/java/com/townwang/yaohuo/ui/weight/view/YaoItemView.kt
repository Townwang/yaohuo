package com.townwang.yaohuo.ui.weight.view

import android.content.Context
import android.graphics.Color
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.dp
import com.townwang.yaohuo.common.sp
import com.townwang.yaohuo.common.widthWithoutPadding
import kotlin.math.roundToInt

typealias OnValueChangedListener = (View, String) -> Unit

class YaoItemView : LinearLayout {

    private lateinit var keyTextView: TextView
    private lateinit var valueTextView: EditText
    private var customizedView: View? = null

    var onValueChangedListener: OnValueChangedListener? = null

     var isShowRightIndicator = false
        set(value) {
            if (field != value) {
                field = value
                if (!this@YaoItemView::valueTextView.isInitialized) return
                if (value) {
                    valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_right_direction), null)
                } else {
                    valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }
        }

    var isValueClickable = false
        set(value) {
            if (field != value) {
                field = value
                if (this::valueTextView.isInitialized) {
                    valueTextView.apply {
                        isFocusable = field
                        isClickable = field
                        isFocusableInTouchMode = field
                    }
                }
            }
        }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context ?: return

        keyTextView = TextView(context).apply {
            isClickable = false
            isFocusableInTouchMode = false
            isFocusable = false
            isActivated = false
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
        }
        valueTextView = EditText(context).apply {
            isSingleLine = true
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            background = null
            minWidth = context.dp(24f).roundToInt()
            maxLines = 1
            isFocusable = isValueClickable
            isClickable = isValueClickable
            isFocusableInTouchMode = isValueClickable
            onFocusChangeListener =
                MyOnTextChangedListener {
                    onValueChangedListener?.invoke(this, it)
                }
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
                marginStart = context.dp(16f).roundToInt()
            }
        }

        addView(keyTextView)
        addView(valueTextView)

        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.YaoItemView, defStyleAttr, 0)
        try {
            with(keyTextView) {
                text = ta.getString(R.styleable.YaoItemView_key)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimension(R.styleable.YaoItemView_keySize, context.sp(14f)))
                setTextColor(Color.BLACK)
            }
            with(valueTextView) {
                text = SpannableStringBuilder(ta.getString(R.styleable.YaoItemView_value) ?: "")
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimension(R.styleable.YaoItemView_valueSize, context.sp(14f)))
                setTextColor(ta.getColor(R.styleable.YaoItemView_valueColor, Color.BLACK))
                isValueClickable = ta.getBoolean(R.styleable.YaoItemView_isValueClickable, false)
                valueTextView.hint = ta.getString(R.styleable.YaoItemView_valueHint)
                valueTextView.filters = arrayOf(InputFilter.LengthFilter(ta.getInteger(R.styleable.YaoItemView_valueMaxLength, 25)))
                isShowRightIndicator = ta.getBoolean(R.styleable.YaoItemView_showRightIndicator, false)
            }
        } finally {
            ta.recycle()
        }
    }

    private class MyOnTextChangedListener(
            private val onTextChanged: (String) -> Unit
    ) : OnFocusChangeListener {
        var savedTextBeforeLossFocus: String = ""
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            v ?: return
            if (v is EditText) {
                if (hasFocus) {
                    savedTextBeforeLossFocus = v.text.toString()
                } else {
                    val text = v.text.toString()
                    if (text != savedTextBeforeLossFocus) onTextChanged(text)
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val vw = widthWithoutPadding()
        valueTextView.maxWidth = (vw - keyTextView.width - context.sp(16f)).toInt()
    }

    fun setInputType(inputType: Int) {
        valueTextView.inputType = inputType
    }

    fun customizeValueView(view: View) {
        removeView(valueTextView)
        addView(view)
        view.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
    }

    override fun performClick(): Boolean {
        if (customizedView != null) customizedView!!.performLongClick()
        else valueTextView.performClick()
        return super.performClick()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !isValueClickable
    }

    fun focusValue() {
        valueTextView.requestFocus()
    }
    var value: String
        get() = valueTextView.text.toString()
        set(value) {
            if (value != valueTextView.text.toString()) {
                valueTextView.text = SpannableStringBuilder(value)
                onValueChangedListener?.invoke(this, value)
            }
        }
}