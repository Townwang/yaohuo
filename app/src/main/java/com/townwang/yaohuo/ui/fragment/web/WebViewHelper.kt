package com.townwang.yaohuo.ui.fragment.web

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.townwang.yaohuo.App
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.COOKIE_KEY
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.URL


typealias OnDownloadListener = (
    url: String, contentDisposition: String,
    mimeType: String
) -> Unit

@SuppressLint("SetJavaScriptEnabled")
class WebViewHelper(context: Context) {
    var onDownloadListener: OnDownloadListener? = null

    var shouldOverrideUrlLoading = false

    private val webView = WebView(context).also {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        it.layoutParams = params
        it.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
    }

    init {
        webView.settings.apply {
            displayZoomControls = true
            javaScriptEnabled = true
            builtInZoomControls = false
            setSupportZoom(false)
            domStorageEnabled = true
            mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return shouldOverrideUrlLoading
            }
        }
    }


    private fun getNewContent(halters: String): String {
        val doc: Document = Jsoup.parse(halters)
        val elements: Elements = doc.getElementsByTag("img")
        for (element in elements) {
            element.attr("width", "100%").attr("height", "auto")
        }
        return doc.toString()
    }

    fun setUrl(urlService: String): WebView {
        val url = URL(urlService)
        val cookieMaps = App.getContext().getSharedPreferences(COOKIE_KEY, Context.MODE_PRIVATE)
        val cookie = cookieMaps.getString(url.host, "")
        syncCookie(url.host, cookie)
        webView.loadUrl(urlService)
        webView.setDownloadListener { urlLink, _, contentDisposition, mistype, _ ->
            onDownloadListener?.invoke(urlLink, contentDisposition, mistype)
        }
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
}