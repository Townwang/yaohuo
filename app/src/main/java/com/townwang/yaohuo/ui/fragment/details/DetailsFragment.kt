package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.tu.loadingdialog.LoadingDailog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.ui.activity.ActivityWebView
import com.townwang.yaohuo.ui.fragment.web.WebViewHelper
import com.townwang.yaohuo.ui.weight.commit.CommentDialogFragment
import com.townwang.yaohuo.ui.weight.htmltext.HtmlText
import com.townwang.yaohuo.ui.weight.htmltext.TextViewFixTouchConsume
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.item_comment_data.view.*
import kotlinx.android.synthetic.main.view_download_style.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailsFragment : Fragment() {
    private var page: Int = 1
    private var ot: Int = 0
    private var loading: LoadingDailog? = null
    private val adapter = CommentAdapter()
    private val viewModel: DetailsModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout?.setOnRefreshListener {
            page = 1
            ot = 0
            adapter.datas.clear()
            viewModel.commentDetails(page, ot)
        }
        refreshLayout?.setOnLoadMoreListener {
            if (adapter.datas.isNullOrEmpty()) {
                refreshLayout?.finishLoadMoreWithNoMoreData()
                return@setOnLoadMoreListener
            }
            if (adapter.datas.last().floor == 1) {
                refreshLayout?.finishLoadMoreWithNoMoreData()
                return@setOnLoadMoreListener
            }
            page++
            viewModel.commentDetails(page, ot)
        }
        attention?.onClickListener {
            Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        reply?.onClickListener {
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
                loading = Loading("正在发送评论...").create()
                loading?.show()
                viewModel.reply(
                    message,
                    requireArguments().getString(HOME_DETAILS_URL_KEY, ""),
                    sendmsg = "1"
                )
                dialogFragment.dismiss()
            }
            dialogFragment.show(parentFragmentManager, "input frag")
        }
        comment?.onClickListener {
                scrollerLayout?.smoothScrollToChild(comment_tip)
        }
        praise?.onClickListener {
            praise_value.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_bottom_praise_selected),
                null,
                null,
                null
            )
            praise.isClickable = false
            praise_value.text = (praise_value.text.toString().toInt() + 1).toString()
            viewModel.praise()
        }
        favorite?.onClickListener {
            favorite_value.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_bottom_favorite_selected),
                null,
                null,
                null
            )
            favorite.isClickable = false
            viewModel.favorite()
        }
        commentLists?.adapter = adapter
        commentLists?.layoutManager =
            (StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            ))

        adapter.onItemListener = { item, data ->
            if (data is CommitListBean) {
                item.apply {
                    viewModel.getUserInfo(
                        this,
                        getParam(data.avatar, BuildConfig.YH_REPLY_TOUSERID)
                    )
                    auth.movementMethod = TextViewFixTouchConsume.LocalLinkMovementMethod.instance
                    HtmlText.from(data.auth.replace("<br>", "")).into(auth)
                    floor.text = "${data.floor}楼"
                    reward.text = data.b
                    time.text = data.time

                    WebViewHelper(requireContext(), htv_content).apply {
                        shouldOverrideUrlLoading = true
                    }.setHtmlCode(data.content.replace("<br>", ""))

                    if (userImg.drawable != null) {
                        startAnimator(userImg.drawable)
                    }
                    onClickListener {
                        sendCommit(data)
                    }
                }
            }

        }
        refreshLayout?.setOnMultiListener(object : SimpleMultiListener() {
            override fun onFooterMoving(
                footer: RefreshFooter?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                maxDragHeight: Int
            ) {
                scrollerLayout?.stickyOffset = offset
            }
        })
    }

    @SuppressLint("InflateParams")
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.data.observe(viewLifecycleOwner, safeObserver {
            viewModel.getAvatar(getParam(it.headUrl, BuildConfig.YH_REPLY_TOUSERID))
            time?.text = it.time
            read_num?.visibility = View.VISIBLE
            read_num?.text =
                requireArguments().getString(HOME_DETAILS_READ_KEY, "").split(" ").first()
            constraintLayout?.visibility = View.VISIBLE
            userName?.text = it.userName
            constraintLayout?.visibility = View.VISIBLE
            if (it.onLineState) {
                online?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_green_10)
            } else {
                online?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_grey_10)
            }
            if (it.giftMoney.isNotEmpty()) {
                linearTop?.visibility = View.VISIBLE
                icon?.text = BuildConfig.YH_MATCH_LIST_MEAT
                icon?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
                subtitle?.text = HtmlCompat.fromHtml(it.giftMoney, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            if (it.reward.isNotEmpty()) {
                linearTop?.visibility = View.VISIBLE
                icon?.text = BuildConfig.YH_MATCH_LIST_GIVE
                icon?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
                subtitle?.text = it.reward
            }

            WebViewHelper(requireContext(), webView).apply {
                shouldOverrideUrlLoading = true
            }.setHtmlCode(it.content)
            webView?.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    scrollerLayout?.checkLayoutChange()
                }
            }
            praise_value?.text = it.praiseSize
            it.downloadList?.forEach { download ->
                val contentLoad =
                    LayoutInflater.from(requireContext())
                        .inflate(R.layout.view_download_style, null)
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
                list_content?.addView(contentLoad)
            }
        })

        viewModel.avatar.observe(viewLifecycleOwner, safeObserver {
            Glide.with(requireContext())
                .load(getUrlString(it))
                .apply(options)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(userImg)
        })
        viewModel.grade.observe(viewLifecycleOwner, safeObserver {
            it ?: return@safeObserver
            leval?.text = it
        })
        viewModel.commentSize.observe(viewLifecycleOwner, safeObserver {
            if (page == 1 && ot == 0) {
                comment_value?.text = it
            }
        })
        viewModel.commentLists.observe(viewLifecycleOwner, safeObserver {
            commentLists?.visibility = View.VISIBLE
            noMore?.visibility = View.GONE
            refreshLayout?.setEnableLoadMore(true)
            if (it is ArrayList<CommitListBean>) {
                adapter.datas = it
            }
        })
        viewModel.noMore.observe(viewLifecycleOwner, safeObserver {
            noMore?.visibility = View.VISIBLE
            if (it) {
                commentLists?.visibility = View.GONE
                refreshLayout?.setEnableLoadMore(false)
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
            loading?.dismiss()
            if (it) {
                page = 1
                ot = 0
                adapter.datas.clear()
                viewModel.commentDetails(page, ot)
                Snackbar.make(requireView(), "评论成功", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "评论失败", Snackbar.LENGTH_SHORT).show()
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
                honor.visibility = View.VISIBLE
                honor.addView(image)
            }
        })
        viewModel.itemAvatar.observe(viewLifecycleOwner, safeObserver {
            it ?: return@safeObserver
            if (isVisible) {
                val imgView = it.item.findViewWithTag<ImageView>(it.touserid)
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
        refreshLayout ?: return
        comment_tip?.visibility = View.VISIBLE
        if (page == 1) {
            refreshLayout.finishRefresh(success)
        } else {
            refreshLayout.finishLoadMore(success)
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
                val dialogFragment = CommentDialogFragment().apply {
                    arguments = Bundle().also {
                        it.putString(SEND_CONTENT_KEY, "回复：${data.auth}")
                    }
                }
                dialogFragment.mDialogListener = { _, msg ->
                    loading = Loading("正在回复妖友...").create()
                    loading?.show()
                    viewModel.reply(
                        msg,
                        data.url,
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
}
