package com.townwang.yaohuoapi.interceptor

import android.content.Context
import com.townwang.yaohuoapi.manager.CookieManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

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
                    CookieManager(mContext)
                        .saveCookie(
                            request.url.toString(),
                            request.url.host,
                            response.headers("set-cookie")
                        )
                }
                response.close()
            }
        }
        return chain.proceed(builder.build())
    }
}