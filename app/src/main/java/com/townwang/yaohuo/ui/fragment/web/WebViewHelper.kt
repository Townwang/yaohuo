package com.townwang.yaohuo.ui.fragment.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.townwang.yaohuo.App
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.COOKIE_KEY
import com.townwang.yaohuo.common.toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.URL


typealias OnDownloadListener = (
    url: String, contentDisposition: String,
    mimeType: String
) -> Unit

@SuppressLint("SetJavaScriptEnabled")
class WebViewHelper(context: Context,var webView: WebView? = null) {
    var onDownloadListener: OnDownloadListener? = null

    var shouldOverrideUrlLoading = false

    init {
        if (webView ==null){
            webView =  WebView(context).also {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                it.layoutParams = params
                it.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            }
        }
        webView?.settings?.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            displayZoomControls = true
            javaScriptEnabled = true
            builtInZoomControls = false
            setSupportZoom(false)
            domStorageEnabled = true
            mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView?.webChromeClient = object :WebChromeClient(){}
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (shouldOverrideUrlLoading){
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                       context.toast("该设备不支持打开此链接")
                    }
                }
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
        webView?.loadUrl(urlService)
        webView?.setDownloadListener { urlLink, _, contentDisposition, mistype, _ ->
            onDownloadListener?.invoke(urlLink, contentDisposition, mistype)
        }
        return webView!!
    }
    fun setHtmlCode(htmlCode: String): WebView? {
        val css = "<style type=\"text/css\">*{margin: 0;padding: 0;outline: none;cursor: pointer;}.main{width: 90%;margin:0 auto;}img{width: 100%;display: block;margin: 0;padding: 0;border: 0;}p:empty{line-height:0;}p{margin:10px 0;}\"</style>"
        val html = "<html><head><meta charset=\"utf-8\" name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0\"/>$css</head><body>${getNewContent(htmlCode)}</body></html>"
        webView?.loadDataWithBaseURL(BuildConfig.BASE_YAOHUO_URL,html, "text/html", "UTF-8", null)
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