package com.townwang.yaohuo.common.resolve

import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.utils.matchValue
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.repo.data.MsgBean
import com.townwang.yaohuo.ui.fragment.msg.Product
import org.jsoup.nodes.Document


class ResolveMsgHelper {

    fun getMsgList(document: Document): List<Product> {
        val list = document.select(NEW_LIST)
        val lists = arrayListOf<Product>()
        lists.clear()
        list.forEachIndexed { index, element ->
            val hrefs = element.select(A_KEY).first()
            val deletes = element.select(A_KEY).last()
            val time = matchValue(
                element.html(),
                "<br>",
                "[",
                true
            ).removePrefix("<br>").removeSuffix("[")
            lists.add(
                Product(
                    index, MsgBean(
                        hrefs.text(),
                        hrefs.attr(A_HREF),
                        deletes.attr(A_HREF),
                        time,
                        element.select(IMG_GIF).isNotEmpty()
                    )
                )
            )
        }
        return lists
    }

    fun getNextUrl(document: Document): String {
        return try {
            document.getElementsContainingOwnText("下一页").last().attr(A_HREF)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    }


    fun getPreviousUrl(document: Document): String {
        return try {
            document.getElementsContainingOwnText("上一页").last().attr(A_HREF)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    }

    fun clearSystemUrl(document: Document): String {
        return try {
            document.getElementsContainingOwnText("清空系统消息").last().attr(A_HREF)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    }

    fun clearToChatWithUrl(document: Document): String {
        return try {
            document.getElementsContainingOwnText("清空聊天消息").last().attr(A_HREF)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    }

    fun clearCollectUrl(document: Document): String {
        return try {
            document.getElementsContainingOwnText("清空收藏消息").last().attr(A_HREF)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    }

    fun clearInboxUrl(document: Document): String {
        return try {
            document.getElementsContainingOwnText("清所有收件箱").last().attr(A_HREF)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    }


}