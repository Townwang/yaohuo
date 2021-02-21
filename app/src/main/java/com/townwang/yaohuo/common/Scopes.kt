package com.townwang.yaohuo.common
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.common.utils.isHaveMsg
import com.townwang.yaohuo.repo.data.Niece
import com.townwang.yaohuo.repo.data.YaoCdnReq
import com.townwang.yaohuo.repo.enum.ErrorCode
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
        (Executors.newFixedThreadPool(50)
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
            }.onFailure { error ->
                it.resumeWithException(error)
            }
        }
    }

suspend fun Call<YaoCdnReq>.getYaoResp() =
    withContext(networkScope.coroutineContext) {
        suspendCoroutine<YaoCdnReq> {
            kotlin.runCatching {
                val result = execute()
                if (result.isSuccessful) {
                    val body = result.body()
                    if (body != null) {
                        if (body.code == 200) {
                            it.resume(body)
                        } else {
                            it.resumeWithException(
                                ApiErrorException(
                                    body.code ?: 0,
                                    body.msg ?: "上传失败"
                                )
                            )
                        }
                    } else {
                        it.resumeWithException(NullResponseBodyException())
                    }
                } else {
                    it.resumeWithException(NetworkFailureException(result.message()))
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
        }.onFailure { error ->
            it.resumeWithException(error)
        }
    }
}

fun checkDoc(document: Element?): Throwable? {
    document ?: return null
    val doc = Jsoup.parse(document.toString())
    val tip = document.select("div.tip")?.first()?.toString() ?: ""
    if (doc.title().contains(BuildConfig.YH_MATCH_404)) {
        return ApiErrorException(ErrorCode.E_1001.hashCode(), "找不到此贴了!")
    }
    if (doc.title().contains(BuildConfig.YH_MATCH_VERIFY)) {
        return ApiErrorException(ErrorCode.E_1002.hashCode(), "访问验证")
    }
    if (tip.contains(BuildConfig.YH_MATCH_IDENTITY)) {
        return ApiErrorException(ErrorCode.E_1003.hashCode(), "身份失效了，请重新登录网站")
    }
    if (tip.contains(BuildConfig.YH_MATCH_CHECK)) {
        return ApiErrorException(ErrorCode.E_1004.hashCode(), "正在审核中...")
    }
    if (tip.contains(BuildConfig.YH_MATCH_INPUT_PSD)) {
        return ApiErrorException(ErrorCode.E_1005.hashCode(), "IP已经更改，需要校验密码")
    }
    if (tip.contains(BuildConfig.YH_MATCH_LOGIN)) {
        return ApiErrorException(ErrorCode.E_1006.hashCode(), "请先登录网站")
    }
    if (tip.contains(BuildConfig.YH_MATCH_COOKIE_OLD)) {
        return ApiErrorException(ErrorCode.E_1007.hashCode(), "Cookie过期，需要重新登录")
    }
    if (tip.contains(BuildConfig.YH_MATCH_SEARCH_NO_DATA)) {
        return ApiErrorException(ErrorCode.E_1008.hashCode(), "暂无记录!")
    }
    if (tip.contains("内容跟上次发的重复！")) {
        return ApiErrorException(ErrorCode.E_1010.hashCode(), "内容跟上次发的重复！")
    }
    if (doc.body().text().contains(BuildConfig.YH_MATCH_SEND_POST_LIMIT)) {
        return ApiErrorException(ErrorCode.E_1009.hashCode(), "今天你已超过发帖限制!")
    }
    return null
}