package com.townwang.yaohuo.ui.fragment.send

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.databinding.ItemSelectListDataBinding
import com.townwang.yaohuo.repo.data.SelectBean
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class SelectViewHolder(view:View) : RecyclerView.ViewHolder(view) {
    val binding: ItemSelectListDataBinding by viewbind()

    fun bind(data: SelectBean) {
        binding.title.text = data.string
    }
}