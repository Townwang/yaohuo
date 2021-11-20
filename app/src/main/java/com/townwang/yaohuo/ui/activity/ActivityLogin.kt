package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.bugly.beta.Beta
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityLoginBinding
import com.townwang.yaohuo.ui.fragment.login.LoginFragment
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.townwang.yaohuoapi.THEME_KEY
import com.townwang.yaohuoapi.manager.config

class ActivityLogin : AppCompatActivity() {
    val binding: ActivityLoginBinding by viewbind()
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
        }
        binding.appbarLayout.toolbar.setTitleCenter()
        Beta.checkUpgrade(false, true)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, LoginFragment())
                .commit()
        }
    }
}
