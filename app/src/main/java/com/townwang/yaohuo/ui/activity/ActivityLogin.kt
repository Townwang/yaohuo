package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.fragment.login.LoginFragment
import kotlinx.android.synthetic.main.appbar.*

class ActivityLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        setTitleCenter(toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, LoginFragment())
            .commit()
    }
}
