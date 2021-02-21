package com.townwang.yaohuo.di.interceptor

import android.content.Context
import android.text.TextUtils
import com.townwang.yaohuo.common.COOKIE_KEY
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
class SaveCookiesInterceptor(private val mContext: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val headerValues = request.headers("loginCookie")
        if (headerValues.isNotEmpty()) {
            builder.removeHeader("loginCookie")
            if (headerValues[0] == "town") {
                val response = chain.proceed(request)
                if (response.headers("set-cookie").isNotEmpty()) {
                    val cookies = response.headers("set-cookie")
                    val cookie = encodeCookie(cookies)
                    saveCookie(request.url.toString(), request.url.host, cookie)
                }
                response.close()
            }
        }
        return chain.proceed(builder.build())
    }

    //整合cookie为唯一字符串
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
    private fun saveCookie(
        url: String,
        domain: String,
        cookies: String
    ) {
        val sp = mContext.getSharedPreferences(
            COOKIE_KEY,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("url is null.")
        } else {
            editor.putString(url, cookies)
        }
        if (!TextUtils.isEmpty(domain)) {
            editor.putString(domain, cookies)
        }
        editor.apply()
    }

}