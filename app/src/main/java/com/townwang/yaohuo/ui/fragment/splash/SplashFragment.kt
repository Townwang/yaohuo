package com.townwang.yaohuo.ui.fragment.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.handleException
import com.townwang.yaohuo.common.isCookieBoolean
import com.townwang.yaohuo.common.safeObserver
import com.townwang.yaohuo.ui.activity.ActivityHome
import com.townwang.yaohuo.ui.activity.ActivityLogin
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mParticleView.startAnim()
        mParticleView.setOnParticleAnimListener {
            checkUpdate()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.nieceSuccess.observe(viewLifecycleOwner, safeObserver {
            it ?: return@safeObserver
            if (it) {
//                startActivity(Intent(context, ActivityLogin::class.java))
                startActivity(Intent(context, ActivityHome::class.java))
                activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                activity?.finish()
                Log.d("解析", "登录成功")
            } else {
                Snackbar.make(requireView(), "非内测成员，请关注后续更新", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        activity?.finish()
                    }
                }.show()
            }
        })
        viewModel.cookieSuccess.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            if (it) {
//                startActivity(Intent(context, ActivityHome::class.java))
//                activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
//                activity?.finish()
            } else {
                Snackbar.make(requireView(), "请勿乱破解，谢谢！", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        activity?.finish()
                    }
                }.show()
            }
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            startActivity(Intent(context, ActivityLogin::class.java))
            activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
            context?.handleException(it)
            activity?.finish()
        })
    }

    private fun checkUpdate() {
                    if (isCookieBoolean()) {
                        startActivity(Intent(context, ActivityLogin::class.java))
                        activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                        activity?.finish()
                    } else {
                        viewModel.checkCookie()
                    }
                    Log.d("pgyer", "there is no new version")

    }
}