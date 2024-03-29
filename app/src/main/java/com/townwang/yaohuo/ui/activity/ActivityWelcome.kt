package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.townwang.binding.ext.viewbind
import com.townwang.yaohuo.R
import com.townwang.yaohuoapi.THEME_KEY
import com.townwang.yaohuoapi.manager.config
import com.townwang.yaohuo.common.setActTheme
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.databinding.ActivityPubBinding
import com.townwang.yaohuo.ui.fragment.splash.SplashFragment

class ActivityWelcome : AppCompatActivity() {
    val binding: ActivityPubBinding by viewbind()
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(false)
        }
        binding.appbarLayout.appbar.visibility = View.GONE
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, SplashFragment())
                .commit()
        }
    }
}
