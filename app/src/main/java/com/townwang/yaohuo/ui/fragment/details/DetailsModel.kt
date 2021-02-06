package com.townwang.yaohuo.ui.fragment.details

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.IMG_JPG
import com.townwang.yaohuo.common.UIViewModel
import com.townwang.yaohuo.common.asLiveData
import com.townwang.yaohuo.common.resolve.ResolveDetailsHelper
import com.townwang.yaohuo.common.resolve.ResolveUserInfoHelper
import com.townwang.yaohuo.databinding.ItemCommentDataBinding
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.repo.data.details.DetailsContentBean
import com.townwang.yaohuo.repo.data.local.ItemAvatarBean
import com.townwang.yaohuo.repo.enum.Level
import org.jsoup.Jsoup

class DetailsModel(private val repo: Repo) : UIViewModel() {
    private var helper: ResolveDetailsHelper? = null
    private val _data = MutableLiveData<DetailsContentBean>()
    val data = _data.asLiveData()
    private val _medal = MutableLiveData<String>()
    val medal = _medal.asLiveData()
    private val _commentLists = MutableLiveData<List<CommitListBean>>()
    private val _commentSize = MutableLiveData<String>()
    private val _avatar = MutableLiveData<String>()
    private val _grade = MutableLiveData<String>()
    private val _itemAvatar = MutableLiveData<ItemAvatarBean>()
    private val _commentSuccess = MutableLiveData<Boolean>()
    private val _noMore = MutableLiveData<Boolean>()
    val commentSuccess = _commentSuccess.asLiveData()
    val avatar = _avatar.asLiveData()
    val grade = _grade.asLiveData()
    val itemAvatar = _itemAvatar.asLiveData()
    val commentLists = _commentLists.asLiveData()
    val commentSize = _commentSize.asLiveData()
    val noMore = _noMore.asLiveData()
    fun getDetails(url: String) = launchTask {
        val doc = repo.getNewListDetails(url)
        helper = ResolveDetailsHelper(doc)
        _data.postValue(
            DetailsContentBean(
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
        )
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
                _commentSize.postValue(it.getCommitLastFloor(doc))
                _commentLists.postValue( it.getCommitListData(doc) )
            } else {
                _noMore.postValue(true)
            }
        }
    }

    fun praise() = launchTask {
        repo.praise(helper?.getPraiseUrl.orEmpty())
    }

    fun favorite() = launchTask {
        repo.praise(helper?.getFavoriteUrl.orEmpty())
    }

    fun getAvatar(touserid: String) = launchTask {
        val doc = repo.getUserInfo(touserid)
        val userInfoHelper = ResolveUserInfoHelper(doc)
        _avatar.postValue(userInfoHelper.avatar)
        _grade.postValue( Level.getLevel(userInfoHelper.grade).toString())
        val medalImgUrl = userInfoHelper.medal
        Jsoup.parse(medalImgUrl).select(IMG_JPG).forEach {
            _medal.postValue(it.attr("src"))
        }
    }

    fun getUserInfo(item: ItemCommentDataBinding, touserid: String) = launchTask {
        item.userImg.tag = touserid
        val doc = repo.getUserInfo(touserid)
        val userInfoHelper = ResolveUserInfoHelper(doc)
        _itemAvatar.postValue( ItemAvatarBean(
            item,
            userInfoHelper.avatar,
            Level.getLevel(userInfoHelper.grade),
            touserid
        ))

    }

    fun reply(
        content: String,
        sid: String,
        floor: String? = null,
        touserid: String? = null,
        sendmsg: String? = "1"
    ) =
        launchTask {
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