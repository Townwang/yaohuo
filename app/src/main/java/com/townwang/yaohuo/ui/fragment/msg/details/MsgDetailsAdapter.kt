package com.townwang.yaohuo.ui.fragment.msg.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ItemMsgLeftDetailsDataBinding
import com.townwang.yaohuo.databinding.ItemMsgRightDetailsDataBinding
import com.townwang.yaohuo.repo.data.details.MsgDetailsBean
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class MsgDetailsAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(Product.CALLBACK) {
    var onItemListListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflateView(parent, viewType)
        return when (viewType) {
            R.layout.item_msg_left_details_data -> ProductViewHolderLeft(view)
            else -> ProductViewHolderRight(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is ProductViewHolderLeft) {
            holder.bindData(data)
            holder.itemView.setOnClickListener {
                onItemListListener?.invoke(it, data)
            }
        }

        if (holder is ProductViewHolderRight) {
            holder.bindData(data)
            holder.itemView.setOnClickListener {
                onItemListListener?.invoke(it, data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pro =  getItem(position)
       val data = pro.t
        if (data is MsgDetailsBean) {
            if (data.isUser){
                return R.layout.item_msg_left_details_data
            }
        }
        return R.layout.item_msg_right_details_data
    }

    private fun inflateView(viewGroup: ViewGroup, @LayoutRes viewType: Int): View {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return layoutInflater.inflate(viewType, viewGroup, false)
    }
}

class ProductViewHolderRight(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemMsgRightDetailsDataBinding by viewbind()
    fun bindData(pro: Product?) {
        val data = pro?.t
        pro ?: return
        if (data is MsgDetailsBean) {
            binding.leval.text = data.level
            binding.msg.text = data.msg.toHtml()
            Glide.with(itemView)
                .load(getUrlString(data.userImg))
                .apply(options)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.userImg)
        }
    }
}


class ProductViewHolderLeft(view: View) : RecyclerView.ViewHolder(view) {
    val binding: ItemMsgLeftDetailsDataBinding by viewbind()
    fun bindData(pro: Product?) {
        val data = pro?.t
        pro ?: return
        if (data is MsgDetailsBean) {
            binding.leval.text = data.level
            binding.msg.text =  data.msg.toHtml()
            Glide.with(itemView)
                .load(getUrlString(data.userImg))
                .apply(options)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.userImg)
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