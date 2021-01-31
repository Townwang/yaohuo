package com.townwang.yaohuo.ui.fragment.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.databinding.ItemSelectListDataBinding
import com.townwang.yaohuo.repo.data.SelectBean

class SelectViewHolder(val binding: ItemSelectListDataBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): SelectViewHolder {
          val  binding = ItemSelectListDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SelectViewHolder(binding)
        }
    }

    fun bind(data: SelectBean) {
        binding.title.text = data.string
    }
}