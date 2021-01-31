package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityListBinding
import com.townwang.yaohuo.repo.data.TabBean
import com.townwang.yaohuo.ui.fragment.pub.PubListFragment
import com.townwang.yaohuo.ui.fragment.pub.TabListAdapter
class ActivityList : AppCompatActivity() {
    lateinit var binding: ActivityListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appbarLayout.toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
        if (savedInstanceState == null) {
            val list = arrayListOf<TabBean>()
            if (intent.getIntExtra(LIST_CLASS_ID_KEY, 0) != 0) {
                list.add(TabBean(getString(R.string.list_tab_new_reply), PubListFragment().apply {
                    arguments = Bundle().also {
                        it.putInt(LIST_CLASS_ID_KEY, intent.getIntExtra(LIST_CLASS_ID_KEY, 0))
                        it.putString(
                            LIST_BBS_NAME_KEY,
                            intent.getStringExtra(LIST_BBS_NAME_KEY)
                        )
                        it.putString(
                            LIST_ACTION_KEY,
                            BuildConfig.YH_BBS_ACTION_CLASS
                        )
                    }
                }))
            }
            list.add(TabBean(getString(R.string.list_tab_new_send), PubListFragment().apply {
                arguments = Bundle().also {
                    it.putInt(LIST_CLASS_ID_KEY, intent.getIntExtra(LIST_CLASS_ID_KEY, 0))
                    it.putString(
                        LIST_BBS_NAME_KEY,
                        intent.getStringExtra(LIST_BBS_NAME_KEY)
                    )
                    it.putString(
                        LIST_ACTION_KEY,
                        BuildConfig.YH_BBS_ACTION_NEW
                    )
                }
            }))
            list.add(TabBean(getString(R.string.list_tab_marrow), PubListFragment().apply {
                arguments = Bundle().also {
                    it.putInt(LIST_CLASS_ID_KEY, intent.getIntExtra(LIST_CLASS_ID_KEY, 0))
                    it.putString(
                        LIST_BBS_NAME_KEY,
                        intent.getStringExtra(LIST_BBS_NAME_KEY)
                    )
                    it.putString(
                        LIST_ACTION_KEY,
                        BuildConfig.YH_BBS_ACTION_GOOD
                    )
                }
            }))
            binding.viewPager.adapter =
                TabListAdapter(supportFragmentManager, lifecycle.currentState.ordinal, list)
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }
}
