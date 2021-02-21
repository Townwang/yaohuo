package com.townwang.yaohuo.di.interceptor

import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.RANDOM_IP
import com.townwang.yaohuo.common.RANDOM_UA
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NetCookiesInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val oldUrl: HttpUrl = request.url
        val builder = request.newBuilder()
        val headerValues = request.headers("url_name")
        return if (headerValues.isNotEmpty()) {
            builder.removeHeader("url_name")
            val newBaseUrl = when (headerValues[0]) {
                "town" -> {
                    BuildConfig.BASE_URL.toHttpUrlOrNull()
                }
                "yao_cdn" -> {
                    builder.addHeader("accept-language", "zh-CN")
                    builder.addHeader("origin", BuildConfig.YAO_CDN_URL)
                    builder.addHeader("content-type", "multipart/form-data")
                    BuildConfig.YAO_CDN_URL.toHttpUrlOrNull()
                }
                "cn" -> {
                    BuildConfig.CN_URL.toHttpUrlOrNull()
                }
                else -> {
                    builder.addHeader("accept", "*/*")
                    builder.addHeader("accept-encoding", "gzip, deflate, br")
                    builder.addHeader("accept-language", "zh-CN")
                    builder.addHeader("Connection", "keep-alive")
                    builder.addHeader("content-type", "application/x-www-form-urlencoded")
                    BuildConfig.BASE_YAOHUO_URL.toHttpUrlOrNull()
                }
            }
            builder.addHeader("user-agent", RANDOM_UA.random())
            val newUrl = newBaseUrl?.port?.let {
                oldUrl.newBuilder()
                    .scheme(newBaseUrl.scheme)
                    .host(RANDOM_IP.random())
                    .port(it)
                    .build()
            }
            chain.proceed(builder.url(newUrl!!).build())
        } else {
            chain.proceed(request)
        }
    }
}