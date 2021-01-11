package com.townwang.yaohuo.di.interceptor

import com.townwang.yaohuo.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NetCookiesInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val oldUrl: HttpUrl = request.url()
        val builder = request.newBuilder()
        val headerValues = request.headers("url_name")
        return if (headerValues.isNotEmpty()) {
            builder.removeHeader("url_name")
            val newBaseUrl = when (headerValues[0]) {
                "town" -> {
                    HttpUrl.parse(BuildConfig.BASE_URL)
                }
                "cn" -> {
                    HttpUrl.parse(BuildConfig.CN_URL)
                }
                else -> {
                    builder.addHeader(
                        "Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
                    )
                    builder.addHeader(":authority", "yaohuo.me")
                    builder.addHeader(":scheme", "https")
                    builder.addHeader("Accept-Encoding", "gzip, deflate, br")
                    builder.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    builder.addHeader("Cache-Control", "max-age=0")
                    builder.addHeader("Connection", "keep-alive")
                    builder.addHeader(
                        "user-agent",
                        "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14"
                    )
                    HttpUrl.parse(BuildConfig.BASE_YAOHUO_URL)
                }
            }
            val newUrl = newBaseUrl?.port()?.let {
                oldUrl.newBuilder().scheme(newBaseUrl.scheme())
                    .host(newBaseUrl.host())
                    .port(it)
                    .build()
            }
            chain.proceed(builder.url(newUrl).build())
        } else {
            chain.proceed(request)
        }
    }
}