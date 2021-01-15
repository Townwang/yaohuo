package com.townwang.yaohuo.ui.fragment.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.townwang.yaohuo.common.UIViewModel
import com.townwang.yaohuo.common.asLiveData
import com.townwang.yaohuo.common.helper.ResolveDetailsHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.CommitListBean

class DetailsModel(private val repo: Repo) : UIViewModel() {
    var helper: ResolveDetailsHelper? = null
    private val _content = MutableLiveData<String>()
    private val _commentLists = MutableLiveData<List<CommitListBean>>()
    private val _image = MutableLiveData<List<String>>()
    private val _download = MutableLiveData<List<String>>()
    private val _title = MutableLiveData<String>()
    private val _online = MutableLiveData<Boolean>()
    private val _time = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    private val _giftMoney = MutableLiveData<String>()
    private val _reward = MutableLiveData<String>()
    private val _commentPraise = MutableLiveData<String>()
    private val _commentSize = MutableLiveData<String>()
    private val _avatar = MutableLiveData<String>()
    private val _commentSuccess = MutableLiveData<Boolean>()
    val commentSuccess = _commentSuccess.asLiveData()
    val title = _title.asLiveData()
    val avatar = _avatar.asLiveData()
    val commentLists = _commentLists.asLiveData()
    val commentPraise = _commentPraise.asLiveData()
    val commentSize = _commentSize.asLiveData()
    val download = _download.asLiveData()
    val online = _online.asLiveData()
    val giftMoney = _giftMoney.asLiveData()
    val reward = _reward.asLiveData()
    val time = _time.asLiveData()
    val name = _name.asLiveData()
    val content = _content.asLiveData()
    val image = _image.asLiveData()
    fun getDetails(url: String) = launchTask {
        val doc = repo.getNewListDetails(url)
        helper = ResolveDetailsHelper(doc)
        _title.value = helper?.title
        _reward.value = helper?.reward
        _giftMoney.value = helper?.giftMoney
        _time.value = helper?.time
        _commentPraise.value = helper?.praiseSize
        _name.value = helper?.userName
        _online.value = helper?.onLineState
        getAvatar(helper?.getHandUrl ?: "")
        _content.value = helper?.content
        helper?.downLoad?.forEach {
            val array = arrayListOf<String>()
            array.add(it.fileName)
            array.add(it.url)
            array.add(it.description ?: "")
            _download.value = array
        }
        commentDetails(1, 0)
    }

    fun commentDetails(page: Int, ot: Int) = launchTask {
        helper?.let {
            if (it.isHaveMore) {
                val doc = repo.comment(
                    page,
                    it.id,
                    it.classId,
                    ot
                )
                _commentSize.value = it.getCommitLastFloor(doc)
                _commentLists.value = it.getCommitListData(doc)
            } else {
                Log.d("list", "没有更多了")
            }
        }
    }

    fun praise() = launchTask {
        repo.praise(helper?.getPraiseUrl ?: "")
    }

    private fun getAvatar(handUrl: String) = launchTask {
        val doc = repo.praise(handUrl)
        _avatar.value = helper?.getHandImage(doc)
    }

    fun reply(
        content: String,
        url: String,
        floor: String? = null,
        touserid: String? = null,
        sendmsg: String? = "1"
    ) =
        launchTask {
            try {
                val doc = repo.reply(url, content, floor, touserid, sendmsg = sendmsg)
                doc.body()
                _commentSuccess.value = true
            } catch (e: Exception) {
                _commentSuccess.value = false
            }
        }
}