package com.townwang.yaohuo.ui.fragment.pub

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.HomeBean

class ListModel(private val repo: Repo) : UIViewModel() {
    private val _listDates = MutableLiveData<List<HomeBean>>()
    val listDates = _listDates.asLiveData()
    fun loadList(classId: Int, page: Int, action: String) = launchTask {
        val doc = repo.getNewList(classId, page, action)
        val helper = ResolveListHelper(doc)
        _listDates.value = helper.homeListData
    }
}