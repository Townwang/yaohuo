package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.binding.ext.viewbind
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityPubBinding
import com.townwang.yaohuo.ui.fragment.pub.details.PubDetailsFragment
import com.townwang.yaohuoapi.*
import com.townwang.yaohuoapi.manager.config

class ActivityDetails : AppCompatActivity() {
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
            title = intent.getStringExtra(HOME_DETAILS_TITLE_KEY)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.navHost,
                    PubDetailsFragment().apply {
                        arguments = Bundle().also {
                            it.putString(
                                HOME_DETAILS_URL_KEY,
                                intent.getStringExtra(HOME_DETAILS_URL_KEY)
                            )
                            it.putString(
                                HOME_DETAILS_READ_KEY,
                                intent.getStringExtra(HOME_DETAILS_READ_KEY)
                            )
                            it.putBoolean(
                                HOME_DETAILS_BEAR_KEY,
                                intent.getBooleanExtra(HOME_DETAILS_BEAR_KEY,true)
                            )
                        }
                    }
                ).commit()
        }
    }
}
