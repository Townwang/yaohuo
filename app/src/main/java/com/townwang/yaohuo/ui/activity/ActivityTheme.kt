package com.townwang.yaohuo.ui.activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.binding.ext.viewbind
import com.townwang.yaohuo.common.setActTheme
import com.townwang.yaohuo.common.setSharedElement
import com.townwang.yaohuo.common.work
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.setTitleCenter
import com.townwang.yaohuo.databinding.ActivityThemeBinding
import com.townwang.yaohuo.ui.fragment.theme.ThemeFragment

class ActivityTheme : AppCompatActivity() {
    val binding: ActivityThemeBinding by viewbind()
    override fun onCreate(savedInstanceState: Bundle?) {
        setActTheme()
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.appbarLayout.toolbar)
        setSharedElement()
        supportActionBar.work {
            setDisplayHomeAsUpEnabled(true)
        }
        binding.appbarLayout.toolbar.setTitleCenter()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.navHost, ThemeFragment())
                .commit()
        }
    }
}
