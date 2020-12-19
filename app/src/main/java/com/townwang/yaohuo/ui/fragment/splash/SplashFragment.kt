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
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.pgyersdk.update.javabean.AppBean
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.getCookie
import com.townwang.yaohuo.common.handleException
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mParticleView.startAnim()
        mParticleView.setOnParticleAnimListener {
            checkUpdate()
        }
    }

    private  fun checkUpdate(){
        PgyUpdateManager.Builder()
            .setForced(true)                //设置是否强制提示更新,非自定义回调更新接口此方法有用
            .setUserCanRetry(true)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
            .setDeleteHistroyApk(true)     // 检查更新前是否删除本地历史 Apk， 默认为true
            .setUpdateManagerListener(object : UpdateManagerListener {
                override fun onNoUpdateAvailable() {
                    if (getCookie().isNullOrEmpty()) {
                        startActivity(Intent(context, ActivityLogin::class.java))
                        activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                        activity?.finish()
                    } else {
                        viewModel.checkCookie()
                    }
                    Log.d("pgyer", "there is no new version")
                }
                override fun onUpdateAvailable(appBean: AppBean) {
                    PgyUpdateManager.downLoadApk(appBean.downloadURL)
                }
                override fun checkUpdateFailed(e: Exception) {
                    if (getCookie().isNullOrEmpty()) {
                        startActivity(Intent(context, ActivityLogin::class.java))
                        activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                        activity?.finish()
                    } else {
                        viewModel.checkCookie()
                    }
                }
            }).register()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.neiceSuccess.observe(requireActivity(), safeObserver {
            it ?: return@safeObserver
            if (it) {
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
        viewModel.cookieSuccess.observe(requireActivity(), Observer {
            it ?: return@Observer
            if (it) {
//                startActivity(Intent(context, ActivityHome::class.java))
//                activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
//                activity?.finish()
            }else{
                Snackbar.make(requireView(), "请勿乱破解，谢谢！", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        activity?.finish()
                    }
                }.show()
            }
        })
        viewModel.error.observe(requireActivity(), safeObserver {
            startActivity(Intent(context, ActivityLogin::class.java))
            activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
            context?.handleException(it)
            activity?.finish()
        })
    }
}