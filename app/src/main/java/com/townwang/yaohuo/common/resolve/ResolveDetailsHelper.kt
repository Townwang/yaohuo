package com.townwang.yaohuo.common.resolve

import android.annotation.SuppressLint
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.utils.DateFormatHelper
import com.townwang.yaohuo.common.utils.matchValue
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.repo.data.details.DownloadBean
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*

class ResolveDetailsHelper(private val document: Document) {
    val id: String
        get() = try {
            getParam(getFavoriteUrl, BuildConfig.YH_REPLY_ID)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    val classId: Int
        get() = try {
            getParam(getFavoriteUrl, BuildConfig.YH_SEND_BOOK_CLASSID).toInt()
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            0
        }
    val userName: String
        get() = try {
            document.select("div.subtitle").last().select(A_KEY).first().ownText()
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }

    val getHandUrl: String
        get() = try {
            document.select("div.subtitle").last().select(A_KEY).first().attr(A_HREF)
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, e.message)
            ""
        }
    val onLineState: Boolean
        get() = try {
            document.select("div.subtitle").last().select(IMG_GIF).after(IMG_ALT).first()
            .attr(IMG_ALT) == "ONLINE"
} catch (e: Exception) {
    BuglyLog.e(BuildConfig.FLAVOR, e.message)
    false
}
    val getPraiseUrl: String
        get() = try {
        document.select("div.subtitle").last().getElementsContainingOwnText("顶")
            .attr(A_HREF)
} catch (e: Exception) {
    BuglyLog.e(BuildConfig.FLAVOR, e.message)
    ""
}
    val getFavoriteUrl: String
        get() = try {
         document.getElementsContainingOwnText("收藏").last().attr(A_HREF)
} catch (e: Exception) {
    BuglyLog.e(BuildConfig.FLAVOR, e.message)
    ""
}
    val getShareUrl: String
        get() = try {
          document.getElementsContainingOwnText("分享").last().attr(A_HREF)
} catch (e: Exception) {
    BuglyLog.e(BuildConfig.FLAVOR, e.message)
    ""
}
    val praiseSize: String
        get() = try {
        Regex("([()])").split(
            document.select("div.subtitle").last().ownText().split(" ").last()
        )[1]
} catch (e: Exception) {
    BuglyLog.e(BuildConfig.FLAVOR, e.message)
    ""
}
    val reward: String?
        get() {
            val operatingData = document.select("div.content").first().toString()
            return if (operatingData.contains("[悬赏]")) {
                Jsoup.parse(
                    matchValue(
                        operatingData,
                        "[悬赏]",
                        "[标题]",
                        true
                    )
                        .removeSuffix("[标题]")
                ).body().text()
            } else {
                null
            }
        }
    val giftMoney: String?
        get() {
            val operatingData = document.select("div.content").first().toString()
            return if (operatingData.contains("礼金")) {
                return Jsoup.parse(
                    operatingData.substring(
                        0
                        , operatingData.indexOf("[标题]")
                    ) + "</div>"
                ).body().text()
            } else {
                null
            }
        }
    val time: String?
        get() {
            val operatingData = document.select("div.content").first().toString()
            return if (operatingData.contains("[时间]")) {
                return Jsoup.parse(
                    "发布时间：${matchValue(
                        operatingData,
                        "[时间]",
                        "<!--listS-->",
                        true
                    )
                        .removePrefix("[时间]")
                        .removeSuffix("<!--listS-->")}</div>"
                ).body().text()
            } else {
                null
            }
        }
    val content: String
        get() {
            val content = document.select("div.bbscontent")
            var str = matchValue(
                content.toString(),
                "<!--listS-->",
                "<!--listE-->",
                false
            )
            val annex = content.select("div.line")
            val img = annex.select(IMG_JPG).after("src")
            img.forEach { i ->
                str += "<br/>$i</img>"
            }
            return Jsoup.parse(str).body().toString()
        }

    val downLoad: List<DownloadBean>
        get() {
            val urlList = document.getElementsContainingOwnText("次)")
            val list = arrayListOf<DownloadBean>()
            urlList.forEach {
                if (it.hasClass("line")) {
                    val url = it.select(A_KEY).attr(A_HREF)
                    val name = it.ownText().substring(0, it.ownText().indexOf("("))
                    val description =
                        "<div>" + it.ownText().substring(it.ownText().indexOf("次)"))
                            .removePrefix("次)")
                    list.add(
                        DownloadBean(
                            Jsoup.parse(name).body().text(),
                            url,
                            Jsoup.parse(description).body().text()
                        )
                    )
                }
            }
            return list
        }

    val isHaveMore: Boolean
        get() {
            val more = document.select("div.content").last().select("div.more")
            return more.size > 0
        }

    fun getHandImage(doc: Document): String {
        return doc.select("div.content").select(IMG_JPG).first().attr("src")
    }

    fun getCommitLastFloor(doc: Document): String? {
        val commitList =
            matchValue(
                doc.body().toString(),
                "<!--listS-->",
                "<!--listE-->",
                false
            )
        val select = Jsoup.parse(commitList).select("div.line1,div.line2")
        return if (select.size > 0) {
            val value = select.first()
            return convertText(
                Regex("([\\[\\]])").split(value.text())[1].replace(
                    "楼",
                    ""
                )
            ).toString()
        } else {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCommitListData(doc: Document): List<CommitListBean> {
        val array = arrayListOf<CommitListBean>()
        doc.select("div.line1,div.line2").forEach {
            val url = it.select(A_KEY).first().attr(A_HREF)
            val auth = it.select(A_KEY).last()
            val floor = Regex("([\\[\\]])").split(it.text())[1].replace("楼", "")
            var b = ""
            val bEts = it.select("b")
            if (bEts.hasText()) {
                b = bEts.text()
            }
            auth.select(IMG_GIF).remove()
            val authString = auth.html()
            val avatar = auth.attr(A_HREF)
            val time = it.text().split(" ").last()
            val date = it.text().split(" ")[it.text().split(" ").lastIndex - 1]
            val format = SimpleDateFormat("yyyy-MM-ddHH:m")
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val dateObj = format.parse("$year-$date$time")
            val content = matchValue(
                it.toString()
                , "回</a>]"
                , "<br>"
                , false
            ).removePrefix("回</a>]")
                .removeSuffix("<br>")
            array.add(
                CommitListBean(
                    convertText(floor),
                    url,
                    authString,
                    avatar,
                    DateFormatHelper.format(dateObj!!),
                    content, b
                )
            )
        }
        return array
    }


    private fun convertText(floor: String): Int {
        return try {
            when (floor) {
                "沙发" -> 1
                "椅子" -> 2
                "板凳" -> 3
                else -> floor.toInt()
            }
        } catch (e: NumberFormatException) {
            0
        }
    }


}