package com.townwang.yaohuo.ui.fragment.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.startAnimator
import com.townwang.yaohuo.repo.data.ThemeList
import kotlinx.android.synthetic.main.item_theme_data.view.*

class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): ThemeViewHolder {
            return ThemeViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_theme_data, parent, false))
        }
    }
    fun bind(data: ThemeList) {
        itemView.layout.setBackgroundColor(ContextCompat.getColor(itemView.context,data.color))

        Glide.with(itemView)
            .load(data.icon)
            .apply(RequestOptions().placeholder(R.drawable.anim_vector_theme_icon))
            .into(itemView.icon)
        startAnimator(itemView.icon.drawable)
        itemView.title.text = data.title
    }
}