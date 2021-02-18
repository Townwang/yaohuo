package com.townwang.yaohuo.ui.fragment.msg.details

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.MsgDetailsBean

class MsgDetailsModel(private val repo: Repo) : UIViewModel() {
    private val _listDates = MutableLiveData<List<Product>>()
    val listDates = _listDates.asLiveData()
    val data = mutableListOf<Product>()
    fun getMsgDetails(url:String) = launchTask {
//        val doc = repo.getNext(url)
        data.add(Product(0,MsgDetailsBean(true,"1","哈哈哈哈哈","https://yaohuo.me/album/upload/1000/2014/08/25/1000_1726210.png")))
        data.add(Product(1,MsgDetailsBean(true,"2","lalallala","https://yaohuo.me/bbs/head/3.gif")))
        data.add(Product(2,MsgDetailsBean(false,"3","ffffffffffff","https://yaohuo.me/album/upload/1000/2021/02/09/24770_1353480.png")))
        _listDates.value = data
    }
}