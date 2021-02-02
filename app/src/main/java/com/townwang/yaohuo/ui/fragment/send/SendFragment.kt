package com.townwang.yaohuo.ui.fragment.send

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityListBinding
import com.townwang.yaohuo.databinding.FragmentSendBinding
import com.townwang.yaohuo.databinding.ProBbsSwitchBinding
import com.townwang.yaohuo.repo.data.SelectBean
import com.townwang.yaohuo.repo.data.YaoCdnReq
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

typealias SendListener = (fragment: SendFragment, title: String, type: String, content: String) -> Unit

class SendFragment : DialogFragment() {
    private var _binding: FragmentSendBinding? = null
    private var loading: LoadingDialog? = null
    private val binding get() = _binding!!
    var mDialogListener: SendListener? = null
    var type = 0
    val viewModel: UploadFileModel by viewModel()
    private val adapter = SelectAdapter()
    private lateinit var adapterImg: ImageAdapter
    private val listImgs = arrayListOf<YaoCdnReq>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireDialog().window?.requestFeature(Window.FEATURE_NO_TITLE)
        requireDialog().setCancelable(true)
        requireDialog().setCanceledOnTouchOutside(true)
        _binding = FragmentSendBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("InlinedApi", "WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterImg = ImageAdapter(requireContext())
        binding.commentEt.requestFocus()
        binding.commentEt.post {
            (requireActivity().getSystemService(
                Context
                    .INPUT_METHOD_SERVICE
            ) as InputMethodManager).showSoftInput(binding.commentEt, 0)
        }
        binding.close.onClickListener {
            dialog?.dismiss()
        }
        binding.send.onClickListener {
            val title = binding.title.text.toString()
            if (title.isNullOrEmpty()) {
                Snackbar.make(it, "请输入标题", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            if (title.length !in 5..25) {
                Snackbar.make(it, "请输入标题在5~25个字符内", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            val content = binding.commentEt.text?.lines()
            if (content.isNullOrEmpty() || binding.commentEt.text.toString().length <= 15) {
                Snackbar.make(it, "内容不足15字", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            if (type == 0) {
                Snackbar.make(it, "请选择板块", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            val stringBuilder = StringBuilder()
            content.forEach { stringBuilderValue ->
                if (content.last() != stringBuilderValue) {
                    stringBuilder.append("$stringBuilderValue///")
                } else {
                    stringBuilder.append(stringBuilderValue)
                }
            }
            adapterImg.datas.remove(adapterImg.datas.last())
            if (adapterImg.datas.isNotEmpty()) {
                adapterImg.datas.forEach { data ->
                    stringBuilder.append("[img]${data.url}[/img]///")
                }
            }
            mDialogListener?.invoke(this, title, type.toString(), stringBuilder.toString())
        }
        listImgs.add(YaoCdnReq(0))
        adapterImg.datas = listImgs
        binding.gridView.adapter = adapterImg
        adapter.onItemClickListener = { _, data ->
            if (data is YaoCdnReq) {
                viewModel.remove(data.delete)
            }
        }

        binding.gridView.setOnItemClickListener { _, _, position, id ->
            if (position == adapterImg.datas.lastIndex) {
                PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isGif(true)
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isCompress(true)
                    .forResult(PictureConfig.CHOOSE_REQUEST)
            }
        }
        binding.selectBBSValue.doOnPreDraw {
            var isOpenPopup = true
            val contentBinding = ProBbsSwitchBinding.inflate(layoutInflater)
            val tooltipPopup = PopupWindow(contentBinding.root).apply {
                isOutsideTouchable = true
                height = WindowManager.LayoutParams.WRAP_CONTENT
                width = WindowManager.LayoutParams.WRAP_CONTENT
            }
            contentBinding.selectList.layoutManager =
                (StaggeredGridLayoutManager(
                    3,
                    StaggeredGridLayoutManager.VERTICAL
                ))
            contentBinding.selectList.adapter = adapter
            adapter.onItemClickListener = { _, data ->
                if (data is SelectBean) {
                    type = data.type
                    binding.selectBBSValue.text = data.string
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
                binding.selectBBSValue.text = lists[type].string
            }
            binding.selectBBSValue.onClickListener {
                if (isOpenPopup) {
                    isOpenPopup = false
                    type = 0
                    binding.selectBBSValue.text = "请选择合适的板块"
                    PopupWindowCompat.showAsDropDown(
                        tooltipPopup,
                        binding.selectBBSValue,
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

        viewModel.fileStatus.observe(requireActivity(), safeObserver {
            if (it) {
                loading?.loadSuccess()
            }
        })
        viewModel.error.observe(requireActivity(),safeObserver {
            loading?.setFailedText(it.message)
            loading?.loadFailed()
            requireContext().handleException(it)
        })
        viewModel.list.observe(requireActivity(), safeObserver {
            it ?: return@safeObserver
            listImgs.add(0, it)
            adapterImg.datas = listImgs
        })

    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.let {
            it.setGravity(Gravity.TOP)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                    val selectList =
                        PictureSelector.obtainMultipleResult(data)
                    if (loading == null) {
                        loading = Loading("上传图片中...").apply {
                            setFailedText("上传失败")
                            setSuccessText("上传成功")
                        }
                    }
                    loading?.show()
                    selectList?.forEach { file ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            viewModel.uploadFile(File(file.androidQToPath),file.mimeType)
                        } else {
                            viewModel.uploadFile(File(file.realPath),file.mimeType)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        loading?.close()
    }
}