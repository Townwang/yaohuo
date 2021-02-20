package com.townwang.yaohuo.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.resolve.ResolveListHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.HomeBean

class HomeModel(private val repo: Repo) : UIViewModel() {
    private val _tipData = MutableLiveData<List<HomeBean>>()
    val tipData: LiveData<List<HomeBean>> = _tipData
    private val _liveData = MutableLiveData<List<Product>>()
    val liveData: LiveData<List<Product>> = _liveData
    val data = mutableListOf<Product>()
    fun refresh(action: String) = launchTask {
        repo.refresh()
        data.clear()
        data.add(Product(0, null))
        data.add(Product(1, null))
        loadList(0, 1, action)
    }

    fun loadList(classId: Int, page: Int, action: String) = launchTask {
        val doc = repo.getNewList(classId, page, action)
        val helper = ResolveListHelper(doc)
        helper.homeListData.forEachIndexed { index, homeData ->
            data.add(Product(index, homeData))
        }
        _liveData.postValue(data)
    }

    fun loadTipList(classId: Int, page: Int, action: String) = launchTask {
        val doc = repo.getNewList(classId, page, action)
        val helper = ResolveListHelper(doc)
        val data = helper.homeListData.slice(0..4)
        data.last().title = "开发者公众号:开源人"
        _tipData.value = data
    }
}