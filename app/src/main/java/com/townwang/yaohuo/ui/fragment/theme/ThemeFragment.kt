package com.townwang.yaohuo.ui.fragment.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.THEME_KEY
import com.townwang.yaohuo.common.config
import com.townwang.yaohuo.common.reload
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.databinding.FragmentThemeBinding
import com.townwang.yaohuo.repo.data.ThemeList
import com.townwang.yaohuo.ui.fragment.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ThemeFragment : BaseFragment() {
    private val binding get() = _binding!! as FragmentThemeBinding
    private val adapter = ThemeAdapter()
    private val viewModel: ThemeModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentThemeBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.theme)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        binding.themeList.adapter = adapter
        binding.themeList.layoutManager = LinearLayoutManager(context)
        adapter.onItemClickListener = { _, data ->
            requireActivity().work {
                reload(config(THEME_KEY,(data as ThemeList).id.toString()))
            }
        }
        adapter.datas = viewModel.listDates
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

}