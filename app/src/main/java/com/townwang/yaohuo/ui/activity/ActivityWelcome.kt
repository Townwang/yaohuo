package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.THEME_KEY
import com.townwang.yaohuo.common.config
import com.townwang.yaohuo.common.setActTheme
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.ui.fragment.splash.SplashFragment
import kotlinx.android.synthetic.main.activity_about.*

class ActivityWelcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(false)
        }
        appbarLayout.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, SplashFragment())
            .commit()
    }
}
