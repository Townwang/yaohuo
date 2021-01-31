package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.THEME_KEY
import com.townwang.yaohuo.common.config
import com.townwang.yaohuo.common.setActTheme
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.databinding.ActivityPubBinding
import com.townwang.yaohuo.ui.fragment.splash.SplashFragment

class ActivityWelcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        val binding = ActivityPubBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
