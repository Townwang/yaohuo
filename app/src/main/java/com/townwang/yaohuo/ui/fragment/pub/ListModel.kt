package com.townwang.yaohuo.ui.fragment.pub

import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.HomeData
class ListModel(private val repo: Repo) : UIViewModel() {
    private val _listDates = MutableLiveData<List<HomeData>>()
    val listDates = _listDates.asLiveData()
    fun loadList(classId: Int, page: Int,action:String) = launchTask {
        val doc = repo.getNewList(classId, page,action)
        val list = doc.select(NEW_LIST)
        val lists = arrayListOf<HomeData>()
        list.forEach {
            val hrefs = it.select(A_KEY).first()
            val a = hrefs.attr(A_HREF)
            val title = hrefs.text()
            val str = it
            str.select(A).first().remove()
            val auth = str.text().split("/")[0].split(" ").last()
            val reply = str.text().split("/")[1]
            val read = str.text().split("/")[2]
            val time = it.select(NEW_LIST_TIME).text()
            val smailImg = it.select(IMG_GIF)
            val smailImgs = arrayListOf<String>()
            smailImg.forEach { img ->
                smailImgs.add(img.attr(IMG_ALT))
            }
            lists.add(HomeData(title, a, auth, reply, read, time, smailImgs))
        }
        _listDates.value = lists
    }
}