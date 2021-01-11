package com.townwang.yaohuo.ui.fragment.me
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo

class MeModel(private val repo: Repo) : UIViewModel() {

    private val _nikeName = MutableLiveData<String>()
    val nikeName = _nikeName.asLiveData()

    private val _accountNumber = MutableLiveData<String>()
    val accountNumber = _accountNumber.asLiveData()

    private val _money = MutableLiveData<String>()
    val money = _money.asLiveData()

    private val _bankSavings = MutableLiveData<String>()
    val bankSavings = _bankSavings.asLiveData()

    private val _experience = MutableLiveData<String>()
    val experience = _experience.asLiveData()

    private val _rank = MutableLiveData<String>()
    val rank = _rank.asLiveData()

    private val _title = MutableLiveData<String>()
    val title = _title.asLiveData()

    private val _identity = MutableLiveData<String>()
    val identity = _identity.asLiveData()

    private val _managementAuthority = MutableLiveData<String>()
    val managementAuthority = _managementAuthority.asLiveData()

    fun getMeData() = launchTask {
        val doc =  repo.getMe()
        _nikeName.value =   doc.select("div.welcome").text().substringAfterLast(":")

            val content = doc.select("div.content")

            content.forEach {
                when(it.text().substringBefore(":")){
                    "我的ID" ->{_accountNumber.value = it.text().substringAfterLast(":")}
                    "我的妖晶" ->{_money.value = it.text().substringAfterLast(":")}
                    "银行存款" ->{_bankSavings.value = it.text().substringAfterLast(":").removeSuffix("管理")}
                    "我的经验" ->{_experience.value = it.text().substringAfterLast(":")}
                    "我的等级" ->{_rank.value = it.text().substringAfterLast(":")}
                    "我的头衔" ->{_title.value = it.text().substringAfterLast(":")}
                    "我的身份" ->{_identity.value = it.text().substringAfter(":")}
                    "管理权限" ->{_managementAuthority.value = it.text().substringAfterLast(":")}
                }
            }
    }

}