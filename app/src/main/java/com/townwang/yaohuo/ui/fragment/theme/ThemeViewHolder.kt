package com.townwang.yaohuo.ui.fragment.theme

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.townwang.binding.ext.viewbind
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.startAnimator
import com.townwang.yaohuo.databinding.ItemThemeDataBinding
import com.townwang.yaohuo.repo.data.ThemeList

class ThemeViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val binding: ItemThemeDataBinding by viewbind()
    fun bind(data: ThemeList) {
        binding.layout.setBackgroundColor(ContextCompat.getColor(binding.root.context,data.color))
        Glide.with(binding.root)
            .load(data.icon)
            .apply(RequestOptions().placeholder(R.drawable.anim_vector_theme_icon))
            .into(binding.icon)
        startAnimator(binding.icon.drawable)
        binding.title.text = data.title
    }
}