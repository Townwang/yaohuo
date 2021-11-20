package com.townwang.yaohuo.common.resolve

import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuoapi.IMG_ALT
import com.townwang.yaohuoapi.IMG_JPG
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ResolveUserInfoHelper(private val document: Document) {
    val avatar: String
        get() {
            var url = "/bbs/head/64.gif"
            try {
                document.select(IMG_JPG).forEach {
                    if (it.attr(IMG_ALT) == "头像") {
                        url = it.attr("src")
                        return@forEach
                    }
                }
            } catch (e: Exception) {
                BuglyLog.w(BuildConfig.FLAVOR, e.message)
            } finally {
                return url
            }
        }

    val userId: String
        get() = Jsoup.parse(parseContent("【用户ID】")).text()
    val userName: String
        get() = parseContent("【昵称】")
    val demonCrystal: String
        get() = parseContent("【妖晶】")
    val grade: String
        get() = parseContent("【等级】")
    val experience: String
        get() = parseContent("【经验】")
    val title: String
        get() = parseContent("【头衔】")
    val identity: String
        get() =  Jsoup.parse(parseContent("【身份】")).text()
    val purview: String
        get() = parseContent("【权限】")
    val medal: List<String>
        get(){
            val lists = arrayListOf<String>()
            lists.clear()
            Jsoup.parse(parseContent("【勋章】")).select(IMG_JPG).forEach {
                lists.add(it.attr("src"))
            }
          return lists
        }
    val sex: String
        get() = parseContent("【性别】")
    val age: String
        get() = parseContent("【年龄】")
    val accumulatedTime: String
        get() = parseContent("【积时】")
    val registrationTime: String
        get() = parseContent("【注册时间】")
    val QQNumber: String
        get() = parseContent("【QQ号】")
    val height: String
        get() = parseContent("【身高】")
    val bodyWeight: String
        get() = parseContent("【体重】")
    val constellation: String
        get() = parseContent("【星座】")
    val hobby: String
        get() = parseContent("【爱好】")
    val marriage: String
        get() = parseContent("【婚否】")
    val profession: String
        get() = parseContent("【职业】")
    val city: String
        get() = parseContent("【城市】")
    val eMail: String
        get() = parseContent("【邮箱】")

    private fun parseContent(keyword: String): String {
        var grade = ""
        try {
            document.select("div.content").first().html().split("<br>").forEach {
                if (it.contains(keyword)) {
                    grade = it.split("：").last().trim()
                    return@forEach
                }
            }
        } catch (e: Exception) {
            BuglyLog.w(BuildConfig.FLAVOR, e.message)
        } finally {
            return grade
        }
    }
}