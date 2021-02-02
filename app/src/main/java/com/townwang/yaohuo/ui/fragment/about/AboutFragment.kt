package com.townwang.yaohuo.ui.fragment.about

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tencent.bugly.beta.Beta
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentAboutBinding
import com.townwang.yaohuo.ui.activity.ActivityWebView
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.townwang.yaohuo.ui.weight.pay.PayConfig
import com.townwang.yaohuo.ui.weight.pay.PayHelper
class AboutFragment : Fragment(R.layout.fragment_about) {
    val binding: FragmentAboutBinding by viewbind()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.about)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        startAnimator(binding.logoImage.drawable)
        binding.versionName.text = BuildConfig.VERSION_NAME
        binding.versionName.onClickListener {
            Beta.checkUpgrade()
        }
        val upgradeInfo = Beta.getUpgradeInfo()
        if (upgradeInfo == null) {
            binding.redDot.visibility = View.GONE
        } else {
            binding.redDot.visibility = View.VISIBLE
        }
        binding.openSource.onClickListener {
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityWebView::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(WEB_VIEW_URL_KEY, "https://github.com/Townwang")
                    putExtra(WEB_VIEW_URL_TITLE, "Townwang")
                }, null
            )
        }
        binding.pubWechat.onClickListener {
            ActivityCompat.startActivity(
                requireContext(), Intent(
                    requireContext(), ActivityWebView::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra(WEB_VIEW_URL_KEY, "https://mp.weixin.qq.com/s/QY_GNyihv1Zx1RGHus26fg")
                    putExtra(WEB_VIEW_URL_TITLE, "公众号：文科中的技术宅")
                }, null
            )
        }
        binding.post.onClickListener {
            Snackbar.make(binding.post, "暂未开贴", Snackbar.LENGTH_SHORT).show()
        }
        binding.praise.onClickListener {
            PayHelper.setupPay(
                requireContext(),
                PayConfig(
                    "fkx083710xkhl4xuxzpud4e",
                    R.mipmap.alipay, R.mipmap.wechat
                )
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