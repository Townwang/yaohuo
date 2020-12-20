package com.townwang.yaohuo.ui.fragment.splash

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo

class SplashModel(private val repo: Repo) : UIViewModel() {

    private val _nieceSuccess = MutableLiveData<Boolean>()
    val nieceSuccess = _nieceSuccess.asLiveData()
    private val _cookieSuccess = MutableLiveData<Boolean>()
    val cookieSuccess = _cookieSuccess.asLiveData()

    fun checkCookie() = launchTask {
        repo.cookie()
        _cookieSuccess.value = isCrack
        checkId()
    }
    private fun checkId() = launchTask {
        try {
            val doc = repo.checkNice()
            val a = doc.select("div.top2").select(A_KEY)[1].attr(A_HREF)
            val result = repo.neice()
            result.data.forEach {
                if (it.phone == getParam(a,"touserid")) {
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