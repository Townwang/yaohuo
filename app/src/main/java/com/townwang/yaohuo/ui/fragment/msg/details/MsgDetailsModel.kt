package com.townwang.yaohuo.ui.fragment.msg.details

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveMsgDetailsHelper
import com.townwang.yaohuo.common.resolve.ResolveUserInfoHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.MsgDetailsBean
import org.jsoup.Jsoup

class MsgDetailsModel(private val repo: Repo) : UIViewModel() {
    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess = _isSuccess.asLiveData()
    private val _listDates = MutableLiveData<List<Product>>()
    val listDates = _listDates.asLiveData()
    val data = mutableListOf<Product>()
    var helper: ResolveMsgDetailsHelper? = null
    fun getMsgDetails(url: String, torridMe: String) = launchTask {
        val doc = repo.getNext(url)
        helper = ResolveMsgDetailsHelper(doc)
        val docInfo = repo.getUserInfo(helper?.torridUser ?: "")
        val userInfoHelper = ResolveUserInfoHelper(docInfo)
        val docInfoMe = repo.getUserInfo(torridMe)
        val meInfoHelper = ResolveUserInfoHelper(docInfoMe)
        data.clear()
        helper?.getList()?.forEachIndexed { index, msgDetailsBean ->
            if (msgDetailsBean.isUser) {
                data.add(
                    Product(
                        index, MsgDetailsBean(
                            msgDetailsBean.isUser,
                            userInfoHelper.grade,
                            msgDetailsBean.msg,
                            userInfoHelper.avatar
                        )
                    )
                )
            } else {
                data.add(
                    Product(
                        index, MsgDetailsBean(
                            msgDetailsBean.isUser,
                            meInfoHelper.grade,
                            msgDetailsBean.msg,
                            meInfoHelper.avatar
                        )
                    )
                )
            }
        }
        _listDates.value = data
    }

    fun senMsg(content: String) = launchTask {
        Jsoup.connect("").get()
        val data = helper?.getSendData(content)
        data?.run {
            repo.sendMsg(this)
            _isSuccess.value = true
        }
    }
}