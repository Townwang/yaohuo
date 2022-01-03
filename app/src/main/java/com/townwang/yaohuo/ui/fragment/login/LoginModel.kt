package com.townwang.yaohuo.ui.fragment.login

import androidx.lifecycle.MutableLiveData
import com.tencent.bugly.crashreport.CrashReport
import com.townwang.yaohuo.BuildConfig.IS_ALPHA
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuoapi.A_HREF
import com.townwang.yaohuoapi.A_KEY
import com.townwang.yaohuoapi.BuildConfig.*

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
            resultPage.indexOf(YH_MATCH_LOGIN_SUCCESS) != -1 -> {
                getAccountInformation()
            }
            resultPage.indexOf(YH_MATCH_LOGIN_PASS_ERROR) != -1 ->
                _loginPsdError.value = YH_MATCH_LOGIN_PASS_ERROR
            resultPage.indexOf(YH_MATCH_LOGIN_USER_ERROR) != -1 ->
                _loginUserError.value = YH_MATCH_LOGIN_USER_ERROR
            resultPage.indexOf(YH_MATCH_LOGIN_MAX_ERROR) != -1 ->
                _loginError.value = YH_MATCH_LOGIN_MAX_ERROR
            else -> _loginError.value = "未知异常"
        }
    }

    private fun getAccountInformation() = launchTask {
        try {
            val doc = repo.checkNice()
            val a = doc.select("div.top2").select(A_KEY)[1].attr(
                A_HREF
            )
            val trouserId = getParam(a, YH_REPLY_TOUSERID)
            _trouser.value = trouserId
            CrashReport.setUserId(trouserId)
            if (IS_ALPHA.not()) {
                _loginSuccess.value = true
            } else {
                val result = repo.neice()
                val d =  result.select("div.rich_media_content")
                    .first()
                    .getElementsByTag("span")
                d.forEach {
                    if (it.text() == trouserId) {
                        _loginSuccess.value  = true
                        _trouser.value = trouserId
                        return@launchTask
                    }
                }
                _loginSuccess.value  = false
            }
        } catch (e: Exception) {
            _loginSuccess.value  = false
        }
    }
}