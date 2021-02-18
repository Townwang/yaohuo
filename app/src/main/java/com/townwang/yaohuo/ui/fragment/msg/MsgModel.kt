package com.townwang.yaohuo.ui.fragment.msg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.common.resolve.ResolveMsgHelper
import com.townwang.yaohuo.repo.Repo

class MsgModel(private val repo: Repo) : UIViewModel() {
    private val _liveData = MutableLiveData<List<Product>>()
    val liveData: LiveData<List<Product>> = _liveData
    private val _noData = MutableLiveData<Boolean>()
    val noData: LiveData<Boolean> = _noData
    val data = mutableListOf<Product>()
    var currentNextUrl = ""
    fun loadList(isRefresh: Boolean) = launchTask {
        if (isRefresh) {
            currentNextUrl = ""
        }
        val doc = if (currentNextUrl.isNullOrEmpty()) {
            repo.getMsg()
        } else {
            repo.getNext(currentNextUrl)
        }
        val helper = ResolveMsgHelper()
        data.addAll(helper.getMsgList(doc))
        _liveData.value = data
        val nextUrl = helper.getNextUrl(doc)
        if (nextUrl == currentNextUrl) {
            _noData.value = true
        } else {
            currentNextUrl = nextUrl
        }
    }

    fun operating(url: String) = launchTask {
        repo.getNext(url)
        loadList(true)
    }

    fun deleteMsg(url: String,pro :Product) = launchTask {
        data.remove(pro)
        _liveData.value = data
        repo.deleteMsg(url)
    }
}