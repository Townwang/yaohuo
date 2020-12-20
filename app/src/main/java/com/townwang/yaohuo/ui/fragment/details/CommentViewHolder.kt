package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cn.droidlover.xrichtext.XRichText
import com.bumptech.glide.Glide
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.startAnimator
import com.townwang.yaohuo.common.toast
import com.townwang.yaohuo.repo.data.CommentData
import kotlinx.android.synthetic.main.item_comment_data.view.*

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): CommentViewHolder {
            return CommentViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_comment_data, parent, false)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(data: CommentData) {
        Log.d("解析", "评论：$data")
        itemView.auth.text(data.auth)
        itemView.floor.text = "${data.floor}楼"
        itemView.reward.text = data.b
        itemView.comment_tv.callback(object : XRichText.BaseClickCallback() {

            override fun onLinkClick(url: String?): Boolean {
                url ?: return true
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                itemView.context?.startActivity(intent)
                return true
            }

            override fun onImageClick(urlList: MutableList<String>?, position: Int) {
                super.onImageClick(urlList, position)
                itemView.context?.toast("图片而已，别瞎几把点了")
            }
            override fun onFix(holder: XRichText.ImageHolder?) {
                super.onFix(holder)
                holder?.style = XRichText.Style.LEFT
            }
        }).imageDownloader { url ->
            Glide.with(itemView.context)
                .asBitmap()
                .load(url)
                .submit().get()
        }.text(data.content + " <font color='#BDBDBD'>${data.time}</font>")
        getAvatar(data.avatar, itemView.userImg)
        startAnimator(itemView.userImg.drawable)
    }

    private fun getAvatar(handUrl: String, img: ImageView) {
//        Thread(Runnable {
//            val bbs = Jsoup.connect(handUrl)
////            val cookie = getCookie()?.iterator()
////            cookie?.run {
////                while (hasNext()) {
////                    val entry = next()
////                    bbs.cookie(entry.key, entry.value)
////                }
////            }
//            val doc = bbs.get()
//            val url = doc.select("div.content").select(IMG_JPG).first()
//            handler.post {
//                if (url != null) {
//                    Glide.with(itemView)
//                        .load(url.attr("src"))
//                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
//                        .into(img)
//                }
//            }
//        }).start()

    }
}