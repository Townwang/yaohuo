//package com.townwang.yaohuo.ui.fragment.test
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.townwang.yaohuo.R
//import com.townwang.yaohuo.common.*
//import kotlinx.android.synthetic.main.fragment_list_pub.*
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//
//class newPubListFragment : Fragment() {
//    private val adapter = VTHistoryListAdapter()
//    private val viewModel: newListModel by viewModel()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_list_pub, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        (activity as AppCompatActivity).work {
//            supportActionBar.work {
//                title = requireArguments().getString(LIST_BBS_NAME_KEY, "")
//                setDisplayHomeAsUpEnabled(requireArguments().getInt(LIST_CLASS_ID_KEY, 0) != 0)
//            }
//        }
//        homeList.adapter  = adapter
////        homeList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
//        refreshLayout.setOnRefreshListener {
//            refreshLayout?.autoRefresh()
//        }
////        viewModel.loadList(
////            requireArguments().getInt(LIST_CLASS_ID_KEY),
////            requireArguments().getString(LIST_ACTION_KEY, "new")
////        )
////        adapter.onItemClickListener = { v, data ->
////            if (data is HomeData) {
////                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
////                    requireActivity(), v.title, "share name"
////                ).toBundle()
////                var isBear = true
////                data.smailIng.forEach {
////                    if (it == BuildConfig.YH_MATCH_LIST_BEAR){
////                        isBear = false
////                        return@forEach
////                    }
////                }
////                ActivityCompat.startActivity(
////                    requireContext(), Intent(
////                        requireContext(), ActivityDetails::class.java
////                    ).apply {
////                        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
////                        putExtra(HOME_DETAILS_URL_KEY, data.a)
////                        putExtra(HOME_DETAILS_READ_KEY, data.read)
////                        putExtra(HOME_DETAILS_BEAR_KEY, isBear)
////                        putExtra(HOME_DETAILS_TITLE_KEY, data.title)
////                    }, bundle
////                )
////            }
////        }
//
//
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        viewModel.history.observe(viewLifecycleOwner, safeObserver {
//            it ?: return@safeObserver
////            if (it.isEmpty()) {
////                showEmpty()
////            } else {
////                showContent()
//            refreshLayout?.finishRefresh()
//            adapter.submitList(it)
////            }
//        })
//
//        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
//            if (!it) {
//                refreshDone(true)
//            }
//        })
//        viewModel.error.observe(viewLifecycleOwner, safeObserver {
//            refreshDone(false)
//            requireContext().handleException(it)
//        })
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                requireActivity().onBackPressed()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private fun refreshDone(success: Boolean) {
//        refreshLayout ?: return
//        refreshLayout.finishLoadMore(success)
//    }
//}