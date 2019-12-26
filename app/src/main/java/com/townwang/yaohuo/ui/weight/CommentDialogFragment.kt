package com.townwang.yaohuo.ui.weight

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
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
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.townwang.yaohuo.R

/**
 * 输入对话框：评论框
 */

typealias CommentDialogSendListener = (fragment: CommentDialogFragment, message: String) -> Unit
class CommentDialogFragment : DialogFragment() {
     var mDialogListener: CommentDialogSendListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val window = dialog!!.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        return inflater.inflate(R.layout.fragment_comment_dialog, container, false)
    }

    @SuppressLint("InlinedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editComment = view.findViewById<EditText>(R.id.dialog_comment_et)
        val send = view.findViewById<Button>(R.id.dialog_comment_bt)

        // 把传递过来的数据设置给EditText
        editComment.hint = HtmlCompat.fromHtml(arguments!!.getString(MESSAGE_HINT)?:"",Html.FROM_HTML_MODE_LEGACY)
        editComment.requestFocus()
        editComment.post {
            (activity!!.getSystemService(
                Context
                    .INPUT_METHOD_SERVICE
            ) as InputMethodManager).showSoftInput(editComment, 0)
        }
        send.setOnClickListener { v ->
            val commentStr = editComment.text.toString()
            mDialogListener?.invoke(this@CommentDialogFragment, commentStr)
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        if (null != window) {
            window.setGravity(Gravity.BOTTOM)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        val res = resources
        val titleDividerId = res.getIdentifier("titleDivider", "id", "android")
        if (titleDividerId > 0) {
            val titleDivider = dialog?.findViewById<View>(titleDividerId)
            titleDivider?.setBackgroundColor(res.getColor(android.R.color.transparent))
        }
    }

    companion object {
        var MESSAGE_HINT = ""
        fun newInstance(message: String): CommentDialogFragment {
            //创建一个带有参数的Fragment实例
            val fragment = CommentDialogFragment()
            val bundle = Bundle()
            bundle.putString(MESSAGE_HINT, message)
            fragment.arguments = bundle//把参数传递给该DialogFragment
            return fragment
        }
    }

}