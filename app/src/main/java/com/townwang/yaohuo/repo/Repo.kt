package com.townwang.yaohuo.repo
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.api.Api
import com.townwang.yaohuo.common.*
import okhttp3.internal.wait
import org.jsoup.nodes.Document

class Repo constructor(
    private val api: Api
) {
    suspend fun neice() = withRepoContext {
        val bbs = api.niece()
        bbs.getUResp()
    }

    suspend fun checkNice() = withRepoContext {
        val doc = api.checkNice()
        doc.getResp()
    }

    suspend fun login(loginName: String, password: String): Document = withRepoContext {
        val rs = api.getLoginParameter()
        val doc = rs.getResp()
        val ets = doc.select(AK_FORM)
        val data = HashMap<String, String>()
        ets.first().allElements.forEach {
            if (it.attr(AK_NAME) == BuildConfig.YH_LOGIN_USER_NAME) {
                it.attr(AK_VALUE, loginName)
            }
            if (it.attr(AK_NAME) == BuildConfig.YH_LOGIN_PASSWORD) {
                it.attr(AK_VALUE, password)
            }
            if (it.attr(AK_NAME) == BuildConfig.YH_LOGIN_SACESID) {
                it.attr(AK_VALUE, "1")
            }
            if (it.attr(AK_NAME).isNotEmpty()) {
                data[it.attr(AK_NAME)] = it.attr(AK_VALUE)
            }
        }
        val con = api.login(data)
        con.getResp()
    }

    suspend fun getNewList(classId: Int, page: Int,action:String): Document = withRepoContext {
        val bbs = api.getNewList(classId.toString(), page.toString(),action)
        bbs.getResp()
    }

    suspend fun getNewListDetails(url: String): Document = withRepoContext {
        val bbs = api.urlPenetrate(url)
        bbs.getResp()
    }

    suspend fun praise(url: String): Document = withRepoContext {
        val bbs = api.urlPenetrate(url)
        bbs.getResp()
    }

    suspend fun getUserInfo(touserid: String): Document = withRepoContext {
        val bbs = api.getUserInfo(touserid)
        bbs.getResp()
    }

    suspend fun comment(
        page: Int,
        id: String,
        classId: Int,
        ot: Int
    ) = withRepoContext {
        val bbs = api.commentLists(
            page.toString(),
            id,
            classId.toString(),
            ot.toString()
        )
        bbs.getResp()
    }

    suspend fun reply(
        url: String,
        content: String,
        floor: String? = null,
        touserid: String? = null,
        sendmsg:String? = "1"
    ): Document = withRepoContext {
        val bbs = api.urlPenetrate(url)
        val rs = bbs.getResp()
        val ets = rs.select(AK_FORM)
        val data = HashMap<String, String>()
        ets.first().allElements.forEach {
            if (it.attr(AK_NAME) == BuildConfig.YH_REPLY_CONTENT) {
                it.attr(AK_VALUE, content)
            }
            if (it.attr(AK_NAME) == BuildConfig.YH_REPLY_SEND_MSG) {
                it.attr(AK_VALUE, sendmsg)
            }
            if (it.attr(AK_NAME) ==BuildConfig.YH_REPLY_REPLY) {
                it.attr(AK_VALUE, floor)
            }
            if (it.attr(AK_NAME) == BuildConfig.YH_REPLY_TOUSERID) {
                it.attr(AK_VALUE, touserid)
            }
            if (it.attr(AK_NAME).isNotEmpty()) {
                data[it.attr(AK_NAME)] = it.attr(AK_VALUE)
            }
        }
        val rep = api.reply(data)
        rep.getResp()
    }

    suspend fun getMe(): Document = withRepoContext {
        val me = api.getMe()
        me.getResp()
    }
}