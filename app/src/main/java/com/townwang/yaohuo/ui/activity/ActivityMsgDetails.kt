package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityPubBinding
import com.townwang.yaohuo.ui.fragment.msg.details.MsgDetailsFragment
import com.townwang.yaohuo.ui.fragment.pub.details.PubDetailsFragment
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class ActivityMsgDetails : AppCompatActivity() {
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
                    MsgDetailsFragment().apply {
                        arguments = Bundle().also {
                            it.putString(
                                HOME_DETAILS_URL_KEY,
                                intent.getStringExtra(HOME_DETAILS_URL_KEY)
                            )
                        }
                    }
                ).commit()
        }
    }
}
