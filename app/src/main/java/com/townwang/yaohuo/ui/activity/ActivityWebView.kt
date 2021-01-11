package com.townwang.yaohuo.ui.activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.fragment.about.AboutFragment
import com.townwang.yaohuo.ui.fragment.web.WebViewFragment
import kotlinx.android.synthetic.main.appbar.*


class ActivityWebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setSupportActionBar(toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, WebViewFragment(
                intent.getStringExtra(WEB_VIEW_URL_KEY),
                intent.getStringExtra(WEB_VIEW_URL_TITLE)
            ))
            .commit()
    }


}
