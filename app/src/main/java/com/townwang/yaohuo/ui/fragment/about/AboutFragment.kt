package com.townwang.yaohuo.ui.fragment.about
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.startAnimator
import com.townwang.yaohuo.common.work
import kotlinx.android.synthetic.main.fragment_about.*
import com.canking.minipay.MiniPayUtils
import com.canking.minipay.Config
import com.google.android.material.snackbar.Snackbar


class AboutFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        openSource.setOnClickListener {
            val uri = Uri.parse("https://github.com/Townwang")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        pubWechat.setOnClickListener {
            val uri = Uri.parse("https://mp.weixin.qq.com/s/QY_GNyihv1Zx1RGHus26fg")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        post.setOnClickListener {
            Snackbar.make(post, "暂未开贴", Snackbar.LENGTH_SHORT).show()
        }
        praise.setOnClickListener {
            MiniPayUtils.setupPay(
                context,
                Config.Builder("fkx083710xkhl4xuxzpud4e", R.mipmap.alipay, R.mipmap.wechat).build()
            )
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
              activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}