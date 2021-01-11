package com.townwang.yaohuo.ui.fragment.login

import androidx.lifecycle.MutableLiveData
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

    private val _neiceSuccess = MutableLiveData<Boolean>()
    val neiceSuccess = _neiceSuccess.asLiveData()
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
                _loginSuccess.value = isCrack
                checkId()
            }
            resultPage.indexOf("密码错误") != -1 -> _loginPsdError.value = "密码错误"
            resultPage.indexOf("用户ID/用户名/手机号不存！") != -1 -> _loginUserError.value = "用户ID/用户名/手机号不存在！"
            resultPage.indexOf("登录失败次数超过10次了，请明天再来!") != -1 -> _loginError.value = "登录失败次数超过10次了，请明天再来!"
            else -> _loginError.value = "未知异常"
        }
    }

    private fun checkId() = launchTask {
        try {
            val doc = repo.checkNice()
            val a = doc.select("div.top2").select(A_KEY)[1].attr(A_HREF)
            val result = repo.neice()
            val trouserId = getParam(a,"touserid")
            result.data.forEach {
                if (it.phone == trouserId) {
                    _neiceSuccess.value = isCrack
                    _trouser.value = trouserId
                    return@launchTask
                }
            }
            _neiceSuccess.value = false
        } catch (e: Exception) {
            _neiceSuccess.value = false
        }

    }
}