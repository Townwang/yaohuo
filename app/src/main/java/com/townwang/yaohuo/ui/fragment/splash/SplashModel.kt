package com.townwang.yaohuo.ui.fragment.splash

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.ThemeList

class SplashModel(private val repo: Repo) : UIViewModel() {

    private val _neiceSuccess = MutableLiveData<Boolean>()
    val neiceSuccess = _neiceSuccess.asLiveData()
    private val _cookieSuccess = MutableLiveData<Boolean>()
    val cookieSuccess = _cookieSuccess.asLiveData()

    fun checkCookie() = launchTask {
        repo.cookie()
        //TODO 内测期间不放开
//        _cookieSuccess.value = isCrack
        checkId()
    }
    private fun checkId() = launchTask {
        try {
            val doc = repo.checkNice()
            val a = doc.select("div.top2").select(A_KEY)[1].absUrl(A_HREF)
            val result = repo.neice()
            result.data.forEach {
                if (it.phone == getParam(a,"touserid")) {
                    _neiceSuccess.value = isCrack
                    return@launchTask
                }
            }
            setCookie(emptyMap())
            _neiceSuccess.value = false
        } catch (e: Exception) {
            setCookie(emptyMap())
            _neiceSuccess.value = false
        }
    }

}