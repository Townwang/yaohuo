package com.townwang.yaohuo.ui.fragment.search

import androidx.lifecycle.MutableLiveData
import androidx.room.PrimaryKey
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.HomeData

class SearchModel(private val repo: Repo) : UIViewModel() {
    private val _listDates = MutableLiveData<List<HomeData>>()
    val listDates = _listDates.asLiveData()
    fun loadList(key: String, page: Int) = launchTask {
        val doc = repo.queryList(key, page)
        val helper = ResolveListHelper(doc)
        _listDates.value = helper.homeListData
    }
}