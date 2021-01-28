package com.townwang.yaohuo.ui.fragment.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.fragment_me.refreshLayout
import kotlinx.android.synthetic.main.fragment_me.title
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
        refreshLayout?.setOnRefreshListener {
            viewModel.getMeData()
        }
        refreshLayout?.autoRefresh()
        refreshLayout?.setEnableLoadMore(false)
        refreshLayout?.setEnableNestedScroll(true)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (!it) {
                refreshLayout?.finishRefresh(true)
                nesScroll.visibility = View.VISIBLE
            }
        })
        viewModel.data.observe(viewLifecycleOwner, safeObserver {
            nikeName?.text = it.nikeName
            accountNumber?.value = it.accountNumber
            money?.value = it.money
            bankSavings?.value = it.bankSavings
            experience?.value = it.experience
            rank?.value = it.rank
            title?.value = it.title
            identity?.value = it.identity
            managementAuthority?.value = it.managementAuthority
        })
        viewModel.avatar.observe(viewLifecycleOwner, safeObserver {
            Glide.with(requireContext())
                .load(getUrlString(it))
                .apply(options)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(avatar)
        })

    }
}