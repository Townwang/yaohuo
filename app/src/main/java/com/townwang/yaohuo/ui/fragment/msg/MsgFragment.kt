package com.townwang.yaohuo.ui.fragment.msg

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.handleException
import com.townwang.yaohuo.common.safeObserver
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.databinding.FragmentMsgBinding
import com.townwang.yaohuo.repo.data.MsgBean
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import org.koin.androidx.viewmodel.ext.android.viewModel

class MsgFragment : Fragment(R.layout.fragment_msg) {
    val binding: FragmentMsgBinding by viewbind()
    private val adapter = MsgAdapter()
    private val model: MsgModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = "消息"
                setDisplayHomeAsUpEnabled(true)
            }
        }
        binding.listView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener {
            model.loadList(true)
        }
        binding.refreshLayout.setOnLoadMoreListener {
            model.loadList(false)
        }
        binding.refreshLayout.setEnableAutoLoadMore(true)
        binding.refreshLayout.autoRefresh()
        adapter.onDeleteListener = { _, pro ->
            if (pro is Product) {
                val data = pro.t
                if (data is MsgBean) {
                    model.deleteMsg(data.deleteUrl,pro)
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        model.liveData.observe(viewLifecycleOwner, safeObserver {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        model.noData.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                binding.refreshLayout.finishLoadMoreWithNoMoreData()
            }
        })
        model.loading.observe(viewLifecycleOwner, safeObserver {
            if (it.not()) {
                binding.refreshLayout.finishRefresh()
                binding.refreshLayout.finishLoadMore()
            }
        })
        model.error.observe(viewLifecycleOwner, safeObserver {
            requireContext().handleException(it)
        })
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

}