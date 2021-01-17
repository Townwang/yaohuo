package com.townwang.yaohuo.ui.fragment.details

import androidx.lifecycle.MutableLiveData
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.UIViewModel
import com.townwang.yaohuo.common.asLiveData
import com.townwang.yaohuo.common.helper.ResolveDetailsHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.repo.data.details.DetailsContentBean

class DetailsModel(private val repo: Repo) : UIViewModel() {
    var helper: ResolveDetailsHelper? = null
    private val _data = MutableLiveData<DetailsContentBean>()
    val data = _data.asLiveData()
    private val _commentLists = MutableLiveData<List<CommitListBean>>()
    private val _commentSize = MutableLiveData<String>()
    private val _avatar = MutableLiveData<String>()
    private val _commentSuccess = MutableLiveData<Boolean>()
    private val _noMore = MutableLiveData<Boolean>()
    val commentSuccess = _commentSuccess.asLiveData()
    val avatar = _avatar.asLiveData()
    val commentLists = _commentLists.asLiveData()
    val commentSize = _commentSize.asLiveData()
    val noMore = _noMore.asLiveData()
    fun getDetails(url: String) = launchTask {
        val doc = repo.getNewListDetails(url)
        helper = ResolveDetailsHelper(doc)
        _data.value = DetailsContentBean(
            helper?.title.orEmpty(),
            helper?.reward.orEmpty(),
            helper?.giftMoney.orEmpty(),
            helper?.time.orEmpty(),
            helper?.praiseSize.orEmpty(),
            helper?.userName.orEmpty(),
            helper?.onLineState ?: false,
            helper?.getHandUrl.orEmpty(),
            helper?.content.orEmpty(),
            helper?.downLoad
        )
        getAvatar(helper?.getHandUrl.orEmpty())
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
                _noMore.value = true
            }
        }
    }

    fun praise() = launchTask {
        repo.praise(helper?.getPraiseUrl.orEmpty())
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
                BuglyLog.e(BuildConfig.FLAVOR, e.message)
            }
        }
}