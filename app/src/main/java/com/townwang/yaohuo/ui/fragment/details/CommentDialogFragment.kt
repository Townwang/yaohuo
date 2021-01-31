package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.common.SEND_CONTENT_KEY
import com.townwang.yaohuo.common.onClickListener
import com.townwang.yaohuo.databinding.FragmentCommentDialogBinding
import java.lang.StringBuilder

/**
 * 输入对话框：评论框
 */

typealias CommentDialogSendListener = (fragment: CommentDialogFragment, message: String) -> Unit

class CommentDialogFragment : DialogFragment() {
    private var _binding: FragmentCommentDialogBinding? = null

    private val binding get() = _binding!!

    var mDialogListener: CommentDialogSendListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireDialog().window?.requestFeature(Window.FEATURE_NO_TITLE)
        requireDialog().setCancelable(true)
        requireDialog().setCanceledOnTouchOutside(true)
        _binding = FragmentCommentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("InlinedApi", "WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogCommentEt.hint = HtmlCompat.fromHtml(
            requireArguments().getString(SEND_CONTENT_KEY, ""),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.dialogCommentEt.requestFocus()
        binding.dialogCommentEt.post {
            (requireActivity().getSystemService(
                Context
                    .INPUT_METHOD_SERVICE
            ) as InputMethodManager).showSoftInput(binding.dialogCommentEt, 0)
        }
        binding.dialogCommentBt.onClickListener {
            val commentStr = binding.dialogCommentEt.text.lines()

            if (commentStr.isEmpty()) {
                Snackbar.make(requireView(), "请输入内容", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "请输入内容", Snackbar.LENGTH_SHORT).show()
                val stringBuilder = StringBuilder()
                commentStr.forEach {
                    if (commentStr.last() != it) {
                        stringBuilder.append("$it///")
                    } else {
                        stringBuilder.append(it)
                    }
                }
                mDialogListener?.invoke(
                    this@CommentDialogFragment,
                    "$stringBuilder      \uD83D\uDCF1"
                )
            }
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
            titleDivider?.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    android.R.color.transparent,
                    null
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}