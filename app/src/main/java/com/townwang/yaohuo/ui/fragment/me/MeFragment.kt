package com.townwang.yaohuo.ui.fragment.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.safeObserver
import com.townwang.yaohuo.common.setTitleCenter
import com.townwang.yaohuo.common.work
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.fragment_me.*
import org.koin.androidx.viewmodel.ext.android.viewModel
class MeFragment : Fragment() {
    private val viewModel: MeModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.home_me)
                setDisplayHomeAsUpEnabled(false)
            }
            setTitleCenter(toolbar)
        }
        refreshLayout.setOnRefreshListener {
            viewModel.getMeData()
        }
        refreshLayout.autoRefresh()
        refreshLayout.setEnableLoadMore(false)
        refreshLayout.setEnableNestedScroll(true)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (!it) {
                refreshLayout.finishRefresh(true)
            }
        })
        viewModel.nikeName.observe(viewLifecycleOwner, safeObserver {
            nikeName.text = it
        })
        viewModel.accountNumber.observe(viewLifecycleOwner, safeObserver {
            accountNumber.value = it
        })
        viewModel.money.observe(viewLifecycleOwner, safeObserver {
            money.value = it
        })
        viewModel.bankSavings.observe(viewLifecycleOwner, safeObserver {
            bankSavings.value = it
        })
        viewModel.experience.observe(viewLifecycleOwner, safeObserver {
            experience.value = it
        })
        viewModel.rank.observe(viewLifecycleOwner, safeObserver {
            rank.value = it
        })
        viewModel.title.observe(viewLifecycleOwner, safeObserver {
            title.value = it
        })
        viewModel.identity.observe(viewLifecycleOwner, safeObserver {
            identity.value = it
        })
        viewModel.managementAuthority.observe(viewLifecycleOwner, safeObserver {
            managementAuthority.value = it
        })
    }
}