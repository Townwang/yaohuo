package com.townwang.yaohuo.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.repo.Repo

class HomeModel(private val repo: Repo) : UIViewModel() {
    private val _liveData = MutableLiveData<List<Product>>()
    val liveData: LiveData<List<Product>> = _liveData
    val data = mutableListOf<Product>()
    fun loadList(classId: Int, page: Int, action: String) = launchTask {
        val doc = repo.getNewList(classId, page, action)
        val helper = ResolveListHelper(doc)
        if (page == 1) {
            data.clear()
            data.add(Product(0, null))
            data.add(Product(1, null))
        }
        helper.homeListData.forEachIndexed { index, homeData ->
            data.add(Product(index, homeData))
        }
        _liveData.postValue(data)
    }
}