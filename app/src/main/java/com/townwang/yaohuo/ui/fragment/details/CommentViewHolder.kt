package com.townwang.yaohuo.ui.fragment.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.databinding.ItemCommentDataBinding
class CommentViewHolder(val binding: ItemCommentDataBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): CommentViewHolder {
           val binding = ItemCommentDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CommentViewHolder(binding)
        }
    }

}