package com.townwang.yaohuo.di.interceptor

import com.townwang.yaohuo.BuildConfig
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
                "cn" -> {
                    BuildConfig.CN_URL.toHttpUrlOrNull()
                }
                else -> {
                    builder.addHeader("accept", "*/*")
                    builder.addHeader("accept-encoding", "gzip, deflate, br")
                    builder.addHeader("accept-encoding", "gzip, deflate, br")
                    builder.addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8")
                    builder.addHeader("origin", "https://yaohuo.me")
                    builder.addHeader("sec-fetch-dest", "empty")
                    builder.addHeader("sec-fetch-mode", "cors")
                    builder.addHeader("sec-fetch-site", "cross-site")
                    builder.addHeader("content-type", "application/x-www-form-urlencoded")
                    builder.addHeader(
                        "user-agent",
                        "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Mobile Safari/537.36"
                    )
                    BuildConfig.BASE_YAOHUO_URL.toHttpUrlOrNull()
                }
            }
            val newUrl = newBaseUrl?.port?.let {
                oldUrl.newBuilder().scheme(newBaseUrl.scheme)
                    .host(newBaseUrl.host)
                    .port(it)
                    .build()
            }
            chain.proceed(builder.url(newUrl!!).build())
        } else {
            chain.proceed(request)
        }
    }
}