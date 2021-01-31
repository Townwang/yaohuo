package com.townwang.yaohuo.ui.fragment.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.options
import com.townwang.yaohuo.common.startAnimator
import com.townwang.yaohuo.databinding.ItemThemeDataBinding
import com.townwang.yaohuo.repo.data.ThemeList
import kotlinx.android.synthetic.main.item_theme_data.view.*

class ThemeViewHolder(val binding: ItemThemeDataBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): ThemeViewHolder {
          val  binding = ItemThemeDataBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ThemeViewHolder(binding)
        }
    }
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