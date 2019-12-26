package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.fragment.bbs.BBSFragment
import com.townwang.yaohuo.ui.fragment.list.NesListFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.bottom_nav_view.*
import kotlinx.android.synthetic.main.include_home_bottom_btn.*

class ActivityHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        when (config(THEME_KEY).toInt()) {
            1 -> config(THEME_KEY, R.style.DefaultAppTheme.toString())
        }
        setActTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        setTitleCenter(toolbar)
        startAnimator(addFab.drawable)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
        val newsFrag = NesListFragment()
        val bbsFrag = BBSFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost,newsFrag)
            .commit()
        addFab.setOnClickListener {
            Snackbar.make(appbarLayout,"正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        news.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, newsFrag)
                .commit()
        }
        bbs.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, bbsFrag)
                .commit()
        }
        game.setOnClickListener {
            Snackbar.make(appbarLayout,"正在开发...", Snackbar.LENGTH_SHORT).show()
        }
        me.setOnClickListener {
            Snackbar.make(appbarLayout,"正在开发...", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        //disable the super here
    }

    private var prePressTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SystemClock.uptimeMillis() - prePressTime < 2500) {
                this@ActivityHome.finish()
            } else {
                prePressTime = SystemClock.uptimeMillis()
                Snackbar.make(appbarLayout, getString(R.string.exit_app_hint), Snackbar.LENGTH_SHORT).show()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
