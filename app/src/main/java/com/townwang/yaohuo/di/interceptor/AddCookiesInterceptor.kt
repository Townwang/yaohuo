package com.townwang.yaohuo.di.interceptor

import android.content.Context
import android.text.TextUtils
import com.townwang.yaohuo.common.COOKIE_KEY
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AddCookiesInterceptor(private val mContext: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val cookie = getCookie(request.url().toString(), request.url().host())
        cookie?.let {
            builder.addHeader("cookie", cookie)
        }
        return chain.proceed(builder.build())
    }

    private fun getCookie(url: String, domain: String): String? {
        val sp = mContext.getSharedPreferences(
            COOKIE_KEY,
            Context.MODE_PRIVATE
        )
        if (!TextUtils.isEmpty(url) && sp.contains(url) && !TextUtils.isEmpty(
                sp.getString(
                    url,
                    ""
                )
            )
        ) {
            return sp.getString(url, "")
        }
        return if (!TextUtils.isEmpty(domain) && sp.contains(domain) && !TextUtils.isEmpty(
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