package com.townwang.yaohuo.ui.fragment.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentWelcomeBinding
import com.townwang.yaohuo.ui.activity.ActivityHome
import com.townwang.yaohuo.ui.activity.ActivityLogin
import com.townwang.yaohuo.ui.fragment.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseFragment() {
    private val binding get() = _binding!! as FragmentWelcomeBinding
    private val viewModel: SplashModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mParticleView.startAnim()
        binding. mParticleView.mParticleAnimListener = {
            if (isAdded) {
                if (isCookieBoolean()) {
                    startActivity(Intent(requireContext(), ActivityLogin::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                } else {
                    viewModel.checkCookie(requireContext().config(TROUSER_KEY))
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.nieceSuccess.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                startActivity(Intent(requireContext(), ActivityHome::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                BuglyLog.d(BuildConfig.FLAVOR,"login == true")
            } else {
                Snackbar.make(requireView(), "非内测成员，请关注后续更新", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        requireActivity().finish()
                    }
                }.show()
            }
        })
        viewModel.cookieSuccess.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                startActivity(Intent(requireContext(), ActivityHome::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                BuglyLog.d(BuildConfig.FLAVOR,"login == true")
            } else {
                Snackbar.make(requireView(), "请勿乱破解，谢谢！", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        requireActivity().finish()
                    }
                }.show()
            }
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            startActivity(Intent(requireContext(), ActivityLogin::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
            requireContext().handleException(it)
        })
    }
}