package com.townwang.yaohuo.ui.fragment.send

import androidx.lifecycle.MutableLiveData
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.UIViewModel
import com.townwang.yaohuo.common.asLiveData
import com.townwang.yaohuo.repo.Repo

class SendModel(private val repo: Repo) : UIViewModel() {
    private val _sendSuccess = MutableLiveData<Boolean>()
    val sendSuccess = _sendSuccess.asLiveData()
    fun sendPost(classId: String, title: String, content: String) = launchTask {
        try {
            val doc = repo.sendGeneral(classId, title, content)
            doc.body()
            _sendSuccess.value = true
        } catch (e: Exception) {
            _sendSuccess.value = false
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
        }
    }
}