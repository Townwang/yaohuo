package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityListBinding
import com.townwang.yaohuo.databinding.ActivityPubBinding
import com.townwang.yaohuo.repo.data.TabBean
import com.townwang.yaohuo.ui.fragment.msg.MsgFragment
import com.townwang.yaohuo.ui.fragment.pub.PubListFragment
import com.townwang.yaohuo.ui.fragment.pub.TabListAdapter
import com.townwang.yaohuo.ui.fragment.search.SearchFragment
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class ActivityMsg : AppCompatActivity() {
    val binding: ActivityPubBinding by viewbind()
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.appbarLayout.toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
            title = "消息"
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.navHost,
                    MsgFragment()
                ).commit()
        }
    }
}
