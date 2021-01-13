package com.townwang.yaohuo.ui.fragment.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.android.tu.loadingdialog.LoadingDailog
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.ui.activity.ActivityHome
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {
    private var loading: LoadingDailog? = null
    private val viewModel: LoginModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
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
        loginBtn.onClickListener {
            viewModel.login(user_name.editText?.text.toString(), user_password.editText?.text.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.loginSuccess.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                val intent = Intent(requireContext(), ActivityHome::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), loginBtn, "share name"
                ).toBundle()
                ActivityCompat.startActivity(requireContext(), intent, bundle)
                requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                Log.d("解析", "登录成功")
                Snackbar.make(inputGuide, "登录成功", Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(requireView(), "请勿乱破解，谢谢！", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        requireActivity().finish()
                    }
                }.show()
            }
        })
        viewModel.loginError.observe(viewLifecycleOwner, safeObserver {
            Snackbar.make(inputGuide, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.loginUserError.observe(viewLifecycleOwner, safeObserver {
            user_name.error = it
        })
        viewModel.loginPsdError.observe(viewLifecycleOwner, safeObserver {
            user_password.error = it
        })
        viewModel.loading.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                if (loading == null) {
                    loading = Loading("登录中...").create()
                }
                if (loading?.isShowing?.not() == true) {
                    loading?.show()
                }
            } else {
                loading?.dismiss()
            }
        })
        viewModel.error.observe(viewLifecycleOwner, safeObserver {
            context?.handleException(it)
        })

        viewModel.nieceSuccess.observe(viewLifecycleOwner, safeObserver {
            if (it) {
                config(TROUSER_KEY,viewModel.trouser.value)
                val intent = Intent(requireContext(), ActivityHome::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), loginBtn, "share name"
                ).toBundle()
                ActivityCompat.startActivity(requireContext(), intent, bundle)
                requireActivity().overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                Log.d("解析", "登录成功")
                Snackbar.make(inputGuide, "登录成功", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "非内测成员，请关注后续更新", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        requireActivity().finish()
                    }
                }.show()
            }
        })
    }
}