package com.townwang.yaohuo.ui.weight.commit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.SEND_CONTENT_KEY
import kotlinx.android.synthetic.main.fragment_comment_dialog.*

/**
 * 输入对话框：评论框
 */

typealias CommentDialogSendListener = (fragment: CommentDialogFragment, message: String) -> Unit

class CommentDialogFragment: DialogFragment() {
     var mDialogListener: CommentDialogSendListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      requireDialog().window?.requestFeature(Window.FEATURE_NO_TITLE)
        requireDialog().setCancelable(true)
        requireDialog().setCanceledOnTouchOutside(true)
        return inflater.inflate(R.layout.fragment_comment_dialog, container, false)
    }

    @SuppressLint("InlinedApi", "WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog_comment_et.hint = HtmlCompat.fromHtml(requireArguments().getString(SEND_CONTENT_KEY,""),Html.FROM_HTML_MODE_LEGACY)
        dialog_comment_et.requestFocus()
        dialog_comment_et.post {
            (requireActivity().getSystemService(
                Context
                    .INPUT_METHOD_SERVICE
            ) as InputMethodManager).showSoftInput(dialog_comment_et, 0)
        }
        dialog_comment_bt.setOnClickListener {
            val commentStr = dialog_comment_et.text.toString()
            mDialogListener?.invoke(this@CommentDialogFragment, "$commentStr      \uD83D\uDCF1")
        }
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val titleDividerId = resources.getIdentifier("titleDivider", "id", "android")
        if (titleDividerId > 0) {
            val titleDivider = requireDialog().findViewById<View>(titleDividerId)
            titleDivider?.setBackgroundColor(ResourcesCompat.getColor(resources,android.R.color.transparent,null))
        }
    }

}