package com.townwang.yaohuoapi.manager

import android.content.Context
import android.os.Build
import com.townwang.yaohuoapi.BuildConfig
import com.townwang.yaohuoapi.COOKIE_KEY
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CookieManager(private val mContext: Context) {
    private val sharedPreferences = mContext.getSharedPreferences(COOKIE_KEY, Context.MODE_PRIVATE)

    fun getCookie(url: String, domain: String,encodedPath:String): String {
        return when {
            sharedPreferences.contains(url) -> {
                cookieSetTime(encodedPath,sharedPreferences.getString(url, ""))
            }
            else -> {
                cookieSetTime(encodedPath,sharedPreferences.getString(domain, ""))
            }
        }
    }

    fun saveCookie(url: String, domain: String, cookies: List<String>) {
        val cookie = encodeCookie(cookies)
        sharedPreferences.edit().apply {
            putString(url, cookie)
            putString(domain, cookie)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                apply()
            }
        }
    }

    private fun cookieSetTime(encodedPath:String,cookie: String?): String {
        val stringBuilder = StringBuilder()
        var variable = 0
        cookie?.split(";")?.forEach {
            if (it.split("=")[0] == " expires") {
                val date = Date()
                val cal = Calendar.getInstance()
                cal.time = date //设置起时间
                if (variable == 0) {
                    cal.add(Calendar.YEAR, 1)
                }
                val gmtDateFormat: DateFormat =
                    SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss z", Locale.ENGLISH)
                gmtDateFormat.timeZone = TimeZone.getTimeZone("GMT")
                val dateStr: String = gmtDateFormat.format(cal.time)
                stringBuilder.append(" expires=$dateStr")
                variable += 1
            } else if (it.split("=")[0] == " path") {
                stringBuilder.append(" path=$encodedPath")
            } else {
                stringBuilder.append(it)
            }
            when (it.split("=")[0]) {
                BuildConfig.YH_COOKIE_SID -> {
                    mContext.config(BuildConfig.YH_COOKIE_SID, it.split("=")[1])
                }
                BuildConfig.YH_COOKIE_SESSION_ID -> {
                    mContext.config(BuildConfig.YH_COOKIE_SESSION_ID, it.split("=")[1])
                }
                BuildConfig.YH_COOKIE_GUID -> {
                    mContext.config(BuildConfig.YH_COOKIE_GUID, it.split("=")[1])
                }
            }
            stringBuilder.append(";")
        }
        return stringBuilder.toString()
    }

    private fun encodeCookie(cookies: List<String>): String {
        val sb = StringBuilder()
        val set = hashSetOf<String>()
        for (cookie in cookies) {
            val arr = cookie.split(";").toTypedArray()
            for (s in arr) {
                if (set.contains(s)) continue
                set.add(s)
            }
        }
        val ite = set.iterator()
        while (ite.hasNext()) {
            val cookie = ite.next()
            sb.append(cookie).append(";")
        }
        val last = sb.lastIndexOf(";")
        if (sb.length - 1 == last) {
            sb.deleteCharAt(last)
        }
        return sb.toString()
    }
}