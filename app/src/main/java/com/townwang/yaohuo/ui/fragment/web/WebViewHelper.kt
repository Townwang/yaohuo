package com.townwang.yaohuo.ui.fragment.web

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.YaoApplication
import com.townwang.yaohuo.ui.activity.ActivityWebView
import com.townwang.yaohuoapi.BuildConfig.BASE_YAOHUO_URL
import com.townwang.yaohuoapi.COOKIE_KEY
import com.townwang.yaohuoapi.IMG_GIF
import com.townwang.yaohuoapi.WEB_VIEW_URL_KEY
import com.townwang.yaohuoapi.WEB_VIEW_URL_TITLE
import org.jsoup.Jsoup
import org.jsoup.helper.HttpConnection
import java.net.URL


typealias OnDownloadListener = (
    url: String, contentDisposition: String,
    mimeType: String,
    cookie: String?
) -> Unit

typealias OnFinishedListener = (newProgress: Int) -> Unit
typealias OnLoadingListener = (newProgress: Int) -> Unit
typealias OnStartListener = (title: String?) -> Unit

@SuppressLint("SetJavaScriptEnabled", "UseCompatLoadingForDrawables")
class WebViewHelper(context: Context, var webView: WebView) {
    var onDownloadListener: OnDownloadListener? = null
    var onLoadingListener: OnLoadingListener? = null
    var onFinishedListener: OnFinishedListener? = null
    var onStartListener: OnStartListener? = null

    var shouldOverrideUrlLoading = false

    var isAnimStart = false
    var currentProgress = 0

    init {
        webView.settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            displayZoomControls = true
            javaScriptEnabled = true
            builtInZoomControls = false
            setSupportZoom(false)
            domStorageEnabled = true
            mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = HttpConnection.DEFAULT_UA
            blockNetworkImage = false
            mediaPlaybackRequiresUserGesture = false
            textZoom = 100
        }
//        webView.webChromeClient.setOnToggledFullscreen(object : ToggledFullscreenCallback() {
//            fun toggledFullscreen(fullscreen: Boolean) {
//                mImageCloseVideo.setVisibility(if (fullscreen) View.VISIBLE else View.GONE)
//                if (fullscreen) {
//                    getWindow().setFlags(
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN
//                    )
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
//                } else {
//                    getWindow().setFlags(
//                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                        WindowManager.LayoutParams.FLAG_FULLSCREEN
//                    )
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                }
//            }
//        })
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                currentProgress = newProgress
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true
                    onFinishedListener?.invoke(newProgress)
                } else {
                    onLoadingListener?.invoke(newProgress)
                }
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                onStartListener?.invoke(view?.title)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (shouldOverrideUrlLoading) {
                    ActivityCompat.startActivity(
                        context, Intent(
                            context, ActivityWebView::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            putExtra(WEB_VIEW_URL_KEY, url)
                            putExtra(WEB_VIEW_URL_TITLE, view.title)
                        }, null
                    )
                }
                return shouldOverrideUrlLoading
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
                super.onReceivedSslError(view, handler, error)
            }

        }
    }

    private fun getNewContent(halters: String): String {
        val css ="""
            <style type="text/css">*{font-size: 14px;max-width: 100%;margin: 0;padding: 0;outline: none;cursor: pointer;}.main{width: 90%;margin:0 auto;}p:empty{line-height:0;}p{margin:10px 0;}"</style>
        """.trimIndent()
        val html ="""
            <html><head><meta charset="utf-8" name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>$css</head><body>${halters}</body></html>
        """.trimIndent()
        val doc = Jsoup.parse(html)
        val elements = doc.getElementsByTag("img")
        elements.forEach { img ->
            if (img.`is`(IMG_GIF) && img.attr("src").contains("face/")) {
                img.attr("width", "10%")
            } else {
                img.attr("width", "100%").attr("height", "auto")
            }
        }
        return doc.html()
    }

    fun setUrl(urlService: String): WebView {
        val url = URL(urlService)
        val cookieMaps =
            YaoApplication.getContext().getSharedPreferences(COOKIE_KEY, Context.MODE_PRIVATE)
        val cookie = cookieMaps.getString(url.host, "")
        syncCookie(url.host, cookie)
        webView.loadUrl(urlService)
        webView.setDownloadListener { urlLink, _, contentDisposition, mistype, _ ->

            onDownloadListener?.invoke(urlLink, contentDisposition, mistype, cookie)
        }
        return webView
    }

    fun setHtmlCode(htmlCode: String): WebView? {
        val cookieMaps =
            YaoApplication.getContext().getSharedPreferences(COOKIE_KEY, Context.MODE_PRIVATE)
        val cookie = cookieMaps.getString("yaohuo.me", "")
        syncCookie("yaohuo.me", cookie)
        webView.loadDataWithBaseURL(BASE_YAOHUO_URL, getNewContent(htmlCode), "text/html", "UTF-8", null)
        return webView
    }

    /**
     * 将cookie同步到WebView
     * @param url WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    private fun syncCookie(url: String?, cookie: String?) {
        val cookieArray = cookie?.split(";") // 多个Cookie是使用分号分隔的
        cookieArray?.forEach {
            CookieManager.getInstance()
                .setCookie(url, it) // 设置 Cookie
        }
    }


    /**
     * progressBar递增动画
     */
    fun startProgressAnimation(mProgressBar: ProgressBar?, newProgress: Int) {
        mProgressBar ?: return
        val animator =
            ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    /**
     * progressBar消失动画
     */
    fun startDismissAnimation(mProgressBar: ProgressBar?, progress: Int) {
        mProgressBar ?: return
        val anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f)
        anim.duration = 1500 // 动画时长
        anim.interpolator = DecelerateInterpolator() // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction // 0.0f ~ 1.0f
            val offset = 100 - progress
            mProgressBar.progress = (progress + offset * fraction).toInt()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                // 动画结束
                mProgressBar.progress = 0
                mProgressBar.visibility = View.GONE
                isAnimStart = false
            }
        })
        anim.start()
    }
}