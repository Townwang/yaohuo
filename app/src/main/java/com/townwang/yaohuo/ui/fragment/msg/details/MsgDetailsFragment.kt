package com.townwang.yaohuo.ui.fragment.msg.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.HOME_DETAILS_URL_KEY
import com.townwang.yaohuo.common.handleException
import com.townwang.yaohuo.common.safeObserver
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.databinding.FragmentMsgDetailsBinding
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class MsgDetailsFragment : Fragment(R.layout.fragment_msg_details) {
    val binding: FragmentMsgDetailsBinding by viewbind()
    var loading: LoadingDialog? = null
    private val adapter = MsgDetailsAdapter()
    val viewModel: MsgDetailsModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
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
            viewModel.getMsgDetails(requireArguments().getString(HOME_DETAILS_URL_KEY,""))
        }
        binding.refreshLayout.setNoMoreData(false)
        binding.refreshLayout.autoRefresh()
        adapter.onItemListListener = { _, pro ->
            if (pro is Product) {
                val data = pro.t

            }
        }

    }

    @SuppressLint("InflateParams")
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.listDates.observe(viewLifecycleOwner, safeObserver {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (it.not()) {
                binding.refreshLayout.finishRefresh()
                binding.refreshLayout.finishLoadMore()
            }
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            requireContext().handleException(it)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        loading?.run {
            close()
        }
        super.onDestroy()
    }
}
