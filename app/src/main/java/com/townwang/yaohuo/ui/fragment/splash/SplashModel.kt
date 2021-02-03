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
    fun checkCookie(trouserId:String) = launchTask {
        try {
            CrashReport.setUserId(trouserId)
            if (BuildConfig.IS_ALPHA.not()) {
                _cookieSuccess.value = isCrack
            } else {
                val result = repo.neice()
                val d =  result.select("div.rich_media_content")
                    .first()
                    .getElementsByTag("span")
                d.forEach {
                    if (it.text() == trouserId) {
                        _nieceSuccess.value = isCrack
                        return@launchTask
                    }
                }
                _nieceSuccess.value = false
            }
        } catch (e: Exception) {
            _nieceSuccess.value = false
        }
    }
}