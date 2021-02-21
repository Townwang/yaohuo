package com.townwang.yaohuo.ui.fragment.search

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.HomeBean

class SearchModel(private val repo: Repo) : UIViewModel() {
    private val _listDates = MutableLiveData<List<HomeBean>>()
    val listDates = _listDates.asLiveData()
    fun loadList(key: String, page: Int) = launchTask {
        val doc = repo.queryList(key, page)
        val helper = ResolveListHelper(doc)
        _listDates.value = helper.homeListData
    }
}