package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.ui.activity.ActivityWebView
import com.townwang.yaohuo.ui.weight.htmltext.GlideHtmlImageLoader
import com.townwang.yaohuo.ui.weight.htmltext.HtmlText
import com.townwang.yaohuo.ui.weight.htmltext.OnTagClickListener
import com.townwang.yaohuo.ui.weight.htmltext.TextViewFixTouchConsume
import kotlinx.android.synthetic.main.item_comment_data.view.*

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): CommentViewHolder {
            return CommentViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_comment_data, parent, false)
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(data: CommitListBean) {
        Log.d("解析", "评论：$data")
        itemView.apply {
            auth.movementMethod = TextViewFixTouchConsume.LocalLinkMovementMethod.instance
            HtmlText.from(data.auth).into(auth)
            floor.text = "${data.floor}楼"
            reward.text = data.b
            comment_tv.movementMethod = TextViewFixTouchConsume.LocalLinkMovementMethod.instance
            HtmlText.from(data.content + " <font color='#BDBDBD'>${data.time}</font>")
                .setImageLoader(GlideHtmlImageLoader(itemView.context, resources, comment_tv))
                .setOnTagClickListener(object : OnTagClickListener {
                    override fun onImageClick(
                        context: Context,
                        imageUrlList: List<String>,
                        position: Int
                    ) {
                        ActivityCompat.startActivity(
                            context, Intent(
                                context, ActivityWebView::class.java
                            ).apply {
                                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                                putExtra(WEB_VIEW_URL_KEY, imageUrlList[position])
                                putExtra(WEB_VIEW_URL_TITLE, data.auth)
                            }, null
                        )
                    }

                    override fun onLinkClick(context: Context, url: String) {
                        ActivityCompat.startActivity(
                            context, Intent(
                                context, ActivityWebView::class.java
                            ).apply {
                                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                                putExtra(WEB_VIEW_URL_KEY, url)
                                putExtra(WEB_VIEW_URL_TITLE, data.auth)
                            }, null
                        )
                    }
                }).into(comment_tv)
            getAvatar(userImg)
            if (userImg.drawable != null) {
                startAnimator(userImg.drawable)
            }
        }
    }

    private fun getAvatar(img: ImageView) {
        Glide.with(itemView)
            .load(R.drawable.avatar)
            .apply(options)
            .apply(RequestOptions.bitmapTransform(CircleCrop())).into(img)

//        GlobalScope.launch(Dispatchers.IO) {
//            var doc: Document? = null
//            try {
//                @Suppress("UNCHECKED_CAST") val bbs =
//                    Jsoup.connect(
//                        BuildConfig.BASE_YAOHUO_URL + handUrl.substring(
//                            1,
//                            handUrl.length
//                        )
//                    ).timeout(30000).cookies(
//                        App.getContext().getSharedPreferences(
//                            COOKIE_KEY,
//                            Context.MODE_PRIVATE
//                        ).all as MutableMap<String, String>?
//                    )
//                val response = bbs.execute()
//                doc = if (response.statusCode() == 200) {
//                    bbs.get()
//                } else {
//                    null
//                }
//            } catch (e: IOException) {
//                Log.e("头像请求", "出现异常  ${e.message}")
//            }
//            val url = doc?.select("div.content")?.select(IMG_JPG)?.first()
//            handler.post {
//                if (url != null) {
//                    Glide.with(itemView)
//                        .load(
//                            BuildConfig.BASE_YAOHUO_URL + url.attr("src")
//                                .substring(1, url.attr("src").length)
//                        )
//                        .apply(
//                            RequestOptions.bitmapTransform(CircleCrop())
//                                .error(R.drawable.anim_vector_android_blue_senior)
//                        )
//                        .into(img)
//                }
//            }
//        }
    }
}