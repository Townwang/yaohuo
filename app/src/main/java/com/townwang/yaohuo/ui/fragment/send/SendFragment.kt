package com.townwang.yaohuo.ui.fragment.send

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.HOME_LIST_THEME_SHOW
import com.townwang.yaohuo.common.SEND_CONTENT_KEY
import com.townwang.yaohuo.common.config
import com.townwang.yaohuo.common.onClickListener
import com.townwang.yaohuo.repo.data.SelectBean
import com.townwang.yaohuo.repo.data.details.MeBean
import com.townwang.yaohuo.ui.fragment.pub.PubListAdapter
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_list_pub.*
import kotlinx.android.synthetic.main.fragment_send.*
import kotlinx.android.synthetic.main.pro_bbs_switch.view.*
import okhttp3.RequestBody

typealias SendListener = (fragment: SendFragment, title: String, type: String, content: String) -> Unit

class SendFragment : DialogFragment() {
    var mDialogListener: SendListener? = null
    var type = 0
    private val adapter = SelectAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireDialog().window?.requestFeature(Window.FEATURE_NO_TITLE)
        requireDialog().setCancelable(true)
        requireDialog().setCanceledOnTouchOutside(true)
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    @SuppressLint("InlinedApi", "WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comment_et.requestFocus()
        comment_et.post {
            (requireActivity().getSystemService(
                Context
                    .INPUT_METHOD_SERVICE
            ) as InputMethodManager).showSoftInput(comment_et, 0)
        }
        send.onClickListener {
            val title = title.text.toString()
            if (title.isNullOrEmpty()) {
                Snackbar.make(it, "请输入标题", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            if (title.length !in 5..25) {
                Snackbar.make(it, "请输入标题在5~25个字符内", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            val content = comment_et.text.toString()

            if (content.length <= 15) {
                Snackbar.make(it, "内容不足15字", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            if (type == 0) {
                Snackbar.make(it, "请选择板块", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            mDialogListener?.invoke(this, title, type.toString(), content)
        }
        addImg.onClickListener {
            Snackbar.make(it, "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        selectBBSValue.doOnPreDraw {
            var isOpenPopup = true
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val contentView = inflater.inflate(R.layout.pro_bbs_switch, null)
            val tooltipPopup = PopupWindow(contentView).apply {
                isOutsideTouchable = true
                height = WindowManager.LayoutParams.WRAP_CONTENT
                width = WindowManager.LayoutParams.WRAP_CONTENT
            }
            contentView.selectList.layoutManager =
                (StaggeredGridLayoutManager(
                    3,
                    StaggeredGridLayoutManager.VERTICAL
                ))
            contentView.selectList.adapter = adapter
            adapter.onItemClickListener = { _, data ->
                if (data is SelectBean) {
                    type = data.type
                    selectBBSValue.text = data.string
                    isOpenPopup = true
                    tooltipPopup.dismiss()
                }
            }
            val lists = arrayListOf<SelectBean>()
            lists.add(SelectBean(201, getString(R.string.bbs_res_share)))
            lists.add(SelectBean(197, getString(R.string.bbs_integrated_technology)))
            lists.add(SelectBean(203, getString(R.string.bbs_ml_talk_over)))
            lists.add(SelectBean(204, getString(R.string.bbs_reward)))
            lists.add(SelectBean(177, getString(R.string.bbs_tea_house)))
            lists.add(SelectBean(213, getString(R.string.bbs_quest_answer)))
            lists.add(SelectBean(240, getString(R.string.bbs_textured_photo)))
            lists.add(SelectBean(199, getString(R.string.bbs_stationService)))
            lists.add(SelectBean(198, getString(R.string.bbs_complaint)))
            adapter.datas = lists
            if (type != 0) {

            }
            selectBBSValue.onClickListener {
                if (isOpenPopup) {
                    isOpenPopup = false
                    type = 0
                    selectBBSValue.text = "请选择合适的板块"
                    PopupWindowCompat.showAsDropDown(
                        tooltipPopup,
                        selectBBSValue,
                        0,
                        0,
                        Gravity.BOTTOM
                    )
                } else {
                    isOpenPopup = true
                    tooltipPopup.dismiss()
                }
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

}