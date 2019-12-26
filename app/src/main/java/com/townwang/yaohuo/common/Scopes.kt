package com.townwang.yaohuo.common

import android.util.Log
import com.townwang.yaohuo.api.JsApi
import com.townwang.yaohuo.repo.data.Neice
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private val appJob = SupervisorJob()
private val networkJob = Job(appJob)
private val repoJob = Job(appJob)


private val repoScope =
    CoroutineScope((Executors.newFixedThreadPool(3).asCoroutineDispatcher()) + repoJob)
val networkScope =
    CoroutineScope(Executors.newCachedThreadPool().asCoroutineDispatcher() + networkJob)

suspend fun <T> withRepoContext(block: suspend CoroutineScope.() -> T): T =
    withContext(repoScope.coroutineContext) { block() }

/**
 * Server response with nothing
 */
class NullResponseBodyException : Exception()


suspend fun <T : Neice> Call<T>.getResp() = withContext(networkScope.coroutineContext) {
    suspendCoroutine<T> {
        kotlin.runCatching {
            val result = execute()
            if (!isVpnUsed()) {
                if (result.isSuccessful) {
                    val body = result.body()
                    if (body != null) {
                        if (body.code == 200) {
                            it.resume(body)
                        } else {
                            it.resumeWithException(ApiErrorException(body.code, body.message))
                        }
                    } else {
                        it.resumeWithException(NullResponseBodyException())
                    }
                } else {
                    it.resumeWithException(NetworkFailureException(result.message()))
                }
            } else {
                it.resumeWithException(UseVPNException("当前网络环境不安全，请切换安全环境"))
            }
        }.onFailure { error ->
            it.resumeWithException(error)
        }
    }
}

/**
 * 检测是否使用VPN
 * @return Boolean  true 使用 false 未使用
 */
fun isVpnUsed(): Boolean {
    try {
        val niList = NetworkInterface.getNetworkInterfaces()
        niList?.run {
            Collections.list(niList).forEach {
                if (it.isUp || it.interfaceAddresses.size != 0) {
                    if (it.name == "tun0" || it.name == "ppp0") {
                        return true
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

/**
 * 在网络上执行请求 以及设置共同参数
 */
suspend fun Connection.getResp(): Document = withContext(networkScope.coroutineContext) {
    suspendCoroutine<Document> {
        kotlin.runCatching {
            val result = execute()
            header(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
            )
            header("Accept-Encoding", "gzip, deflate, br")
            header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            header("Cache-Control", "max-age=0")
            header("Connection", "keep-alive")
            val cookies = getCookie()?.iterator()
            var cookieString = ""
            cookies?.run {
                while (hasNext()) {
                    val entry = next()
                    cookie(entry.key, entry.value)
                    cookieString += entry.key + "=" + entry.value + ";"
                }
            }
            header("Cookie", cookieString.substring(0, cookieString.length - 1))
            header("Host", "yaohuo.me")
            header("Upgrade-Insecure-Requests", "1")
            userAgent("Mozilla/5.0 (Linux; Android 4.4.4; SAMSUNG-SM-N900A Build/tt) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36")
            timeout(3000)
            if (result.statusCode() == 200) {
                val doc = get()
                Log.d("解析", doc.html())
                if (!isVpnUsed()) {
                    if (doc.title() != "Error 404 (Not Found)") {
                        if (doc.title() != "访问验证") {
                            if (!doc.body().text().contains("身份失效了，请重新登录网站")) {
                                if (!doc.html().contains("请先登录网站")) {
                                    if (!doc.html().contains("请开启JavaScript并刷新该页")) {
                                        if (!doc.body().text().contains("正在审核中")) {
                                            if (!doc.body().text().contains("请输入您的密码")) {
                                                it.resume(doc)
                                            } else {
                                                it.resumeWithException(
                                                    ApiErrorException(
                                                        1004,
                                                        "IP已经更改，需要校验密码"
                                                    )
                                                )
                                            }
                                        } else {
                                            it.resumeWithException(
                                                ApiErrorException(
                                                    1003,
                                                    "帖子还在审核中.."
                                                )
                                            )
                                        }
                                    } else {
                                        it.resumeWithException(
                                            ApiErrorException(
                                                1002,
                                                "Cookie过期，需要重新登录"
                                            )
                                        )
                                    }
                                } else {
                                    it.resumeWithException(ApiErrorException(1002, "请先登录网站"))
                                }
                            } else {
                                it.resumeWithException(ApiErrorException(1002, "身份失效了，请重新登录"))
                            }
                        } else {
                            it.resumeWithException(ApiErrorException(1001, "访问验证"))
                        }
                    } else {
                        it.resumeWithException(ApiErrorException(999, "找不到此帖了。"))
                    }
                } else {
                    it.resumeWithException(UseVPNException("当前网络环境不安全，请切换安全环境"))
                }
            } else {
                it.resumeWithException(NetworkFailureException("网络故障"))
            }

        }.onFailure { error ->
            it.resumeWithException(error)
        }
    }
}
