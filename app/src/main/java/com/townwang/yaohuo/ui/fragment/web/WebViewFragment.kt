package com.townwang.yaohuo.ui.fragment.web

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.common.utils.clearNotificaion
import com.townwang.yaohuo.databinding.FragmentWebviewBinding
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind
import com.townwang.yaohuoapi.WEB_VIEW_URL_KEY
import com.townwang.yaohuoapi.WEB_VIEW_URL_TITLE

class WebViewFragment : Fragment(R.layout.fragment_webview) {
    val binding: FragmentWebviewBinding by viewbind()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val web = WebViewHelper(requireContext(), binding.content).apply {
            shouldOverrideUrlLoading = false
        }
        web.onStartListener = {
            (activity as AppCompatActivity).work {
                supportActionBar.work {
                    title = it ?: requireArguments().getString(WEB_VIEW_URL_TITLE, "")
                    setDisplayHomeAsUpEnabled(true)
                }
            }
            binding.progressBar.visibility = View.VISIBLE
            binding.progressBar.alpha = 1.0f
        }
        web.onLoadingListener = {
            web.startProgressAnimation(binding.progressBar, it)
        }
        web.onFinishedListener = {
            binding.progressBar.progress = it
            web.startDismissAnimation(binding.progressBar, it)
        }
        web.onDownloadListener = { url, contentDisposition, mimeType, cookie ->
            Snackbar.make(requireView(), "检测到一个文件需要下载", Snackbar.LENGTH_INDEFINITE)
                .setAction("下载") {
                    downloadBySystem(url, contentDisposition, mimeType, cookie)
                }.show()
        }

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.finishRefresh()
            web.setUrl(getUrlString(requireArguments().getString(WEB_VIEW_URL_KEY, "")))
        }
        binding.refreshLayout.setEnableLoadMore(false)
        binding.refreshLayout.autoRefresh()
        if ("消息" == requireArguments().getString(WEB_VIEW_URL_TITLE, "")) {
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
        mimeType: String,
        cookie: String?
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDescription(contentDisposition)
        request.setAllowedOverMetered(true)
        request.setVisibleInDownloadsUi(true)
        request.setAllowedOverRoaming(true)
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        cookie?.let {
            request.addRequestHeader("cookie", cookie)
        }
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

}