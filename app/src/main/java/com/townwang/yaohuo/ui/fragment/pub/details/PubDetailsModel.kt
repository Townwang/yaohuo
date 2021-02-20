package com.townwang.yaohuo.ui.fragment.pub.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.UIViewModel
import com.townwang.yaohuo.common.asLiveData
import com.townwang.yaohuo.common.getParam
import com.townwang.yaohuo.common.resolve.ResolveDetailsHelper
import com.townwang.yaohuo.common.resolve.ResolveUserInfoHelper
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.repo.data.details.DetailsContentBean
import com.townwang.yaohuo.repo.enum.Level

class PubDetailsModel(private val repo: Repo) : UIViewModel() {
    private var helper: ResolveDetailsHelper? = null
    private val _data = MutableLiveData<DetailsContentBean>()
    val data = _data.asLiveData()
    private val _commentLists = MutableLiveData<List<Product>>()
    val commentLists: LiveData<List<Product>> = _commentLists
    val commentData = mutableListOf<Product>()
    private val _commentSize = MutableLiveData<String>()
    private val _commentSuccess = MutableLiveData<Boolean>()
    private val _noMore = MutableLiveData<Boolean>()
    val commentSuccess = _commentSuccess.asLiveData()
    val commentSize = _commentSize.asLiveData()
    val noMore = _noMore.asLiveData()
    var currentNextUrl = ""
    fun getDetails(url: String) = launchTask {
        val doc = repo.getNewListDetails(url)
        helper = ResolveDetailsHelper(doc)
        val docInfo =
            repo.getUserInfo(getParam(helper?.getHandUrl.orEmpty(), BuildConfig.YH_REPLY_TOUSERID))
        val userInfoHelper = ResolveUserInfoHelper(docInfo)
        _data.postValue(
            DetailsContentBean(
                helper?.reward.orEmpty(),
                helper?.giftMoney.orEmpty(),
                helper?.time.orEmpty(),
                helper?.praiseSize.orEmpty(),
                userInfoHelper.userName,
                helper?.onLineState ?: false,
                userInfoHelper.avatar,
                helper?.content.orEmpty(),
                Level.getLevel(userInfoHelper.grade),
                userInfoHelper.medal,
                helper?.downLoad,
                getParam(helper?.getHandUrl.orEmpty(), BuildConfig.YH_REPLY_TOUSERID)
            )
        )
        commentDetails(true, 0)
    }

    fun commentDetails(isRefresh: Boolean, ot: Int) = launchTask {
        if (isRefresh) {
            currentNextUrl = ""
        }
        helper?.let {it->
            val doc = if (currentNextUrl.isEmpty()) {
                commentData.clear()
                repo.comment(
                    it.id,
                    it.classId
                )
            } else {
                repo.getNext(currentNextUrl)
            }
            if (isRefresh) {
                _commentSize.postValue(it.getCommitLastFloor(doc))
            }
            val lists = it.getCommitListData(doc)
            lists.forEach { pro ->
                val data = pro.t
                if (data is CommitListBean) {
                    val touserid = getParam(data.avatar, BuildConfig.YH_REPLY_TOUSERID)
                    val docInfo = repo.getUserInfo(touserid)
                    val userInfoHelper = ResolveUserInfoHelper(docInfo)
                    data.avatar = userInfoHelper.avatar
                    data.level = Level.getLevel(userInfoHelper.grade)
                }
            }
            commentData.addAll(lists)
            _commentLists.value = commentData
            val nextUrl = it.getNextUrl(doc)
            if (nextUrl == currentNextUrl || nextUrl.isEmpty() || it.isLast(doc)) {
                _noMore.value = true
            } else {
                currentNextUrl = nextUrl
            }
        }
    }

    fun praise() = launchTask {
        repo.praise(helper?.getPraiseUrl.orEmpty())
    }

    fun favorite() = launchTask {
        repo.praise(helper?.getFavoriteUrl.orEmpty())
    }

    fun reply(
        content: String,
        sid: String,
        floor: String? = null,
        touserid: String? = null,
        sendmsg: String? = "1"
    ) = launchTask {
        try {
            helper?.run {
                val doc =
                    repo.reply(sid, content, id, classId, floor, touserid, sendmsg = sendmsg)
                doc.body()
                _commentSuccess.postValue(true)
            }
        } catch (e: Exception) {
            _commentSuccess.postValue(false)
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
        }
    }
}