package com.townwang.yaohuo.ui.fragment.me

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveUserInfoHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.MeBean

class MeModel(private val repo: Repo) : UIViewModel() {
    var helper: ResolveUserInfoHelper? = null
    private val _data = MutableLiveData<MeBean>()
    val data = _data.asLiveData()
    private val _avatar = MutableLiveData<String>()
    val avatar = _avatar.asLiveData()
    fun getMeData(touserid: String) = launchTask {
        val doc = repo.getUserInfo(touserid)
        helper = ResolveUserInfoHelper(doc)
        helper?.also {
            _data.value = MeBean(
                it.userName,
                it.userId,
                it.demonCrystal,
                it.registrationTime,
                it.experience,
                it.grade,
                it.title,
                it.identity,
                it.purview
            )
            _avatar.value = helper?.avatar
        }
    }
}