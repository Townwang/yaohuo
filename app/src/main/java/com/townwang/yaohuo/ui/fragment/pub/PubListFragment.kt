package com.townwang.yaohuo.ui.fragment.pub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.helper.sendNotification
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.ui.activity.ActivityDetails
import kotlinx.android.synthetic.main.fragment_list_pub.*
import kotlinx.android.synthetic.main.item_list_data.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PubListFragment : Fragment() {
    private val adapter = PubListAdapter()
    private val viewModel: ListModel by viewModel()

    private var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_pub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = requireArguments().getString(LIST_BBS_NAME_KEY, "")
                setDisplayHomeAsUpEnabled(requireArguments().getInt(LIST_CLASS_ID_KEY, 0) != 0)
            }
        }
        homeList?.adapter = adapter
        homeList?.layoutManager =
            (StaggeredGridLayoutManager(
               requireContext().config(HOME_LIST_THEME_SHOW).toInt(),
                StaggeredGridLayoutManager.VERTICAL
            ))
        adapter.onItemClickListener = { v, data ->
            if (data is HomeData) {
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), v.title, "share name"
                ).toBundle()
                var isBear = true
                data.smailIng.forEach {
                    if (it == BuildConfig.YH_MATCH_LIST_BEAR){
                        isBear = false
                        return@forEach
                    }
                }
                ActivityCompat.startActivity(
                    requireContext(), Intent(
                        requireContext(), ActivityDetails::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        putExtra(HOME_DETAILS_URL_KEY, data.a)
                        putExtra(HOME_DETAILS_READ_KEY, data.read)
                        putExtra(HOME_DETAILS_BEAR_KEY, isBear)
                    }, bundle
                )
            }
        }
        refreshLayout?.setOnRefreshListener {
            page = 1
            viewModel.loadList(requireArguments().getInt(LIST_CLASS_ID_KEY), page,requireArguments().getString(LIST_ACTION_KEY,"new"))
        }
        refreshLayout?.setOnLoadMoreListener {
            page++
            viewModel.loadList(requireArguments().getInt(LIST_CLASS_ID_KEY), page,requireArguments().getString(LIST_ACTION_KEY,"new"))
        }
        refreshLayout?.autoRefresh()
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
        refreshLayout ?: return
        if (page == 1) {
            refreshLayout.finishRefresh(success)
        } else {
            refreshLayout.finishLoadMore(success)
        }
    }
}