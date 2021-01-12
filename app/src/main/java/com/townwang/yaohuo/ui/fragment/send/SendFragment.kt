package com.townwang.yaohuo.ui.fragment.send

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.SEND_CONTENT_KEY
import kotlinx.android.synthetic.main.fragment_send.*


typealias SendListener = (fragment: SendFragment, message: String) -> Unit

class SendFragment : DialogFragment() {
     var mDialogListener: SendListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      requireDialog().window?.requestFeature(Window.FEATURE_NO_TITLE)
        requireDialog().setCancelable(true)
        requireDialog().setCanceledOnTouchOutside(true)
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    @SuppressLint("InlinedApi", "WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comment_et.hint = HtmlCompat.fromHtml(requireArguments().getString(SEND_CONTENT_KEY,""),Html.FROM_HTML_MODE_LEGACY)
        comment_et.requestFocus()
        comment_et.post {
            (requireActivity().getSystemService(
                Context
                    .INPUT_METHOD_SERVICE
            ) as InputMethodManager).showSoftInput(comment_et, 0)
        }
        send.setOnClickListener {
            val commentStr = comment_et.text.toString()
            mDialogListener?.invoke(this, commentStr)
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