package com.townwang.yaohuo.ui.fragment.details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.WEB_VIEW_URL_KEY
import com.townwang.yaohuo.common.WEB_VIEW_URL_TITLE
import com.townwang.yaohuo.common.startAnimator
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
            if (userImg.drawable != null) {
                startAnimator(userImg.drawable)
            }
        }
    }
}