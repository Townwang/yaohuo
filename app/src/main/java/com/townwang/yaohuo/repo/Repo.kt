package com.townwang.yaohuo.repo

import android.content.Context
import android.content.LocusId
import android.util.Log
import com.townwang.yaohuo.api.Api
import com.townwang.yaohuo.api.JsApi
import com.townwang.yaohuo.common.*
import kotlinx.coroutines.delay
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Repo constructor(
    private val applicationContext: Context,
    private val api: Api,
    private val jsApi: JsApi
) {

    suspend fun neice() = withRepoContext {
        val bbs = api.neice()
        bbs.getResp()
    }


    suspend fun checkNice() = withRepoContext {
      val doc =   Jsoup.connect("https://yaohuo.me/")
        doc.getResp()
    }



    suspend fun cookie() = withRepoContext {
        val bbs = Jsoup.connect(jsApi.checkCookie())
        bbs.getResp()
    }

    suspend fun login(loginName: String, password: String): Document = withRepoContext {
        val rs = Jsoup.connect(jsApi.login()).execute()
        val doc = Jsoup.parse(rs.body())
        val ets = doc.select(AK_FORM)
        val data = HashMap<String, String>()
        ets.first().allElements.forEach {
            if (it.attr(AK_NAME) == YH_USERNAME) {
                it.attr(AK_VALUE, loginName)
            }
            if (it.attr(AK_NAME) == YH_PASSWORD) {
                it.attr(AK_VALUE, password)
            }
            if (it.attr(AK_NAME) == YH_SACESID) {
                it.attr(AK_VALUE, "1")
            }
            if (it.attr(AK_NAME).isNotEmpty()) {
                data[it.attr(AK_NAME)] = it.attr(AK_VALUE)
            }
        }
        val con = Jsoup.connect(jsApi.login())
        val login = con.ignoreContentType(true).method(Connection.Method.POST).data(data).cookies(rs.cookies())
            .execute()
        val map = login.cookies()
        if (login.statusCode() == 200) {
            setCookie(map)
        } else {
            throw  NetworkFailureException("网络故障")
        }
        con.get()
    }
    suspend fun getNewList(classId: Int, page: Int): Document = withRepoContext {
        val bbs = Jsoup.connect(jsApi.newLists(classId, page))
        bbs.getResp()
    }
    suspend fun getNewListDetails(url: String): Document = withRepoContext {
        val bbs = Jsoup.connect(url)
        bbs.getResp()
    }
    suspend fun praise(url: String): Document = withRepoContext {
        val bbs = Jsoup.connect(url)
        bbs.getResp()
    }
    suspend fun comment(page:Int,id: String,classId: Int,ot:Int): Document = withRepoContext {
        val bbs = Jsoup.connect(jsApi.commentLists(page,id,classId,ot))
        bbs.getResp()
    }


    suspend fun reply(url: String,content: String,floor:String? = null,touserid:String? = null): Document = withRepoContext {
        val rs = Jsoup.connect(url).getResp()
        val ets = rs.select(AK_FORM)
        val data = HashMap<String, String>()
        ets.first().allElements.forEach {
            if (it.attr(AK_NAME) == "content") {
                it.attr(AK_VALUE, content)
            }
            if (it.attr(AK_NAME) == "reply"){
                it.attr(AK_VALUE,floor)
            }
            if (it.attr(AK_NAME) == "touserid"){
                it.attr(AK_VALUE,touserid)
            }
            if (it.attr(AK_NAME).isNotEmpty()) {
                data[it.attr(AK_NAME)] = it.attr(AK_VALUE)
            }
        }
        val con = Jsoup.connect(jsApi.reply())
        con.ignoreContentType(true).method(Connection.Method.POST).data(data).cookies(getCookie())
            .execute()
        con.get()
    }
}