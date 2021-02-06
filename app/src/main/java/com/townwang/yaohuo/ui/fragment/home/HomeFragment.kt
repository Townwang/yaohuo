package com.townwang.yaohuo.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.utils.isHaveMessage
import com.townwang.yaohuo.databinding.FragmentHomeBinding
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.ui.activity.*
import com.townwang.yaohuo.ui.fragment.bbs.BBSFragment
import com.townwang.yaohuo.ui.fragment.pub.PubListAdapter
import com.townwang.yaohuo.ui.weight.binding.databind.FragmentDataBinding
import com.townwang.yaohuo.ui.weight.binding.ext.databind
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    val binding: FragmentHomeBinding by viewbind()
//    private val adapter = PubListAdapter()
    private val adapter =  HomeAdapter()

    private val model: HomeModel by viewModel()
    lateinit var request: ActivityResultLauncher<Intent>
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
                setTitleCenter()
            }
        }
//        if (savedInstanceState == null) {
//            childFragmentManager.beginTransaction()
//                .replace(R.id.navBBSHost, BBSFragment())
//                .commit()
//        }
        binding.listView.adapter = adapter
//        binding.apply {
//            viewModel = model
//            adapter = HomeAdapter()
//        }
//        binding.homeList.adapter = adapter
//        binding.homeList.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        adapter.onItemClickListener = { v, data ->
//            if (data is HomeData) {
//                var isBear = true
//                data.smailIng.forEach {
//                    if (it == BuildConfig.YH_MATCH_LIST_BEAR) {
//                        isBear = false
//                        return@forEach
//                    }
//                }
//                startActivity(Intent(
//                    requireContext(), ActivityDetails::class.java
//                ).apply {
//                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                    putExtra(HOME_DETAILS_URL_KEY, data.a)
//                    putExtra(HOME_DETAILS_READ_KEY, data.read)
//                    putExtra(HOME_DETAILS_BEAR_KEY, isBear)
//                    putExtra(HOME_DETAILS_TITLE_KEY, data.title)
//                }
//                )
//            }
//        }
//        viewModel.loadList(0, 1, BuildConfig.YH_BBS_ACTION_NEW)
//        binding.noMore.onClickListener {
//            startActivity(Intent(
//                requireContext(), ActivityList::class.java
//            ).apply {
//                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                putExtra(LIST_CLASS_ID_KEY, 0)
//                putExtra(LIST_BBS_NAME_KEY, getString(R.string.home_news))
//                putExtra(LIST_ACTION_KEY, BuildConfig.YH_BBS_ACTION_NEW)
//            }
//            )
//        }
//        binding.searchValue.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                val searchValue = binding.searchValue.text.toString()
//                if (searchValue.isEmpty()) {
//                    Snackbar.make(
//                        v,
//                        getString(R.string.search_value_empty_tip),
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                } else {
//                    ActivityCompat.startActivity(
//                        requireContext(), Intent(
//                            requireContext(), ActivitySearch::class.java
//                        ).apply {
//                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                            putExtra(HOME_SEARCH_URL_KEY, searchValue)
//                        }, null
//                    )
//                }
//                return@setOnEditorActionListener true
//            }
//            return@setOnEditorActionListener false
//        }
//        binding.searchBtn.onClickListener {
//            val searchValue = binding.searchValue.text.toString()
//            if (searchValue.isEmpty()) {
//                Snackbar.make(
//                    it,
//                    getString(R.string.search_value_empty_tip),
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            } else {
//                ActivityCompat.startActivity(
//                    requireContext(), Intent(
//                        requireContext(), ActivitySearch::class.java
//                    ).apply {
//                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                        putExtra(HOME_SEARCH_URL_KEY, searchValue)
//                    }, null
//                )
//            }
//        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        model.liveData.observeOnce(viewLifecycleOwner, safeObserver {
            adapter.submitList(it)
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
                        requireContext(), ActivityWebView::class.java
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

    fun refreshData() {
        model.loadList(0, 1, BuildConfig.YH_BBS_ACTION_NEW)
    }
}