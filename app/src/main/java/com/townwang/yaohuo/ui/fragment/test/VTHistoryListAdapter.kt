package com.townwang.yaohuo.ui.fragment.test

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.townwang.yaohuo.repo.data.HomeData

val itemDiffCallback = object : DiffUtil.ItemCallback<HomeData>() {
    override fun areItemsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
        return oldItem == newItem
    }
}

class VTHistoryListAdapter : PagedListAdapter<HomeData, VTHistoryViewHolder>(itemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VTHistoryViewHolder {
        return VTHistoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: VTHistoryViewHolder, position: Int) {
        holder.bind(currentList?.get(position) ?: return)
    }
}