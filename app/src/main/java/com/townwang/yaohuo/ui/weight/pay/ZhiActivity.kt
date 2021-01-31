package com.townwang.yaohuo.ui.weight.pay

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.townwang.yaohuo.R
import com.townwang.yaohuo.databinding.ZhiActivityBinding
class ZhiActivity : AppCompatActivity() {
    private var mZhiWay = 0

    companion object {
        private const val ZHI_WAY_WE_CHAT = 0 //weixin
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ZhiActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val config = intent.getSerializableExtra(PayHelper.EXTRA_KEY_PAY_CONFIG)
        config ?: throw IllegalStateException("PayHelper PayConfig illegal!!!")
        if (config is PayConfig) {
            binding.zhiBg.setOnClickListener {
                if (mZhiWay == ZHI_WAY_WE_CHAT) {
                    binding.zhiBg.setBackgroundResource(R.drawable.background_blue_10)
                    binding.zhiTitle.setText(R.string.ali_zhi_title)
                    binding.zhiSummery.text = config.aliTip ?: getString(R.string.ali_zhi_tip)
                    binding.qaImageView.setImageResource(config.aliQaImage)
                } else {
                    binding.zhiBg.setBackgroundResource(R.drawable.background_green_10)
                    binding.zhiTitle.setText(R.string.wei_zhi_title)
                    binding.zhiSummery.text = config.weChatTip ?: getString(R.string.wei_zhi_tip)
                    binding.qaImageView.setImageResource(config.weChatQaImage)
                }
                mZhiWay = ++mZhiWay % 2
            }
            binding.zhiBtn.setOnClickListener {
                if (mZhiWay == ZHI_WAY_WE_CHAT) {
                    PayHelper.weZhi.startWeZhi(this, binding.qaLayout)
                } else {
                    PayHelper.aliZhi.startAliPayClient(this, config.aliZhiKey)
                }
            }
            binding.zhiBg.setBackgroundResource(R.drawable.background_green_10)
            binding.zhiTitle.setText(R.string.wei_zhi_title)
            binding.zhiSummery.text = config.weChatTip ?: getString(R.string.wei_zhi_tip)
            binding.qaImageView.setImageResource(config.weChatQaImage)
            val animator = ObjectAnimator.ofFloat(binding.tip, "alpha", 0f, 0.66f, 1.0f, 0f)
            animator.duration = 2888
            animator.repeatCount = 6
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.repeatMode = ValueAnimator.REVERSE
            animator.start()
        }
    }
}