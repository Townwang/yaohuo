package com.townwang.yaohuo.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.HomeData

class HomeModel(private val repo: Repo) : UIViewModel() {
    private val _listDates = MutableLiveData<List<HomeData>>()
    val listDates = _listDates.asLiveData()
    fun loadList(classId: Int, page: Int, action: String) = launchTask {
        val doc = repo.getNewList(classId, page, action)
        val helper = ResolveListHelper(doc)
        _listDates.value = helper.homeListData
    }
}