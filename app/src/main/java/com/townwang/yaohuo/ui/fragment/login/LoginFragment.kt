package com.townwang.yaohuo.ui.fragment.login

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.work
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.android.tu.loadingdialog.LoadingDailog
import com.townwang.yaohuo.common.Loading
import com.townwang.yaohuo.common.handleException
import com.townwang.yaohuo.common.safeObserver
import com.townwang.yaohuo.ui.activity.ActivityHome
import com.townwang.yaohuo.ui.activity.ActivityLogin
import kotlinx.android.synthetic.main.fragment_login.*

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
        loginBtn.setOnClickListener {
            viewModel.login(user_name.editText?.text.toString(), user_password.editText?.text.toString())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loginSuccess.observe(requireActivity(), Observer {
            it ?: return@Observer
            if (it) {
                val intent = Intent(context, ActivityHome::class.java)
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), loginBtn, "share name"
                ).toBundle()
                ActivityCompat.startActivity(requireContext(), intent, bundle)
                activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                Log.d("解析", "登录成功")
                Snackbar.make(inputGuide, "登录成功", Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(requireView(), "请勿乱破解，谢谢！", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        activity?.finish()
                    }
                }.show()
            }
        })
        viewModel.loginError.observe(requireActivity(), Observer {
            it ?: return@Observer
            Snackbar.make(inputGuide, it, Snackbar.LENGTH_SHORT).show()
        })
        viewModel.loginUserError.observe(requireActivity(), Observer {
            it ?: return@Observer
            user_name.error = it
        })
        viewModel.loginPsdError.observe(requireActivity(), Observer {
            it ?: return@Observer
            user_password.error = it
        })
        viewModel.loading.observe(requireActivity(), safeObserver {
            if (it) {
                if (loading == null) {
                    loading = Loading("登录中...").create()
                }
                if (!loading!!.isShowing) {
                    loading?.show()
                }
            } else {
                loading?.dismiss()
            }
        })
        viewModel.error.observe(requireActivity(), safeObserver {
            context?.handleException(it)
        })

        viewModel.neiceSuccess.observe(requireActivity(), safeObserver {
            it ?: return@safeObserver
            if (it) {
                val intent = Intent(context, ActivityHome::class.java)
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(), loginBtn, "share name"
                ).toBundle()
                ActivityCompat.startActivity(requireContext(), intent, bundle)
                activity?.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                Log.d("解析", "登录成功")
                Snackbar.make(inputGuide, "登录成功", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "非内测成员，请关注后续更新", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(android.R.string.ok) {
                        activity?.finish()
                    }
                }.show()
            }
        })
    }
}