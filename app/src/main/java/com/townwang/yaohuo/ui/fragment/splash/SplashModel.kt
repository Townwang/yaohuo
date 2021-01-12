package com.townwang.yaohuo.ui.fragment.splash

import androidx.lifecycle.MutableLiveData
import com.tencent.bugly.crashreport.CrashReport
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo

class SplashModel(private val repo: Repo) : UIViewModel() {

    private val _nieceSuccess = MutableLiveData<Boolean>()
    val nieceSuccess = _nieceSuccess.asLiveData()
    private val _cookieSuccess = MutableLiveData<Boolean>()
    val cookieSuccess = _cookieSuccess.asLiveData()

    fun checkCookie() = launchTask {
        repo.cookie()
        val doc = repo.checkNice()
        val a = doc.select("div.top2").select(A_KEY)[1].attr(A_HREF)
        val trouserId = getParam(a, "touserid")
        CrashReport.setUserId(trouserId)
        if (BuildConfig.IS_ALPHA.not()) {
            _cookieSuccess.value = isCrack
        } else {
            try {
                val result = repo.neice()
                result.data.forEach {
                    if (it.phone == trouserId) {
                        _nieceSuccess.value = isCrack
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