package com.townwang.yaohuo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ActivityPubBinding
import com.townwang.yaohuo.ui.fragment.me.MeFragment
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.townwang.yaohuoapi.TROUSER_KEY

class ActivityInfo : AppCompatActivity() {
    val binding: ActivityPubBinding by viewbind()
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
                .replace(R.id.navHost, MeFragment().apply {
                    arguments = Bundle().also {
                        it.putString(
                            TROUSER_KEY,
                            intent.getStringExtra(TROUSER_KEY)
                        )
                    }
                })
                .commit()
        }
    }


}
