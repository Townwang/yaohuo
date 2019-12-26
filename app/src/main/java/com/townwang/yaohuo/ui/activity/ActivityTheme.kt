package com.townwang.yaohuo.ui.activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.common.setActTheme
import com.townwang.yaohuo.common.setSharedElement
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.setTitleCenter
import com.townwang.yaohuo.ui.fragment.theme.ThemeFragment
import kotlinx.android.synthetic.main.appbar.*

class ActivityTheme : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setActTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        setSupportActionBar(toolbar)
        setTitleCenter(toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.navHost, ThemeFragment())
            .commit()
    }
}
