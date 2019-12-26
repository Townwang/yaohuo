package com.townwang.yaohuo.ui.fragment.list

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.ui.activity.ActivityDetails
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_list_data.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PubListFragment(private  val classId: Int,private val bbsTitle:String) : Fragment() {
    private val adapter = ListAdapter()
    private val viewModel: ListModel by viewModel()

    private var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = bbsTitle
                setDisplayHomeAsUpEnabled(true)
            }
        }
        homeList.adapter = adapter
        homeList.layoutManager =
            (StaggeredGridLayoutManager(
                config(HOME_LIST_THEME_SHOW).toInt(),
                StaggeredGridLayoutManager.VERTICAL
            ))
        adapter.onItemClickListener = { v, data ->
            val d = data as HomeData
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity!!, v.title, "share name"
            ).toBundle()
            ActivityCompat.startActivity(
                context!!, Intent(
                    context!!, ActivityDetails::class.java
                ).apply {
                    putExtra(HOME_DETAILS_URL_KEY, d.a)
                    putExtra(HOME_DETAILS_READ_KEY, d.read)
                }, bundle
            )
        }
        refreshLayout.setOnRefreshListener {
            page = 1
            viewModel.loadList(classId, page)
        }
        refreshLayout.setOnLoadMoreListener {
            page++
            viewModel.loadList(classId, page)
        }
        refreshLayout.autoRefresh()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.listDates.observe(this, Observer {
            it ?: return@Observer
            if (page == 1) {
                adapter.datas.clear()
            }
            adapter.datas = it as ArrayList<HomeData>
        })
        viewModel.loading.observe(this, safeObserver {
            if (!it) {
                if (page == 1) {
                    refreshLayout.finishRefresh()
                }else {
                    refreshLayout.finishLoadMore()
                }
            }
        })
        viewModel.error.observe(this, safeObserver {
            context?.handleException(it)
        })
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