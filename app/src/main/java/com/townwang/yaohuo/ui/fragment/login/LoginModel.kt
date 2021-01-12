package com.townwang.yaohuo.ui.fragment.login

import androidx.lifecycle.MutableLiveData
import com.tencent.bugly.crashreport.CrashReport
import com.townwang.yaohuo.BuildConfig.IS_ALPHA
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo

class LoginModel(private val repo: Repo) : UIViewModel() {
    private val _loginSuccess = MutableLiveData<Boolean>()
    private val _loginError = MutableLiveData<String>()
    private val _loginUserError = MutableLiveData<String>()
    private val _loginPsdError = MutableLiveData<String>()
    private val _trouser = MutableLiveData<String>()

    val loginUserError = _loginUserError.asLiveData()
    val loginPsdError = _loginPsdError.asLiveData()
    val loginSuccess = _loginSuccess.asLiveData()
    val loginError = _loginError.asLiveData()
    val trouser = _trouser.asLiveData()

    private val _nieceSuccess = MutableLiveData<Boolean>()
    val nieceSuccess = _nieceSuccess.asLiveData()
    fun login(loginName: String, password: String) = launchTask {
        if (loginName.isEmpty()) {
            _loginUserError.value = "用户名不能为空"
            return@launchTask
        }
        if (password.isEmpty()) {
            _loginPsdError.value = "密码不能为空"
            return@launchTask
        }
        val doc = repo.login(loginName, password)
        val resultPage = doc.body().html()
        when {
            resultPage.indexOf("登录成功") != -1 -> {
                getAccountInformation()
            }
            resultPage.indexOf("密码错误") != -1 -> _loginPsdError.value = "密码错误"
            resultPage.indexOf("用户ID/用户名/手机号不存！") != -1 -> _loginUserError.value =
                "用户ID/用户名/手机号不存在！"
            resultPage.indexOf("登录失败次数超过10次了，请明天再来!") != -1 -> _loginError.value =
                "登录失败次数超过10次了，请明天再来!"
            else -> _loginError.value = "未知异常"
        }
    }

    private fun getAccountInformation() = launchTask {
        val doc = repo.checkNice()
        val a = doc.select("div.top2").select(A_KEY)[1].attr(A_HREF)
        val trouserId = getParam(a, "touserid")
        CrashReport.setUserId(trouserId)
        if (IS_ALPHA.not()) {
            _loginSuccess.value = isCrack
        } else {
            try {
                val result = repo.neice()
                result.data.forEach {
                    if (it.phone == trouserId) {
                        _nieceSuccess.value = isCrack
                        _trouser.value = trouserId
                        return@launchTask
                    }
                }
                _nieceSuccess.value = false
            } catch (e: Exception) {
                _nieceSuccess.value = false
            }
        }

    }
}