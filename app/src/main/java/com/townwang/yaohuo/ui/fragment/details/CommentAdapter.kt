package com.townwang.yaohuo.ui.fragment.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.OnItemListener
import com.townwang.yaohuo.common.T
import com.townwang.yaohuo.databinding.ItemCommentDataBinding
import com.townwang.yaohuo.repo.data.details.CommitListBean
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class CommentAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(Product.CALLBACK) {
    var onItemListener: OnItemListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflateView(parent, viewType)
        return CViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CViewHolder) {
            val product = getItem(position).t
            if (product is CommitListBean) {
                onItemListener?.invoke(holder.binding, product)
            }
        }
    }
    override fun getItemViewType(position: Int): Int = R.layout.item_comment_data

    private fun inflateView(viewGroup: ViewGroup, @LayoutRes viewType: Int): View {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return layoutInflater.inflate(viewType, viewGroup, false)
    }
}

class CViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding: ItemCommentDataBinding by viewbind()
}

data class Product(val id: Int, val t: T?) {
    companion object {
        val CALLBACK: DiffUtil.ItemCallback<Product> = object : DiffUtil.ItemCallback<Product>() {
            // 判断两个Objects 是否代表同一个item对象， 一般使用Bean的id比较
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id == newItem.id
            // 判断两个Objects 是否有相同的内容。
            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = true
        }
    }
}