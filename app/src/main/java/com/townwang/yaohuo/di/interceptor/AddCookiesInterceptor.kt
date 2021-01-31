package com.townwang.yaohuo.di.interceptor

import android.content.Context
import android.text.TextUtils
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.COOKIE_KEY
import com.townwang.yaohuo.common.config
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
class AddCookiesInterceptor(private val mContext: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val cookie = getCookie(mContext, request.url.toString(), request.url.host)
        cookie?.let {
            builder.addHeader("cookie", fillTime(cookie))
        }
        return chain.proceed(builder.build())
    }


    private fun fillTime(cookie: String): String {
        val stringBuilder = StringBuilder()
        var variable = 0
        cookie.split(";").forEach {
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

    private fun getCookie(context: Context, url: String, domain: String): String? {
        val sp = context.getSharedPreferences(
            COOKIE_KEY,
            Context.MODE_PRIVATE
        )
        if (!TextUtils.isEmpty(url) &&
            sp.contains(url) &&
            !TextUtils.isEmpty(sp.getString(url, ""))
        ) {
            return sp.getString(url, "")
        }
        return if (
            !TextUtils.isEmpty(domain) &&
            sp.contains(domain) &&
            !TextUtils.isEmpty(
                sp.getString(
                    domain,
                    ""
                )
            )
        ) {
            sp.getString(domain, "")
        } else ""
    }
}