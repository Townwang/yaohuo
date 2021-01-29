package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.fragment.search.SearchFragment
import kotlinx.android.synthetic.main.appbar.*

class ActivitySearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pub)
        setSupportActionBar(toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
            title = intent.getStringExtra(HOME_SEARCH_URL_KEY)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.navHost,
                    SearchFragment().apply {
                        arguments = Bundle().also {
                            it.putString(
                                HOME_SEARCH_URL_KEY,
                                intent.getStringExtra(HOME_SEARCH_URL_KEY)
                            )
                        }
                    }
                ).commit()
        }
    }
}
