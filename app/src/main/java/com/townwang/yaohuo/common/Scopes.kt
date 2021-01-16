package com.townwang.yaohuo.common

import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.helper.isHaveMsg
import com.townwang.yaohuo.repo.data.Niece
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
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
    CoroutineScope(
        (Executors.newFixedThreadPool(3)
            .asCoroutineDispatcher()) + repoJob
    )
val networkScope =
    CoroutineScope(
        Executors.newCachedThreadPool()
            .asCoroutineDispatcher() + networkJob
    )

suspend fun <T> withRepoContext(block: suspend CoroutineScope.() -> T): T =
    withContext(repoScope.coroutineContext) { block() }

/**
 * Server response with nothing
 */
class NullResponseBodyException : Exception()


suspend fun <T : Niece> Call<T>.getUResp() =
    withContext(networkScope.coroutineContext) {
        suspendCoroutine<T> {
            kotlin.runCatching {
                val result = execute()
                if (isVpnUsed().not()) {
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
suspend fun <T : Document> Call<T>.getResp() = withContext(networkScope.coroutineContext) {
    suspendCoroutine<Document> {
        kotlin.runCatching {
            val result = execute()
            if (isVpnUsed().not()) {
                if (result.isSuccessful) {
                    val body = result.body()
                    val throwable = checkDoc(body)
                    if (throwable == null) {
                        it.resume(isHaveMsg(Jsoup.parse(body.toString())))
                    } else {
                        it.resumeWithException(throwable)
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

fun checkDoc(document: Element?): Throwable? {
    document ?: return null
    val doc = Jsoup.parse(document.toString())
    if (doc.title().contains(BuildConfig.YH_MATCH_404)) {
        return ApiErrorException(999, "找不到此贴了!")
    }
    if (doc.title().contains(BuildConfig.YH_MATCH_VERIFY)) {
        return ApiErrorException(1001, "访问验证")
    }
    if (doc.html().contains(BuildConfig.YH_MATCH_IDENTITY)) {
        return ApiErrorException(1002, "身份失效了，请重新登录网站")
    }
    if (doc.html().contains(BuildConfig.YH_MATCH_CHECK)) {
        return ApiErrorException(1003, "正在审核中...")
    }
    if (doc.html().contains(BuildConfig.YH_MATCH_INPUT_PSD)) {
        return ApiErrorException(1004, "IP已经更改，需要校验密码")
    }
    if (doc.html().contains(BuildConfig.YH_MATCH_LOGIN)) {
        return ApiErrorException(1005, "请先登录网站")
    }
    if (doc.html().contains(BuildConfig.YH_MATCH_COOKIE_OLD)) {
        return ApiErrorException(1006, "Cookie过期，需要重新登录")
    }
    return null
}