package com.townwang.yaohuo.ui.fragment.pub

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentListPubBinding
import com.townwang.yaohuo.repo.data.HomeBean
import com.townwang.yaohuo.ui.activity.ActivityDetails
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.townwang.yaohuoapi.*
import com.townwang.yaohuoapi.BuildConfig.YH_MATCH_LIST_BEAR
import org.koin.androidx.viewmodel.ext.android.viewModel


class PubListFragment : Fragment(R.layout.fragment_list_pub) {
    private val adapter = PubListAdapter()
    private val viewModel: PubListModel by viewModel()
    val binding: FragmentListPubBinding by viewbind()
    private var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = requireArguments().getString(LIST_BBS_NAME_KEY, "")
                setDisplayHomeAsUpEnabled(true)
            }
        }
        binding.homeList.adapter = adapter
        binding.homeList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.onItemClickListener = { v, data ->
            if (data is HomeBean) {
                var isBear = true
                data.smailIng.forEach {
                    if (it == YH_MATCH_LIST_BEAR) {
                        isBear = false
                        return@forEach
                    }
                }
                startActivity( Intent(
                        requireContext(), ActivityDetails::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        putExtra(HOME_DETAILS_URL_KEY, data.a)
                        putExtra(HOME_DETAILS_READ_KEY, data.read)
                        putExtra(HOME_DETAILS_BEAR_KEY, isBear)
                        putExtra(HOME_DETAILS_TITLE_KEY, data.title)
                    }
                )
            }
        }
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            viewModel.loadList(
                requireArguments().getInt(LIST_CLASS_ID_KEY, 0),
                page,
                requireArguments().getString(LIST_ACTION_KEY, "new")
            )
        }
        binding.refreshLayout.setOnLoadMoreListener {
            page++
            viewModel.loadList(
                requireArguments().getInt(LIST_CLASS_ID_KEY, 0),
                page,
                requireArguments().getString(LIST_ACTION_KEY, "new")
            )
        }
        binding.refreshLayout.setEnableAutoLoadMore(true)
        binding.refreshLayout.autoRefresh()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.listDates.observe(viewLifecycleOwner, safeObserver {
            if (page == 1) {
                adapter.datas.clear()
            }
            adapter.datas = it as ArrayList<HomeBean>
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
        if (page == 1) {
            binding.refreshLayout.finishRefresh(success)
        } else {
            binding.refreshLayout.finishLoadMore(success)
        }
    }
}