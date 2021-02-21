package com.townwang.yaohuo.ui.fragment.login

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.FragmentLoginBinding
import com.townwang.yaohuo.ui.activity.ActivityHome
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.townwang.yaohuo.ui.weight.pay.PayHelper
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel: LoginModel by viewModel()
    val binding: FragmentLoginBinding by viewbind()
    var loading: LoadingDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = getString(R.string.app_name)
                setDisplayHomeAsUpEnabled(false)
            }
        }
        binding.loginBtn.onClickListener {
            if (loading == null) {
                loading = Loading("登录中...").apply {
                    setSuccessText("登陆成功")
                    setFailedText("登陆失败")
                }
            }
            loading?.show()
            viewModel.login(
                binding.userName.editText?.text.toString(),
                binding.userPassword.editText?.text.toString()
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.loginSuccess.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                requireContext().config(TROUSER_KEY, viewModel.trouser.value)
                val intent = Intent(requireContext(), ActivityHome::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), binding.loginBtn, "share name"
                ).toBundle()
                ActivityCompat.startActivity(requireContext(), intent, bundle)
                requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                BuglyLog.d(BuildConfig.FLAVOR, "login == true")
                loading?.loadSuccess()
            } else {
                loading?.loadFailed()
                Snackbar.make(requireView(), "非内测成员，在公众号文章<font color='#03A9F4'>蓝火客户端</font>留言报名".toHtml(), Snackbar.LENGTH_INDEFINITE).apply {
                    setAction("去报名") {
                        PayHelper.weZhi.startWeChatFollowClosely(requireContext(),"请关注公众号后在文章留言报名")
                        requireActivity().finish()
                    }
                }.show()
            }
        })
        viewModel.loginError.observe(viewLifecycleOwner, safeObserver {
            loading?.loadFailed()
            Snackbar.make(binding.inputGuide, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.loginUserError.observe(viewLifecycleOwner, safeObserver {
            loading?.loadFailed()
            binding.userName.error = it
        })
        viewModel.loginPsdError.observe(viewLifecycleOwner, safeObserver {
            loading?.loadFailed()
            binding.userPassword.error = it
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            loading?.loadFailed()
            context?.handleException(it)
        })
    }

    override fun onDestroy() {
        loading?.run {
            close()
        }
        loading = null
        super.onDestroy()
    }
}