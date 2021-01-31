package com.townwang.yaohuo.ui.fragment.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentMeBinding
import com.townwang.yaohuo.ui.fragment.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MeFragment : BaseFragment() {
    private val binding get() = _binding!! as FragmentMeBinding
    private val viewModel: MeModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.home_me)
                setDisplayHomeAsUpEnabled(false)
                setTitleCenter()
            }
        }
        binding.refreshLayout.setOnRefreshListener {
            viewModel.getMeData()
        }
        binding.refreshLayout.autoRefresh()
        binding.refreshLayout.setEnableLoadMore(false)
        binding.refreshLayout.setEnableNestedScroll(true)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (!it) {
                binding.refreshLayout.finishRefresh(true)
                binding.nesScroll.visibility = View.VISIBLE
            }
        })
        viewModel.data.observe(viewLifecycleOwner, safeObserver {
            binding.nikeName.text = it.nikeName
            binding.accountNumber.value = it.accountNumber
            binding.money.value = it.money
            binding.bankSavings.value = it.bankSavings
            binding.experience.value = it.experience
            binding.rank.value = it.rank
            binding.title.value = it.title
            binding.identity.value = it.identity
            binding.managementAuthority.value = it.managementAuthority
        })
        viewModel.avatar.observe(viewLifecycleOwner, safeObserver {
            Glide.with(requireContext())
                .load(getUrlString(it))
                .apply(options)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.avatar)
        })

    }
}