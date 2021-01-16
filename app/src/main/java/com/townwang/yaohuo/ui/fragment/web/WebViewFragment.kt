package com.townwang.yaohuo.ui.fragment.web

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.helper.clearNotificaion
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.fragment_webview.refreshLayout


class WebViewFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).work {
            supportActionBar.work {
                title = requireArguments().getString(WEB_VIEW_URL_TITLE, "")
                setDisplayHomeAsUpEnabled(true)
            }
        }
        val web = WebViewHelper(requireContext(), content).apply {
            shouldOverrideUrlLoading = false
        }
        web.onDownloadListener = { url, contentDisposition, mimeType ->
            downloadBySystem(url, contentDisposition, mimeType)
        }

        val loading = Loading("正在加载网址...").create()
        if (loading.isShowing.not()) {
            loading.show()
        }
        web.onFinishedListener = {
            if (loading.isShowing) {
                loading.dismiss()
            }
            refreshLayout?.finishRefresh()
        }
        refreshLayout?.setOnRefreshListener {
            web.setUrl(getUrlString(requireArguments().getString(WEB_VIEW_URL_KEY, "")))
        }
        refreshLayout?.setEnableLoadMore(false)
        refreshLayout?.autoRefresh()

        if ("消息" == requireArguments().getString(WEB_VIEW_URL_TITLE, "")){
            clearNotificaion()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun downloadBySystem(
        url: String,
        contentDisposition: String,
        mimeType: String
    ) {
        // 指定下载地址
        val request = DownloadManager.Request(Uri.parse(url))
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner()
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        // 设置通知栏的标题，如果不设置，默认使用文件名
        // 设置通知栏的描述
        request.setDescription(requireArguments().getString(WEB_VIEW_URL_TITLE, ""))
        // 允许在计费流量下下载
        request.setAllowedOverMetered(true)
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(true)
        // 允许漫游时下载
        request.setAllowedOverRoaming(true)
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        // 设置下载文件保存的路径和文件名
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        //        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        // 添加一个下载任务
        downloadManager.enqueue(request)
        requireActivity().onBackPressed()
    }

}