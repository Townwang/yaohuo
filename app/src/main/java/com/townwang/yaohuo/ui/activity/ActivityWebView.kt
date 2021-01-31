package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityWebviewBinding
import com.townwang.yaohuo.ui.fragment.web.WebViewFragment
class ActivityWebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appbarLayout.toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, WebViewFragment().apply {
                    arguments = Bundle().also {
                        it.putString(WEB_VIEW_URL_KEY, intent.getStringExtra(WEB_VIEW_URL_KEY))
                        it.putString(WEB_VIEW_URL_TITLE, intent.getStringExtra(WEB_VIEW_URL_TITLE))
                    }
                }).commit()
        }
    }


}
