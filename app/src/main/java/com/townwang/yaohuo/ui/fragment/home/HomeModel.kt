package com.townwang.yaohuo.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.HomeData

class HomeModel(private val repo: Repo) : UIViewModel() {
    private val _liveData = MutableLiveData<List<Product>>()
    val liveData: LiveData<List<Product>> = _liveData
    val data = mutableListOf<Product>()
    fun loadList(classId: Int, page: Int, action: String) = launchTask {
        val doc = repo.getNewList(classId, page, action)
        val helper = ResolveListHelper(doc)
        if (page == 1){
            data.clear()
        }
        helper.homeListData.forEachIndexed { index, homeData ->
            data.add(Product(index,homeData))
        }
        _liveData.postValue(data)
    }
}