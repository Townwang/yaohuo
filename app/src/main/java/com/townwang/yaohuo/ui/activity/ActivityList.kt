package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.TabBean
import com.townwang.yaohuo.ui.fragment.pub.PubListFragment
import com.townwang.yaohuo.ui.fragment.pub.TabListAdapter
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.appbar.*

class ActivityList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
       if (savedInstanceState == null) {
           val list = arrayListOf<TabBean>()

           list.add(TabBean("最近回复", PubListFragment().apply {
               arguments = Bundle().also {
                   it.putInt(LIST_CLASS_ID_KEY, intent.getIntExtra(LIST_CLASS_ID_KEY, 0))
                   it.putString(
                       LIST_BBS_NAME_KEY,
                       intent.getStringExtra(LIST_BBS_NAME_KEY)
                   )
                   it.putString(
                       LIST_ACTION_KEY,
                       "class"
                   )
               }
           }))
           list.add(TabBean("最新发布", PubListFragment().apply {
               arguments = Bundle().also {
                   it.putInt(LIST_CLASS_ID_KEY, intent.getIntExtra(LIST_CLASS_ID_KEY, 0))
                   it.putString(
                       LIST_BBS_NAME_KEY,
                       intent.getStringExtra(LIST_BBS_NAME_KEY)
                   )
                   it.putString(
                       LIST_ACTION_KEY,
                       "new"
                   )
               }
           }))
           list.add(TabBean("本版精华", PubListFragment().apply {
               arguments = Bundle().also {
                   it.putInt(LIST_CLASS_ID_KEY, intent.getIntExtra(LIST_CLASS_ID_KEY, 0))
                   it.putString(
                       LIST_BBS_NAME_KEY,
                       intent.getStringExtra(LIST_BBS_NAME_KEY)
                   )
                   it.putString(
                       LIST_ACTION_KEY,
                       "good"
                   )
               }
           }))
           viewPager?.adapter = TabListAdapter(supportFragmentManager,lifecycle.currentState.ordinal, list)
           tab_layout?.setupWithViewPager(viewPager)
       }
    }
}
