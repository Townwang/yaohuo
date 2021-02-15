package com.townwang.yaohuo.ui.fragment.msg

import android.annotation.SuppressLint
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
import com.townwang.yaohuo.common.onClickListener
import com.townwang.yaohuo.databinding.ItemListMsgDataBinding
import com.townwang.yaohuo.repo.data.MsgBean
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class MsgAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(Product.CALLBACK) {
    var onItemListListener: OnItemListener? = null
    var onDeleteListener: OnItemListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflateView(parent, viewType)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is ProductViewHolder) {
            holder.bindData(data)
            holder.itemView.setOnClickListener {
                onItemListListener?.invoke(it, data)
            }
            holder.binding.delete.onClickListener {
                onDeleteListener?.invoke(it, data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_list_msg_data

    private fun inflateView(viewGroup: ViewGroup, @LayoutRes viewType: Int): View {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return layoutInflater.inflate(viewType, viewGroup, false)
    }
}

class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemListMsgDataBinding by viewbind()

    @SuppressLint("SetTextI18n")
    fun bindData(pro: Product?) {
        val data = pro?.t
        pro ?: return
        if (data is MsgBean) {
            if (data.isSystem) {
                binding.person.setImageResource(R.drawable.anim_vector_msg)
            } else {
                binding.person.setImageResource(R.drawable.anim_vector_about)
            }
            binding.msg.text = data.msg
            binding.time.text = data.time
        }
    }
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