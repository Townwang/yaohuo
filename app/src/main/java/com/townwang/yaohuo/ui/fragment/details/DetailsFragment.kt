package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentDetailsBinding
import com.townwang.yaohuo.databinding.ItemCommentDataBinding
import com.townwang.yaohuo.databinding.ViewDownloadStyleBinding
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.ui.activity.ActivityWebView
import com.townwang.yaohuo.ui.fragment.web.WebViewHelper
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailsFragment : Fragment(R.layout.fragment_details) {
    val binding:FragmentDetailsBinding by viewbind()
    var loading: LoadingDialog? = null
    private var page: Int = 1
    private var ot: Int = 0
    private val adapter = CommentAdapter()
    private val viewModel: DetailsModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            ot = 0
            adapter.datas.clear()
            viewModel.commentDetails(page, ot)
        }
        binding.refreshLayout.setOnLoadMoreListener {
            if (adapter.datas.isNullOrEmpty()) {
                binding.refreshLayout.finishLoadMoreWithNoMoreData()
                return@setOnLoadMoreListener
            }
            if (adapter.datas.last().floor == 1) {
                binding.refreshLayout.finishLoadMoreWithNoMoreData()
                return@setOnLoadMoreListener
            }
            page++
            viewModel.commentDetails(page, ot)
        }
        binding.attention.onClickListener {
            Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        binding.reply.onClickListener {
            if (requireArguments().getBoolean(HOME_DETAILS_BEAR_KEY).not()) {
                Snackbar.make(requireView(), "已经结贴，无法参与评论！", Snackbar.LENGTH_SHORT).show()
                return@onClickListener
            }
            val magTransaction = parentFragmentManager.beginTransaction()
            val fragment = parentFragmentManager.findFragmentByTag("input frag")
            if (fragment != null) {
                magTransaction.remove(fragment)
            }
            val dialogFragment =
                CommentDialogFragment().apply {
                    arguments = Bundle().also {
                        it.putString(SEND_CONTENT_KEY, "请不要乱打字回复，以免被加黑。")
                    }
                }
            dialogFragment.mDialogListener = { _, message ->
                loading = Loading("正在发送评论...").apply {
                    setSuccessText("评论成功")
                    setFailedText("评论失败")
                }
                loading?.show()
                viewModel.reply(
                    message,
                    requireContext().config(BuildConfig.YH_COOKIE_SID),
                    sendmsg = "1"
                )
                dialogFragment.dismiss()
            }
            dialogFragment.show(parentFragmentManager, "input frag")
        }
        binding.comment.onClickListener {
            binding.scrollerLayout.smoothScrollToChild(binding.commentTip)
        }
        binding.praise.onClickListener {
            binding.praiseValue.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_bottom_praise_selected),
                null,
                null,
                null
            )
            binding.praise.isClickable = false
            binding.praiseValue.text = (binding.praiseValue.text.toString().toInt() + 1).toString()
            viewModel.praise()
        }
        binding.favorite.onClickListener {
            binding.favoriteValue.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_bottom_favorite_selected),
                null,
                null,
                null
            )
            binding.favorite.isClickable = false
            viewModel.favorite()
        }
        binding.commentLists.adapter = adapter
        binding.commentLists.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.onItemListener = { item, data ->
            if (data is CommitListBean) {
                if (item is ItemCommentDataBinding) {
                    item.apply {
                        viewModel.getUserInfo(
                            item,
                            getParam(data.avatar, BuildConfig.YH_REPLY_TOUSERID)
                        )
                        WebViewHelper(requireContext(), auth).apply {
                            shouldOverrideUrlLoading = true
                        }.setHtmlCode(data.auth)
                        floor.text = "${data.floor}楼"
                        reward.text = data.b
                        time.text = data.time

                        WebViewHelper(requireContext(), htvContent).apply {
                            shouldOverrideUrlLoading = true
                        }.setHtmlCode(data.content)
                        if (userImg.drawable != null) {
                            startAnimator(userImg.drawable)
                        }
                        reply.onClickListener {
                            sendCommit(data)
                        }
                    }
                }
            }

        }
        binding.refreshLayout.setOnMultiListener(object : SimpleMultiListener() {
            override fun onFooterMoving(
                footer: RefreshFooter?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
                binding.scrollerLayout.stickyOffset = offset
            }
        })
    }

    @SuppressLint("InflateParams")
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.data.observe(viewLifecycleOwner, safeObserver {
            viewModel.getAvatar(getParam(it.headUrl, BuildConfig.YH_REPLY_TOUSERID))
            binding.time.text = it.time
            binding.readNum.visibility = View.VISIBLE
            binding.readNum.text =
                requireArguments().getString(HOME_DETAILS_READ_KEY, "").split(" ").first()
            binding.constraintLayout.visibility = View.VISIBLE
            binding.userName.text = it.userName
            binding.constraintLayout.visibility = View.VISIBLE
            if (it.onLineState) {
                binding.online.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_green_10)
            } else {
                binding.online.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_grey_10)
            }
            if (it.giftMoney.isNotEmpty()) {
                binding.linearTop.visibility = View.VISIBLE
                binding.icon.text = BuildConfig.YH_MATCH_LIST_MEAT
                binding.icon.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
                binding.subtitle.text =
                    HtmlCompat.fromHtml(it.giftMoney, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            if (it.reward.isNotEmpty()) {
                binding.linearTop.visibility = View.VISIBLE
                binding.icon.text = BuildConfig.YH_MATCH_LIST_GIVE
                binding.icon.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
                binding.subtitle.text = it.reward
            }

            WebViewHelper(requireContext(), binding.webView).apply {
                shouldOverrideUrlLoading = true
            }.setHtmlCode(it.content)
            binding.webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.scrollerLayout.checkLayoutChange()
                }
            }
            binding.praiseValue.text = it.praiseSize
            it.downloadList?.forEach { download ->
                val contentLoad = ViewDownloadStyleBinding.inflate(layoutInflater)
                contentLoad.downloadName.text = download.fileName

                contentLoad.downloadUrl.onClickListener { _ ->
                    val uri = Uri.parse(Uri.encode(download.url, "-![.:/,%?&=]"))
                    ActivityCompat.startActivity(
                        requireContext(), Intent(
                            requireContext(), ActivityWebView::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            putExtra(WEB_VIEW_URL_KEY, uri.toString())
                            putExtra(WEB_VIEW_URL_TITLE, "地址加载...")
                        }, null
                    )
                }
                if (download.description.isNullOrEmpty()) {
                    contentLoad.description.visibility = View.GONE
                } else {
                    contentLoad.description.text = download.description
                    contentLoad.description.visibility = View.VISIBLE
                }
                binding.listContent.addView(contentLoad.root)
            }
        })

        viewModel.avatar.observe(viewLifecycleOwner, safeObserver {
            Glide.with(requireContext())
                .load(getUrlString(it))
                .apply(options)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.userImg)
        })
        viewModel.grade.observe(viewLifecycleOwner, safeObserver {
            it ?: return@safeObserver
            binding.leval.text = it
        })
        viewModel.commentSize.observe(viewLifecycleOwner, safeObserver {
            if (page == 1 && ot == 0) {
                binding.commentValue.text = it
            }
        })
        viewModel.commentLists.observe(viewLifecycleOwner, safeObserver {
            binding.commentLists.visibility = View.VISIBLE
            binding.noMore.visibility = View.GONE
            binding.refreshLayout.setEnableLoadMore(true)
            if (it is ArrayList<CommitListBean>) {
                adapter.datas = it
            }
        })
        viewModel.noMore.observe(viewLifecycleOwner, safeObserver {
            binding.noMore.visibility = View.VISIBLE
            if (it) {
                binding.commentLists.visibility = View.GONE
                binding.refreshLayout.setEnableLoadMore(false)
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (!it) {
                refreshDone(true)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            refreshDone(false)
            requireContext().handleException(it)
        })
        viewModel.commentSuccess.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                page = 1
                ot = 0
                adapter.datas.clear()
                viewModel.commentDetails(page, ot)
                loading?.loadSuccess()
            } else {
                loading?.loadFailed()
            }
        })
        viewModel.medal.observe(viewLifecycleOwner, safeObserver {
            it ?: return@safeObserver
            if (isVisible) {
                val image = ImageView(requireContext())
                Glide.with(requireContext())
                    .load(getUrlString(it))
                    .apply(options)
                    .into(image)
                binding.honor.visibility = View.VISIBLE
                binding.honor.addView(image)
            }
        })
        viewModel.itemAvatar.observe(viewLifecycleOwner, safeObserver {
            it ?: return@safeObserver
            if (isVisible) {
                val imgView = it.item.userImg.findViewWithTag<ImageView>(it.touserid)
                if (imgView != null) {
                    Glide.with(requireContext())
                        .load(getUrlString(it.avatarUrl))
                        .apply(options)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(it.item.userImg)
                    it.item.leval.text = it.level.toString()
                }
            }
        })
        viewModel.getDetails(requireArguments().getString(HOME_DETAILS_URL_KEY, ""))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshDone(success: Boolean) {
        binding.commentTip.visibility = View.VISIBLE
        if (page == 1) {
            binding.refreshLayout.finishRefresh(success)
        } else {
            binding.refreshLayout.finishLoadMore(success)
        }
    }

    private fun sendCommit(data: CommitListBean) {
        if (requireArguments().getBoolean(HOME_DETAILS_BEAR_KEY).not()) {
            Snackbar.make(requireView(), "已经结贴，无法参与评论！", Snackbar.LENGTH_SHORT).show()
        } else {
            if (getParam(
                    data.url,
                    BuildConfig.YH_REPLY_TOUSERID
                ) != requireContext().config(TROUSER_KEY)
            ) {
                val magTransaction = childFragmentManager.beginTransaction()
                val fragment = childFragmentManager.findFragmentByTag("input frag")
                if (fragment != null) {
                    magTransaction.remove(fragment)
                }
                val dialogFragment = CommentDialogFragment()
                    .apply {
                        arguments = Bundle().also {
                            it.putString(SEND_CONTENT_KEY, "回复：${data.auth}")
                        }
                    }
                dialogFragment.mDialogListener = { _, msg ->
                    loading = Loading("正在回复妖友...").apply {
                        setSuccessText("评论成功")
                        setFailedText("评论失败")
                    }
                    loading?.show()
                    viewModel.reply(
                        msg,
                        requireContext().config(BuildConfig.YH_COOKIE_SID),
                        data.floor.toString(),
                        getParam(data.url, BuildConfig.YH_REPLY_TOUSERID),
                        "0"
                    )
                    dialogFragment.dismiss()
                }
                dialogFragment.show(childFragmentManager, "input frag")
            } else {
                context?.toast("不能给自己回复！")
            }
        }
    }
    override fun onDestroy() {
        loading?.run {
            close()
        }
        super.onDestroy()
    }
}
