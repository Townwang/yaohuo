package com.townwang.yaohuo.ui.fragment.search

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentSearchBinding
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.repo.enum.ErrorCode
import com.townwang.yaohuo.ui.activity.ActivityDetails
import com.townwang.yaohuo.ui.fragment.pub.PubListAdapter
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val adapter = PubListAdapter()
    private val viewModel: SearchModel by viewModel()
    val binding: FragmentSearchBinding by viewbind()
    private var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = requireArguments().getString(HOME_SEARCH_URL_KEY, "")
            }
        }
        binding.homeList.adapter = adapter
        binding.homeList.layoutManager =
            (StaggeredGridLayoutManager(
                requireContext().config(HOME_LIST_THEME_SHOW).toInt(),
                StaggeredGridLayoutManager.VERTICAL
            ))
        adapter.onItemClickListener = { v, data ->
            if (data is HomeData) {
                var isBear = true
                data.smailIng.forEach {
                    if (it == BuildConfig.YH_MATCH_LIST_BEAR) {
                        isBear = false
                        return@forEach
                    }
                }
                startActivity(Intent(
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
            binding.noMore.visibility = View.GONE
            binding.homeList.visibility = View.VISIBLE
            page = 1
            viewModel.loadList(requireArguments().getString(HOME_SEARCH_URL_KEY, ""), page)
        }
        binding.refreshLayout.setOnLoadMoreListener {
            page++
            viewModel.loadList(requireArguments().getString(HOME_SEARCH_URL_KEY, ""), page)
        }
        binding.refreshLayout.autoRefresh()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.listDates.observe(viewLifecycleOwner, safeObserver {
            if (page == 1) {
                adapter.datas.clear()
            }
            adapter.datas = it as ArrayList<HomeData>
        })
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (!it) {
                refreshDone(true)
            }
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            if (it is ApiErrorException) {
                if (it.code == ErrorCode.E_1008.hashCode()) {
                    binding.noMore.visibility = View.VISIBLE
                    binding.homeList.visibility = View.GONE
                }
            }
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