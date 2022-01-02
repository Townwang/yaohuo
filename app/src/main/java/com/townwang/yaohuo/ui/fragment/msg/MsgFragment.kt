package com.townwang.yaohuo.ui.fragment.msg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.townwang.binding.ext.viewbind
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentMsgBinding
import com.townwang.yaohuo.repo.data.MsgBean
import com.townwang.yaohuo.ui.activity.ActivityMsgDetails
import com.townwang.yaohuoapi.HOME_DETAILS_TITLE_KEY
import com.townwang.yaohuoapi.HOME_DETAILS_URL_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel

class MsgFragment : Fragment(R.layout.fragment_msg) {
    val binding: FragmentMsgBinding by viewbind()
    private val adapter = MsgAdapter()
    private val model: MsgModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("ClickableViewAccessibility")
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
            model.loadList(true)
        }
        binding.refreshLayout.setOnLoadMoreListener {
            model.loadList(false)
        }
        binding.refreshLayout.setEnableAutoLoadMore(true)
        binding.refreshLayout.autoRefresh()
        binding.listView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE){
                binding.menuCrop.collapse()
            }
            false
        }
        binding.fabB1.onClickListener {
            Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        binding.fabB2.onClickListener {
            Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        binding.fabB3.onClickListener {
            Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        binding.fabB4.onClickListener {
            Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        adapter.onDeleteListener = { _, pro ->
            if (pro is Product) {
                val data = pro.t
                if (data is MsgBean) {
                    model.deleteMsg(data.deleteUrl, pro)
                }
            }
        }
        adapter.onItemListListener = { v, pro ->
            if (pro is Product) {
                val data = pro.t
                if (data is MsgBean) {
                    startActivity(
                        Intent(
                            requireContext(), ActivityMsgDetails::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            putExtra(HOME_DETAILS_URL_KEY, data.url)
                            putExtra(HOME_DETAILS_TITLE_KEY, data.msg)
                        }
                    )

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