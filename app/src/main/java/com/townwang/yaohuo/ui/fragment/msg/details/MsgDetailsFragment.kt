package com.townwang.yaohuo.ui.fragment.msg.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.townwang.binding.ext.viewbind
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentMsgDetailsBinding
import com.townwang.yaohuoapi.HOME_DETAILS_URL_KEY
import com.townwang.yaohuoapi.TROUSER_KEY
import com.townwang.yaohuoapi.manager.config
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class MsgDetailsFragment : Fragment(R.layout.fragment_msg_details) {
    val binding: FragmentMsgDetailsBinding by viewbind()
    var loading: LoadingDialog? = null
    private val adapter = MsgDetailsAdapter()
    val viewModel: MsgDetailsModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.msg)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        binding.listView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener {
            viewModel.getMsgDetails(
                requireArguments().getString(HOME_DETAILS_URL_KEY, ""),
                requireContext().config(TROUSER_KEY)
            )
        }
        binding.refreshLayout.setEnableLoadMore(false)
        binding.refreshLayout.autoRefresh()
        binding.listView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (adapter.itemCount > 0) {
                binding.listView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }
        binding.sendMsg.onClickListener {
            loading = Loading("正在发送消息...").apply {
                setSuccessText("发送成功")
                setFailedText("发送失败")
            }
            loading?.show()
            viewModel.senMsg(binding.msgContent.text.toString())
        }
        adapter.onItemListListener = { _, pro ->
            if (pro is Product) {
                val data = pro.t

            }
        }

    }

    @SuppressLint("InflateParams")
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.listDates.observe(viewLifecycleOwner, safeObserver {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (it.not()) {
                binding.refreshLayout.finishRefresh()
                binding.refreshLayout.finishLoadMore()
            }
        })
        viewModel.isSuccess.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                binding.msgContent.setText("")
                viewModel.getMsgDetails(
                    requireArguments().getString(HOME_DETAILS_URL_KEY, ""),
                    requireContext().config(TROUSER_KEY)
                )
                loading?.loadSuccess()
            }
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            loading?.setFailedText(it.message)
            loading?.loadFailed()
            requireContext().handleException(it)
        })
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

    override fun onDestroy() {
        loading?.run {
            close()
        }
        super.onDestroy()
    }
}
