package com.townwang.yaohuoapi.interceptor
import com.townwang.yaohuoapi.BuildConfig
import com.townwang.yaohuoapi.RANDOM_UA
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RequestHeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val oldUrl: HttpUrl = request.url
        val builder = request.newBuilder()
        val headerValues = request.headers("url_name")
        builder.removeHeader("url_name")
        val newBaseUrl = when (
            if (headerValues.isNotEmpty()) {
                headerValues.first()
            } else "yao") {
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
                builder.removeHeader("accept")
                builder.removeHeader("accept-encoding")
                builder.removeHeader("accept-language")
                builder.removeHeader("Connection")
                builder.removeHeader("content-type")

                builder.addHeader("accept", "*/*")
                builder.addHeader("accept-language", "zh-CN")
                builder.addHeader("Connection", "keep-alive")
                builder.addHeader(
                    "content-type",
                    "application/x-www-form-urlencoded,text/html; charset=utf-8"
                )
                BuildConfig.BASE_YAOHUO_URL.toHttpUrlOrNull()
            }
        }
        builder.addHeader("user-agent", RANDOM_UA.random())
        val newUrl = newBaseUrl?.port?.let {
            oldUrl.newBuilder()
                .scheme(newBaseUrl.scheme)
                .build()
        }
        return chain.proceed(builder.url(newUrl!!).build())
    }
}