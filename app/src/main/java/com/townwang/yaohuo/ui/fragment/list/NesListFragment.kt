package com.townwang.yaohuo.ui.fragment.list

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.ui.activity.ActivityAbout
import com.townwang.yaohuo.ui.activity.ActivityTheme
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.ui.activity.ActivityDetails
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.item_list_data.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class NesListFragment : Fragment() {
    private val adapter = ListAdapter()
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
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.home_news)
                setDisplayHomeAsUpEnabled(false)
            }
            setTitleCenter(toolbar)
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
            viewModel.loadList(0, page)
        }
        refreshLayout.setOnLoadMoreListener {
            page++
            viewModel.loadList(0, page)
        }
        refreshLayout.autoRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_home, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.listDates.observe(this, Observer {
            it ?: return@Observer
            if (page == 1) {
                refreshLayout.finishRefresh()
            } else {
                refreshLayout.finishLoadMore()
            }
            if (page == 1) {
                adapter.datas.clear()
            }

            adapter.datas = it as ArrayList<HomeData>
        })
        viewModel.loading.observe(this, safeObserver {
            if (!it) {
                if (page == 1) {
                    refreshLayout.finishRefresh()
                } else {
                    refreshLayout.finishLoadMore()
                }
            }
        })
        viewModel.error.observe(this, safeObserver {
            context?.handleException(it)
        })
        viewModel.isMessage.observe(this, safeObserver {
            it ?: return@safeObserver
            if (it) {
//                context?.toast("你有一个消息")
            } else {

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            R.id.toolbar_r_img -> {
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_setting).icon)
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_theme).icon)
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_about).icon)
                true
            }
            R.id.toolbar_r_setting -> {
                Snackbar.make(view!!, "正在开发...", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.toolbar_r_theme -> {
                startActivityForResult(
                    Intent(context, ActivityTheme::class.java)
                    , Activity.RESULT_CANCELED
                )
                true
            }
            R.id.toolbar_r_about -> {
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!, homeList, "share name"
                ).toBundle()
                ActivityCompat.startActivity(
                    context!!, Intent(
                        context!!, ActivityAbout::class.java
                    ), bundle
                )
                true
            }
            R.id.toolbar_r_msg -> {
                val uri = Uri.parse("https://yaohuo.me/bbs/messagelist.aspx")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activity?.work {
            reload(config(THEME_KEY))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}