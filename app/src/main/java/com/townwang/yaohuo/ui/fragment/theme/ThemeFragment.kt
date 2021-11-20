package com.townwang.yaohuo.ui.fragment.theme

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.townwang.yaohuo.R
import com.townwang.yaohuoapi.THEME_KEY
import com.townwang.yaohuoapi.manager.config
import com.townwang.yaohuo.common.reload
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.databinding.FragmentThemeBinding
import com.townwang.yaohuo.repo.data.ThemeList
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import org.koin.androidx.viewmodel.ext.android.viewModel

class ThemeFragment : Fragment(R.layout.fragment_theme) {
    private val adapter = ThemeAdapter()
    private val viewModel: ThemeModel by viewModel()
    val binding: FragmentThemeBinding by viewbind()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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