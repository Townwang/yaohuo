package com.townwang.yaohuo.repo

import android.util.Log
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuoapi.Api
import com.townwang.yaohuo.common.*
import com.townwang.yaohuoapi.AK_FORM
import com.townwang.yaohuoapi.AK_NAME
import com.townwang.yaohuoapi.AK_VALUE
import com.townwang.yaohuoapi.BuildConfig.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File


class Repo constructor(
    private val api: Api
) {
    suspend fun neice() = withRepoContext {
        Jsoup.connect(CLOSED_ALPHA_LIST).get()
    }

    suspend fun checkNice() = withRepoContext {
        val doc = api.refresh()
        doc.getResp()
    }

    suspend fun refresh() = withRepoContext {
        val doc = api.refresh()
        doc.getResp()
    }

    suspend fun login(loginName: String, password: String): Document = withRepoContext {
        val data = HashMap<String, String>()
        data[YH_LOGIN_USER_NAME] = loginName
        data[YH_LOGIN_PASSWORD] = password
        val con = api.login(data)
        con.getResp()
    }

    suspend fun getNewList(classId: Int, page: Int, action: String): Document = withRepoContext {
        val bbs = api.getNewList(classId.toString(), page.toString(), action)
        bbs.getResp()
    }


    suspend fun getMsg(): Document = withRepoContext {
        val bbs = api.msg()
        bbs.getResp()
    }

    suspend fun deleteMsg(url: String): Document = withRepoContext {

        val bbs = api.deleteMsg(getParam(url, "id"), getParam(url, "backurl"))
        bbs.getResp()
    }

    suspend fun getNext(url: String): Document = withRepoContext {
        val bbs = api.urlPenetrate(url)
        bbs.getResp()
    }

    suspend fun queryList(key: String, page: Int): Document = withRepoContext {
        val bbs = api.queryListBBS(key = key, page = page.toString())
        bbs.getResp()
    }

    suspend fun getNewListDetails(url: String): Document = withRepoContext {
        val bbs = api.urlPenetrate(url)
        bbs.getResp()
    }

//    suspend fun getVTHistoryForProfile(
//        classId: Int,
//        action: String
//    ): LiveData<PagedList<HomeData>> {
//        val source = apiCacheDAO.getVTHistoryDataSource(classId)
//        val pageSize = 15
//        return LivePagedListBuilder(source, pageSize)
//            .setBoundaryCallback(object : PagedList.BoundaryCallback<HomeData>() {
//                override fun onZeroItemsLoaded() {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val doc =
//                            api.getNewList(classId.toString(), "1", action).getResp()
//                        Log.d("哈哈哈哈哈哈哈哈哈哈哈1","page==1")
//                        val helper = ResolveListHelper(doc)
//                        apiCacheDAO.cacheVTHistories(classId, helper.homeListData)
//                    }
//                }
//
//                override fun onItemAtEndLoaded(itemAtEnd: HomeData) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val total = apiCacheDAO.getVTHistoryCount(itemAtEnd.title)
//                        val page = total % 15 + 1
//                        Log.d("哈哈哈哈哈哈哈哈哈哈哈2","page==$page")
//                        val doc =
//                            api.getNewList(classId.toString(), page.toString(), action).getResp()
//                        val helper = ResolveListHelper(doc)
//                        apiCacheDAO.cacheVTHistories(classId, helper.homeListData)
//                    }
//                }
//
//                override fun onItemAtFrontLoaded(itemAtFront: HomeData) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val doc =
//                            api.getNewList(classId.toString(), "1", action).getResp()
//                        Log.d("哈哈哈哈哈哈哈哈哈哈哈3","page==3")
//                        val helper = ResolveListHelper(doc)
//                        apiCacheDAO.cacheVTHistories(classId, helper.homeListData)
//                    }
//                }
//            })
//            .build()
//    }

    suspend fun praise(url: String): Document = withRepoContext {
        val bbs = api.urlPenetrate(url)
        bbs.getResp()
    }

    suspend fun getUserInfo(touserid: String): Document = withRepoContext {
        val bbs = api.getUserInfo(touserid)
        bbs.getResp()
    }

    suspend fun comment(
        id: String,
        classId: Int
    ) = withRepoContext {
        val bbs = api.commentLists(
            id,
            classId.toString()
        )
        bbs.getResp()
    }

    suspend fun reply(
        sid: String,
        content: String,
        id: String,
        classId: Int,
        floor: String? = null,
        touserid: String? = null,
        sendmsg: String? = null
    ): Document = withRepoContext {
        val data = HashMap<String, String>()
        data[YH_REPLY_CONTENT] = content
        data[YH_REPLY_SEND_MSG] = sendmsg ?: "1"
        data[YH_REPLY_SEND_MSG2] = sendmsg ?: "1"
        floor?.apply {
            data[YH_REPLY_REPLY] = floor
        }
        touserid?.apply {
            data[YH_REPLY_TOUSERID] = touserid
        }
        data[YH_REPLY_SID] = sid
        data[YH_REPLY_ID] = id
        data[YH_SEND_BOOK_CLASSID] = classId.toString()
        val rep = api.reply(data)
        rep.getResp()
    }


    suspend fun sendGeneral(
        classId: String,
        title: String,
        content: String
    ): Document = withRepoContext {
        val bbs = api.getSendBookUrl(classId)
        val rs = bbs.getResp()
        val ets = rs.select(AK_FORM)
        val data = HashMap<String, String>()
        ets.first().allElements.forEach {
            if (it.attr(AK_NAME) == YH_SEND_BOOK_TITLE) {
                it.attr(AK_VALUE, title)
            }
            if (it.attr(AK_NAME) == YH_SEND_BOOK_CONTENT) {
                it.attr(AK_VALUE, content)
            }
            if (it.attr(AK_NAME) == YH_SEND_BOOK_CLASSID) {
                it.attr(AK_VALUE, classId)
            }
            if (it.attr(AK_NAME).isNotEmpty()) {
                data[it.attr(AK_NAME)] = it.attr(
                    AK_VALUE
                )
            }
        }
        val rep = api.sendGeneral(data)
        rep.getResp()
    }


    suspend fun getMe( touserid: String): Document = withRepoContext {
        val me = api.getMe(touserid)
        me.getResp()
    }

    suspend fun uploadFile(file: File, type: String) = withRepoContext {
        val fileRequestBody = RequestBody.create(type.toMediaTypeOrNull(), file)
        val requestImgPart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
        val repo = api.upLoadFile(requestImgPart)
        repo.getYaoResp()
    }

    suspend fun deleteImg(url: String) = withRepoContext {
        val repo = api.urlPenetrate(url)
        repo.getResp()
    }

    suspend fun sendMsg(data: Map<String, String>) = withRepoContext {
        Log.d("哈哈哈哈",data.entries.toString())
        val repo = api.sendMsg(data)
        repo.getResp()
    }

    suspend fun getXlsx() = withRepoContext {
        api.getXlsx()
    }
}