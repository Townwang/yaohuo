package com.townwang.yaohuoapi.interceptor

import android.content.Context
import com.townwang.yaohuoapi.manager.CookieManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

class AddCookiesInterceptor(private val mContext: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("cookie", CookieManager(mContext)
            .getCookie(request.url.toString(), request.url.host,request.url.encodedPath))
        return chain.proceed(builder.build())
    }
}