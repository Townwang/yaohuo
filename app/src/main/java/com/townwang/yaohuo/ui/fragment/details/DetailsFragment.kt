package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.droidlover.xrichtext.XRichText
import com.android.tu.loadingdialog.LoadingDailog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.CommentData
import com.townwang.yaohuo.ui.weight.CommentDialogFragment
import kotlinx.android.synthetic.main.fragment_details.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            Snackbar.make(view, "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        reply.setOnClickListener {
            val mfragTransaction = parentFragmentManager.beginTransaction()
            val fragment = parentFragmentManager.findFragmentByTag("input frag")
            if (fragment != null) {
                mfragTransaction.remove(fragment)
            }
            val dialogFragment = CommentDialogFragment.newInstance("请不要乱打字回复，以免被加黑。")
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
            if (getParam(d.url, "touserid") != viewModel.touserid){
                val mfragTransaction = parentFragmentManager.beginTransaction()
                val fragment = parentFragmentManager.findFragmentByTag("input frag")
                if (fragment != null) {
                    mfragTransaction.remove(fragment)
                }
                val dialogFragment = CommentDialogFragment.newInstance("回复：${data.auth}")
                dialogFragment.mDialogListener = { _, msg ->
                    loading = Loading("正在提交...").create()
                    loading?.show()
                    viewModel.reply(msg, d.url, d.floor.toString(), getParam(d.url, "touserid"))
                    dialogFragment.dismiss()
                }
                dialogFragment.show(parentFragmentManager, "input frag")
            }else{
                context?.toast("不能给自己回复！")
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.title.observe(requireActivity(), Observer {
            it ?: return@Observer
            title.text = it
            title.visibility = View.VISIBLE
        })
        viewModel.time.observe(requireActivity(), Observer {
            it ?: return@Observer
            time.text = it
            read_num.visibility = View.VISIBLE
            read_num.text = read.split(" ").first()
        })
        viewModel.name.observe(requireActivity(), Observer {
            it ?: return@Observer
            constraintLayout.visibility = View.VISIBLE
            userName.text = it
        })
        viewModel.online.observe(requireActivity(), Observer {
            it ?: return@Observer
            constraintLayout.visibility = View.VISIBLE
            if (it) {
                online.text = "在线"
                online.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_blue_10)
            } else {
                online.text = "离线"
                online.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_grey_10)
            }
        })
        viewModel.giftMoney.observe(requireActivity(), Observer {
            it ?: return@Observer
            //肉
            linearTop.visibility = View.VISIBLE
            icon.text = "肉"
            icon.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
            subtitle.text = it
        })
        viewModel.reward.observe(requireActivity(), Observer {
            it ?: return@Observer
            //悬赏
            linearTop.visibility = View.VISIBLE
            icon.text = "赏"
            icon.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_yellow_10)
            subtitle.text = it
        })
        viewModel.content.observe(requireActivity(), Observer {
            it ?: return@Observer
            val textView = XRichText(context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.layoutParams = params
            textView.setTextIsSelectable(true)
            textView.textSize = 18.0f
            textView.callback(object : XRichText.BaseClickCallback() {
                override fun onLinkClick(url: String?): Boolean {
                    url ?: return true
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                    return true
                }
                override fun onImageClick(urlList: MutableList<String>?, position: Int) {
                    super.onImageClick(urlList, position)
                    context?.toast("图片而已，别瞎几把点了")
                }
            }).imageDownloader { url ->
                Glide.with(requireContext())
                    .asBitmap()
                    .load(BuildConfig.BASE_YAOHUO_URL+url)
                    .submit().get()
            }.text(it)
            list_content.addView(textView)
        })
        viewModel.image.observe(requireActivity(), Observer {
            it ?: return@Observer
            val contentImg = LayoutInflater.from(requireContext()).inflate(R.layout.view_image_style, null)
            list_content.addView(contentImg)
            Glide.with(requireContext())
                .load(BuildConfig.BASE_YAOHUO_URL+it)
                .apply(RequestOptions.noTransformation())
                .into(contentImg.image)
        })
        viewModel.avatar.observe(requireActivity(), Observer {
            it ?: return@Observer
            Glide.with(requireContext())
                .load(BuildConfig.BASE_YAOHUO_URL+it)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(userImg)
        })
        viewModel.download.observe(requireActivity(), Observer {
            it ?: return@Observer
            val contentLoad = LayoutInflater.from(requireContext()).inflate(R.layout.view_download_style, null)
            contentLoad.downloadName.text = it[0].replace("\n", "")
            contentLoad.downloadUrl.setOnClickListener { _ ->
                val uri = Uri.parse(Uri.encode(it[1], "-![.:/,%?&=]"))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            list_content.addView(contentLoad)
        })
        viewModel.commentPraise.observe(requireActivity(), Observer {
            it ?: return@Observer
            praise_value.text = it
        })
        viewModel.commentSize.observe(requireActivity(), Observer {
            it ?: return@Observer
            if (page == 1 && ot == 0) {
                comment_value.text = it
            }
        })
        viewModel.commentLists.observe(requireActivity(), Observer {
            it ?: return@Observer
            comment_tip.visibility = View.VISIBLE
            view_grey.visibility = View.VISIBLE
            @Suppress("UNCHECKED_CAST")
            adapter.datas = it as ArrayList<CommentData>
        })
        viewModel.loading.observe(requireActivity(), safeObserver {
            it ?: return@safeObserver
            if (!it) {
                if (page == 1) {
                    refreshLayout.finishRefresh()
                } else {
                    refreshLayout.finishLoadMore()
                }
            }
        })
        viewModel.error.observe(requireActivity(), safeObserver {
            it ?: return@safeObserver
            context?.handleException(it)
        })
        viewModel.commentSuccess.observe(requireActivity(), safeObserver {
            it ?: return@safeObserver
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
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}