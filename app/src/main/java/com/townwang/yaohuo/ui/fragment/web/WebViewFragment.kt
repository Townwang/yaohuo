package com.townwang.yaohuo.ui.fragment.web
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.WEB_VIEW_URL_KEY
import com.townwang.yaohuo.common.WEB_VIEW_URL_TITLE
import com.townwang.yaohuo.common.getUrlString
import com.townwang.yaohuo.common.helper.clearNotificaion
import com.townwang.yaohuo.common.work
import kotlinx.android.synthetic.main.fragment_webview.*
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
        val web = WebViewHelper(requireContext(), content).apply {
            shouldOverrideUrlLoading = false
        }
        web.onStartListener = {
            (activity as AppCompatActivity).work {
                supportActionBar.work {
                    title = it ?: requireArguments().getString(WEB_VIEW_URL_TITLE, "")
                    setDisplayHomeAsUpEnabled(true)
                }
            }
            progressBar?.visibility = View.VISIBLE
            progressBar?.alpha = 1.0f
        }
        web.onLoadingListener = {
            web.startProgressAnimation(progressBar, it)
        }
        web.onFinishedListener = {
            progressBar?.progress = it
            web.startDismissAnimation(progressBar, it)
        }
        web.onDownloadListener = { url, contentDisposition, mimeType,cookie ->
            Snackbar.make(requireView(),"正在下载文件...",Snackbar.LENGTH_SHORT).show()
            downloadBySystem(url, contentDisposition, mimeType,cookie)
        }

        refreshLayout?.setOnRefreshListener {
            refreshLayout?.finishRefresh()
            web.setUrl(getUrlString(requireArguments().getString(WEB_VIEW_URL_KEY, "")))
        }
        refreshLayout?.setEnableLoadMore(false)
        refreshLayout?.autoRefresh()
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
        cookie:String?
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDescription(requireArguments().getString(WEB_VIEW_URL_TITLE, ""))
        request.setAllowedOverMetered(true)
        request.setVisibleInDownloadsUi(true)
        request.setAllowedOverRoaming(true)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        cookie?.let {
            request.addRequestHeader("cookie", cookie)
        }
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        requireActivity().onBackPressed()
    }

}