package com.townwang.yaohuo.common.resolve

import android.util.Log
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.details.MsgDetailsBean
import com.townwang.yaohuoapi.*
import com.townwang.yaohuoapi.BuildConfig.YH_REPLY_TOUSERID
import org.jsoup.nodes.Document


class ResolveMsgDetailsHelper(val document: Document) {

    val torridUser: String
        get() {
            val url =
                document.select("div.the_user").select("div.info").last().select(A_KEY).attr(
                    A_HREF
                )
            return getParam(url, YH_REPLY_TOUSERID)
        }

    fun getList(): List<MsgDetailsBean> {
        val data = mutableListOf<MsgDetailsBean>()
        data.clear()
        val doc = document.select("div.content").last().children()
        doc.forEach {
            val isUser = when {
                it.hasClass("the_me") -> false
                it.hasClass("the_user") -> true
                else -> null
            }
            isUser?.work {
                val msg = it.select("div.con").last().html()
                val url = it.select("div.info").last().select(A_KEY).attr(
                    A_HREF
                )
                Log.d("哈哈哈", "$msg \n $url")
                data.add(
                    MsgDetailsBean(
                        isUser,
                        "0",
                        msg,
                        url
                    )
                )
            }
        }
        return data.reversed()
    }

    fun getSendData(content: String): HashMap<String, String> {
        val ets = document.select(AK_FORM)
        val data = HashMap<String, String>()
        ets.first().allElements.forEach {
            data[it.attr(AK_NAME)] = it.attr(
                AK_VALUE
            )
        }
        data["content"] = content
        return data
    }


}