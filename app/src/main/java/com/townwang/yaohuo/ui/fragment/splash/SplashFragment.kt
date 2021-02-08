package com.townwang.yaohuo.ui.fragment.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentWelcomeBinding
import com.townwang.yaohuo.ui.activity.ActivityHome
import com.townwang.yaohuo.ui.activity.ActivityLogin
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : Fragment(R.layout.fragment_welcome) {
    private val viewModel: SplashModel by viewModel()
    val binding: FragmentWelcomeBinding by viewbind()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mParticleView.startAnim()
        binding. mParticleView.mParticleAnimListener = {
            if (isAdded) {
                when(requireContext().config(BuildConfig.APP_IS_CRACK)){
                    "-1" ->{
                        Snackbar.make(requireView(), "请勿乱破解，谢谢！", Snackbar.LENGTH_INDEFINITE).apply {
                            setAction(android.R.string.ok) {
                                requireActivity().finish()
                            }
                        }.show()
                    }
                    else ->{
                        startActivity(Intent(requireContext(), ActivityHome::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                        BuglyLog.d(BuildConfig.FLAVOR,"login == true")
                    }
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }
}