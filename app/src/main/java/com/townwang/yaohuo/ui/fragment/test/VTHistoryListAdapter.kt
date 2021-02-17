package com.townwang.yaohuo.ui.fragment.test

//val itemDiffCallback = object : DiffUtil.ItemCallback<HomeData>() {
//    override fun areItemsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
//        return oldItem == newItem
//    }
//
//    override fun areContentsTheSame(oldItem: HomeData, newItem: HomeData): Boolean {
//        return oldItem == newItem
//    }
//}
//
//class VTHistoryListAdapter : PagedListAdapter<HomeData, VTHistoryViewHolder>(itemDiffCallback) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VTHistoryViewHolder {
//        return VTHistoryViewHolder.create(parent)
//    }
//
//    override fun onBindViewHolder(holder: VTHistoryViewHolder, position: Int) {
//        holder.bind(currentList?.get(position) ?: return)
//    }
//}