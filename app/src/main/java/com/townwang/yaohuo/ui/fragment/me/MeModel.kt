package com.townwang.yaohuo.ui.fragment.me

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.BuildConfig
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
                BuildConfig.YH_MATCH_MY_ID -> {
                    accountNumber = it.text().substringAfterLast(":")
                }
                BuildConfig.YH_MATCH_MY_MONEY  -> {
                    money = it.text().substringAfterLast(":")
                }
                BuildConfig.YH_MATCH_MY_BACK  -> {
                    bankSavings = it.text().substringAfterLast(":")
                        .removeSuffix( BuildConfig.YH_MATCH_MY_MANAGER )
                }
                BuildConfig.YH_MATCH_MY_EX  -> {
                    experience = it.text().substringAfterLast(":")
                }
                BuildConfig.YH_MATCH_MY_RANK  -> {
                    rank = it.text().substringAfterLast(":")
                }
                BuildConfig.YH_MATCH_MY_TITLE  -> {
                    title = it.text().substringAfterLast(":")
                }
                BuildConfig.YH_MATCH_MY_IDENTITY  -> {
                    identity = it.text().substringAfter(":")
                }
                BuildConfig.YH_MATCH_MY_MANAGEMENT  -> {
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

        val headUrl = doc.getElementsContainingOwnText( BuildConfig.YH_MATCH_MY_ROOM).first().attr(A_HREF)
        getAvatar(getUrlString(headUrl))
    }

    private fun getAvatar(handUrl: String) = launchTask {
        val doc = repo.praise(handUrl)
        _avatar.value = helper?.getHandImage(doc)
    }

}