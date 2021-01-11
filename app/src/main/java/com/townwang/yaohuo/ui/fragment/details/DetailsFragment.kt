package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.tu.loadingdialog.LoadingDailog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.CommentData
import com.townwang.yaohuo.ui.activity.ActivityWebView
import com.townwang.yaohuo.ui.fragment.web.WebViewHelper
import com.townwang.yaohuo.ui.weight.commit.CommentDialogFragment
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details.refreshLayout
import kotlinx.android.synthetic.main.view_download_style.view.*
import kotlinx.android.synthetic.main.view_image_style.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailsFragment(private val url: String, private val read: String) : Fragment() {
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
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.list_details)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        refreshLayout.setOnRefreshListener {
            page = 1
            ot = 0
            adapter.datas.clear()
            viewModel.commentDetails(page, ot)
        }
        refreshLayout.setOnLoadMoreListener {
            if (adapter.datas.isNullOrEmpty()) {
                refreshLayout.finishLoadMoreWithNoMoreData()
                return@setOnLoadMoreListener
            }
            if (adapter.datas.last().floor == 1) {
                refreshLayout.finishLoadMoreWithNoMoreData()
                return@setOnLoadMoreListener
            }
            page++
            viewModel.commentDetails(page, ot)
        }
        attention.setOnClickListener {
            Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        reply.setOnClickListener {
            val mfragTransaction = parentFragmentManager.beginTransaction()
            val fragment = parentFragmentManager.findFragmentByTag("input frag")
            if (fragment != null) {
                mfragTransaction.remove(fragment)
            }
            val dialogFragment =
                CommentDialogFragment("请不要乱打字回复，以免被加黑。")
            dialogFragment.mDialogListener = { _, message ->
                loading = Loading("正在提交...").create()
                loading?.show()
                viewModel.reply(message, url)
                dialogFragment.dismiss()
            }
            dialogFragment.show(parentFragmentManager, "input frag")
        }
        comment.setOnClickListener {
            nesScroll.post {
                nesScroll.scrollTo(0, commentLists.top)
            }
        }
        praise.setOnClickListener {
            praise_value.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_praise_grey),
                null,
                null,
                null
            )
            praise.isClickable = false
            praise_value.text = (praise_value.text.toString().toInt() + 1).toString()
            viewModel.praise()
        }
        commentLists.adapter = adapter
        commentLists.layoutManager =
            (StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            ))

        adapter.onItemClickListener = { v, d ->
            val data = d as CommentData
            if (getParam(d.url, "touserid") != config(TROUSER_KEY)) {
                val magTransaction = childFragmentManager.beginTransaction()
                val fragment = childFragmentManager.findFragmentByTag("input frag")
                if (fragment != null) {
                    magTransaction.remove(fragment)
                }
                val dialogFragment = CommentDialogFragment("回复：${data.auth}")
                dialogFragment.mDialogListener = { _, msg ->
                    loading = Loading("正在提交...").create()
                    loading?.show()
                    viewModel.reply(msg, d.url, d.floor.toString(), getParam(d.url, "touserid"))
                    dialogFragment.dismiss()
                }
                dialogFragment.show(childFragmentManager, "input frag")
            } else {
                context?.toast("不能给自己回复！")
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.title.observe(viewLifecycleOwner, safeObserver {
            title.text = it
            title.visibility = View.VISIBLE
        })
        viewModel.time.observe(viewLifecycleOwner, safeObserver {
            time.text = it
            read_num.visibility = View.VISIBLE
            read_num.text = read.split(" ").first()
        })
        viewModel.name.observe(viewLifecycleOwner, safeObserver {
            constraintLayout.visibility = View.VISIBLE
            userName.text = it
        })
        viewModel.online.observe(viewLifecycleOwner, safeObserver {
            constraintLayout.visibility = View.VISIBLE
            if (it) {
                online.text = "在线"
                online.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_blue_10)
            } else {
                online.text = "离线"
                online.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_grey_10)
            }
        })
        viewModel.giftMoney.observe(viewLifecycleOwner, safeObserver {
            //肉
            linearTop.visibility = View.VISIBLE
            icon.text = "肉"
            icon.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
            subtitle.text = it
        })
        viewModel.reward.observe(viewLifecycleOwner, safeObserver {
            //悬赏
            linearTop.visibility = View.VISIBLE
            icon.text = "赏"
            icon.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
            subtitle.text = it
        })
        viewModel.content.observe(viewLifecycleOwner, safeObserver {
            list_content.addView(WebViewHelper(requireContext()).apply {
                shouldOverrideUrlLoading = true
            }.setHtmlCode(it))
        })
        viewModel.image.observe(viewLifecycleOwner, safeObserver {
            val contentImg =
                LayoutInflater.from(requireContext()).inflate(R.layout.view_image_style, null)
            list_content.addView(contentImg)
            Glide.with(requireContext())
                .load(getUrlString(it))
                .apply(RequestOptions.noTransformation())
                .into(contentImg.image)
        })
        viewModel.avatar.observe(viewLifecycleOwner, safeObserver {
            Glide.with(requireContext())
                .load(getUrlString(it))
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(userImg)
        })
        viewModel.download.observe(viewLifecycleOwner, safeObserver {
            val contentLoad =
                LayoutInflater.from(requireContext()).inflate(R.layout.view_download_style, null)
            contentLoad.downloadName.text = it[0].replace("\n", "")

            contentLoad.downloadUrl.setOnClickListener { _ ->
                val uri = Uri.parse(Uri.encode(it[1], "-![.:/,%?&=]"))
                ActivityCompat.startActivity(
                    requireContext(), Intent(
                        requireContext(), ActivityWebView::class.java
                    ).apply {
                        putExtra(WEB_VIEW_URL_KEY,uri.toString())
                        putExtra(WEB_VIEW_URL_TITLE,title.text.toString())
                    },null)
            }
            list_content.addView(contentLoad)
        })
        viewModel.commentPraise.observe(viewLifecycleOwner, safeObserver {
            praise_value.text = it
        })
        viewModel.commentSize.observe(viewLifecycleOwner, safeObserver {
            if (page == 1 && ot == 0) {
                comment_value.text = it
            }
        })
        viewModel.commentLists.observe(viewLifecycleOwner, safeObserver {
            comment_tip.visibility = View.VISIBLE
            view_grey.visibility = View.VISIBLE
            @Suppress("UNCHECKED_CAST")
            adapter.datas = it as ArrayList<CommentData>
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
        viewModel.getDetails(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshDone(success: Boolean) {
        refreshLayout?:return
        if (page == 1) {
            refreshLayout.finishRefresh(success)
        } else {
            refreshLayout.finishLoadMore(success)
        }
    }
}