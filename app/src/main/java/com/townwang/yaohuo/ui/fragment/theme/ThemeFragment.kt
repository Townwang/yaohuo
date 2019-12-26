package com.townwang.yaohuo.ui.fragment.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.THEME_KEY
import com.townwang.yaohuo.common.config
import com.townwang.yaohuo.common.reload
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.repo.data.ThemeList
import kotlinx.android.synthetic.main.fragment_theme.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ThemeFragment : Fragment() {

    private val adapter = ThemeAdapter()
    private val viewModel: ThemeModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_theme, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.theme)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        themeList.adapter = adapter
        themeList.layoutManager = LinearLayoutManager(context)
        adapter.onItemClickListener = { _, data ->
            activity?.work {
                reload(config(THEME_KEY,(data as ThemeList).id.toString()))
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.datas = viewModel.listDates
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}