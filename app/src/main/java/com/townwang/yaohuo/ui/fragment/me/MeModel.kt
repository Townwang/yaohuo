package com.townwang.yaohuo.ui.fragment.me

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.helper.ResolveDetailsHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.MeBean

class MeModel(private val repo: Repo) : UIViewModel() {
    var helper: ResolveDetailsHelper? = null
    private val _data = MutableLiveData<MeBean>()
    val data = _data.asLiveData()
    private val _avatar = MutableLiveData<String>()
    val avatar = _avatar.asLiveData()
    fun getMeData() = launchTask {
        val doc = repo.getMe()
        helper = ResolveDetailsHelper(doc)
        val nikeName = doc.select("div.welcome").text()
            .substringAfterLast(":")
        val content = doc.select("div.content")
        var accountNumber: String = ""
        var money: String = ""
        var bankSavings: String = ""
        var experience: String = ""
        var rank: String = ""
        var title: String = ""
        var identity: String = ""
        var managementAuthority: String = ""
        content.forEach {
            when (it.text().substringBefore(":")) {
                "我的ID" -> {
                    accountNumber = it.text().substringAfterLast(":")
                }
                "我的妖晶" -> {
                    money = it.text().substringAfterLast(":")
                }
                "银行存款" -> {
                    bankSavings = it.text().substringAfterLast(":")
                        .removeSuffix("管理")
                }
                "我的经验" -> {
                    experience = it.text().substringAfterLast(":")
                }
                "我的等级" -> {
                    rank = it.text().substringAfterLast(":")
                }
                "我的头衔" -> {
                    title = it.text().substringAfterLast(":")
                }
                "我的身份" -> {
                    identity = it.text().substringAfter(":")
                }
                "管理权限" -> {
                    managementAuthority = it.text().substringAfterLast(":")
                }
            }
        }
        _data.value = MeBean(
            nikeName,
            accountNumber,
            money,
            bankSavings,
            experience,
            rank,
            title,
            identity,
            managementAuthority
        )

        val headUrl = doc.getElementsContainingOwnText("我的空间").first().attr(A_HREF)
        getAvatar(getUrlString(headUrl))
    }

    private fun getAvatar(handUrl: String) = launchTask {
        val doc = repo.praise(handUrl)
        _avatar.value = helper?.getHandImage(doc)
    }

}