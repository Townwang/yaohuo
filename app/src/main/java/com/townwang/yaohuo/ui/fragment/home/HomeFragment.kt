package com.townwang.yaohuo.ui.fragment.home

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.utils.isHaveMessage
import com.townwang.yaohuo.databinding.FragmentHomeBinding
import com.townwang.yaohuo.repo.data.HomeBean
import com.townwang.yaohuo.ui.activity.*
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    val binding: FragmentHomeBinding by viewbind()
    private val adapter = HomeAdapter()
    var page = 1
    private val model: HomeModel by viewModel()
    lateinit var request: ActivityResultLauncher<Intent>
    override fun onStart() {
        super.onStart()
       binding.marqueeView.startFlipping()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        request = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            requireActivity().work {
                reload(config(THEME_KEY))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.home_page)
                setDisplayHomeAsUpEnabled(false)
            }
        }
        binding.listView.adapter = adapter
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            model.refresh(BuildConfig.YH_BBS_ACTION_NEW)
            model.loadTipList(288, 0, BuildConfig.YH_BBS_ACTION_NEW)
        }
        binding.refreshLayout.setOnLoadMoreListener {
            page++
            model.loadList(0, page, BuildConfig.YH_BBS_ACTION_NEW)
        }
        binding.refreshLayout.autoRefresh()
        val ad = binding.image.drawable
        if (ad is AnimationDrawable) {
            if (!ad.isRunning) {
                ad.start()
            } else if (ad.isRunning) {
                ad.stop()
            }
        }
        adapter.onItemListListener = { _, pro ->
            if (pro is Product) {
                val data = pro.t
                if (data is HomeBean) {
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
        }
        adapter.onBBSListener = { classId, resId, action ->
            startActivity(Intent(
                requireContext(), ActivityList::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(LIST_CLASS_ID_KEY, classId)
                putExtra(LIST_BBS_NAME_KEY, getString(resId))
                putExtra(LIST_ACTION_KEY, action)
            }
            )
        }
        adapter.onSearchListener = { v, data ->
            if (data is String) {
                if (data.isEmpty()) {
                    if (v is View) {
                        Snackbar.make(
                            v,
                            getString(R.string.search_value_empty_tip),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    ActivityCompat.startActivity(
                        requireContext(), Intent(
                            requireContext(), ActivitySearch::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            putExtra(HOME_SEARCH_URL_KEY, data)
                        }, null
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
        model.tipData.observe(viewLifecycleOwner,safeObserver {
            val info: MutableList<String> = ArrayList()
            it.forEach { b ->
                info.add(b.title)
            }
            binding.marqueeView.startWithList(info)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_home, menu)
        if (isHaveMessage) {
            menu.findItem(R.id.toolbar_r_msg).setIcon(R.drawable.anim_vector_new_msg)
        } else {
            menu.findItem(R.id.toolbar_r_msg).setIcon(R.drawable.anim_vector_msg)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            R.id.toolbar_r_img -> {
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_setting).icon)
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_theme).icon)
                startAnimator(item.subMenu.findItem(R.id.toolbar_r_about).icon)
                true
            }
            R.id.toolbar_r_setting -> {
                Snackbar.make(requireView(), "正在开发...", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.toolbar_r_theme -> {
                request.launch(Intent(context, ActivityTheme::class.java))
                true
            }
            R.id.toolbar_r_about -> {
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), binding.listView, "share name"
                ).toBundle()
                ActivityCompat.startActivity(
                    requireContext(), Intent(
                        requireContext(), ActivityAbout::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    }, bundle
                )
                true
            }
            R.id.toolbar_r_msg -> {
                ActivityCompat.startActivity(
                    requireContext(), Intent(
                        requireContext(), ActivityMsg::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        putExtra(WEB_VIEW_URL_KEY, "https://yaohuo.me/bbs/messagelist.aspx")
                        putExtra(WEB_VIEW_URL_TITLE, "消息")
                    }, null
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.marqueeView.stopFlipping()
    }
    fun refreshData() {
        model.refresh(BuildConfig.YH_BBS_ACTION_NEW)
    }
}