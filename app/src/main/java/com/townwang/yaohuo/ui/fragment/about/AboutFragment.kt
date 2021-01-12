package com.townwang.yaohuo.ui.fragment.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.startAnimator
import com.townwang.yaohuo.common.work
import kotlinx.android.synthetic.main.fragment_about.*
import com.canking.minipay.MiniPayUtils
import com.canking.minipay.Config
import com.google.android.material.snackbar.Snackbar
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.townwang.yaohuo.common.WEB_VIEW_URL_KEY
import com.townwang.yaohuo.common.WEB_VIEW_URL_TITLE
import com.townwang.yaohuo.ui.activity.ActivityWebView


class AboutFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.about)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        startAnimator(logoImage.drawable)
        versionName.text = BuildConfig.VERSION_NAME
        versionName.setOnClickListener {
            Beta.checkUpgrade()
        }
        val upgradeInfo = Beta.getUpgradeInfo()
        if (upgradeInfo == null) {
            redDot.visibility = View.GONE
        } else {
            redDot.visibility = View.VISIBLE
        }
        openSource.setOnClickListener {
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityWebView::class.java
                ).apply {
                    putExtra(WEB_VIEW_URL_KEY, "https://github.com/Townwang")
                    putExtra(WEB_VIEW_URL_TITLE, "Townwang")
                }, null
            )
        }
        pubWechat.setOnClickListener {
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityWebView::class.java
                ).apply {
                    putExtra(WEB_VIEW_URL_KEY, "https://mp.weixin.qq.com/s/QY_GNyihv1Zx1RGHus26fg")
                    putExtra(WEB_VIEW_URL_TITLE, "公众号：文科中的技术宅")
                }, null
            )
        }
        post.setOnClickListener {
            Snackbar.make(post, "暂未开贴", Snackbar.LENGTH_SHORT).show()
        }
        praise.setOnClickListener {
            MiniPayUtils.setupPay(
                requireContext(),
                Config.Builder("fkx083710xkhl4xuxzpud4e", R.mipmap.alipay, R.mipmap.wechat).build()
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}