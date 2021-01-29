package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.tencent.bugly.beta.Beta
import com.townwang.yaohuo.R
import com.townwang.yaohuo.YaoApplication
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.fragment.home.HomeFragment
import com.townwang.yaohuo.ui.fragment.me.MeFragment
import com.townwang.yaohuo.ui.fragment.send.SendFragment
import com.townwang.yaohuo.ui.fragment.send.SendModel
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.bottom_nav_view.*
import kotlinx.android.synthetic.main.include_home_bottom_btn.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivityHome : AppCompatActivity() {
    val viewModel: SendModel by viewModel()
    private var loading: LoadingDialog? = null
    val newsFrag = HomeFragment()
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
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, newsFrag)
                .commit()
        }
        addFab.onClickListener {
            val dialogFragment = SendFragment()
            val magTransaction = supportFragmentManager.beginTransaction()
            val fragment = supportFragmentManager.findFragmentByTag("send frag")
            if (fragment != null) {
                magTransaction.remove(fragment)
            }
            dialogFragment.mDialogListener = { _, title, type, content ->
                loading = Loading(getString(R.string.home_send_post_loading)).apply {
                    setSuccessText(getString(R.string.home_send_post_success))
                    setFailedText(getString(R.string.home_send_post_failure))
                }
                loading?.show()
                viewModel.sendPost(type, title, content)
                dialogFragment.dismiss()
            }
            dialogFragment.show(supportFragmentManager, "send frag")
        }
        news.onClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, newsFrag)
                .commit()
        }
        me.onClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, MeFragment())
                .commit()
        }
        viewModel.sendSuccess.observe(this, safeObserver {
            if (it) {
                newsFrag.refreshData()
                loading?.loadSuccess()
            } else {
                loading?.loadFailed()
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Beta.checkUpgrade(false, true)
    }

    override fun onBackPressed() {
        //disable the super here
    }

    private var prePressTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SystemClock.uptimeMillis() - prePressTime < 2500) {
                val app = application
                if (app is YaoApplication) {
                    app.appExit()
                }
            } else {
                prePressTime = SystemClock.uptimeMillis()
                Snackbar.make(
                    appbarLayout,
                    getString(R.string.exit_app_hint),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
