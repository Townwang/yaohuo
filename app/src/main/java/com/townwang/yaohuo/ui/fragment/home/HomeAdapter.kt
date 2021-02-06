package com.townwang.yaohuo.ui.fragment.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.OnItemClickListener
import com.townwang.yaohuo.common.T
import com.townwang.yaohuo.databinding.ItemHomeBbsHanderBinding
import com.townwang.yaohuo.databinding.ItemHomeMoreFooterBinding
import com.townwang.yaohuo.databinding.ItemHomeSearchHanderBinding
import com.townwang.yaohuo.databinding.ItemListDataBinding
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.ui.weight.binding.ext.databind
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class HomeAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(Product.CALLBACK) {
    var onItemListListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflateView(parent, viewType)
        return when (viewType) {
            R.layout.item_home_search_hander -> ProductViewHolderHeader(view)
            R.layout.item_home_bbs_hander -> ProductBBSViewHolderHeader(view)
            R.layout.item_home_more_footer -> ProductViewHolderFooter(view)
            else -> ProductViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is ProductViewHolder) {
            holder.bindData(data)
        }

        when (holder) {
            is ProductViewHolderHeader -> holder.bindData(null)
            is ProductBBSViewHolderHeader -> holder.bindData(null)
            is ProductViewHolderFooter -> holder.bindData(null, position)
            is ProductViewHolder -> {
                val data = getItem(position)
                holder.bindData(data)
            }
        }

    }
    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_home_search_hander
        itemCount - 1 -> R.layout.item_home_bbs_hander
        itemCount - 2 -> R.layout.item_home_more_footer
        else -> R.layout.item_list_data
    }

    private fun inflateView(viewGroup: ViewGroup, @LayoutRes viewType: Int): View {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return layoutInflater.inflate(viewType, viewGroup, false)
    }
}

class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemListDataBinding by viewbind()

    fun bindData(pro: Product?) {
        val data = pro?.t
        pro ?: return
        if (data is HomeData) {
            binding.apply {
                binding.title.text = data.title
                binding.auth.text = "楼主：${data.auth}"
                binding.reply.text = data.reply
                binding.read.text = data.read
                binding.annex.visibility = View.GONE
                binding.reward.visibility = View.GONE
                binding.meat.visibility = View.GONE
                binding.bear.visibility = View.GONE
                binding.ceremony.visibility = View.GONE
                binding.fine.visibility = View.GONE
                binding.headline.visibility = View.GONE
                if (data.smailIng.isNotEmpty()) {
                    data.smailIng.forEach {
                        when (it) {
                            BuildConfig.YH_MATCH_LIST_FILE -> {
                                binding.annex.visibility = View.VISIBLE
                                binding.annex.text = it
                            }
                            BuildConfig.YH_MATCH_LIST_GIVE -> {
                                binding.reward.visibility = View.VISIBLE
                                binding.reward.text = it
                            }
                            BuildConfig.YH_MATCH_LIST_MEAT -> {
                                binding.meat.visibility = View.VISIBLE
                                binding.meat.text = it
                            }
                            BuildConfig.YH_MATCH_LIST_BEAR -> {
                                binding.bear.visibility = View.VISIBLE
                                binding.bear.text = it
                            }
                            BuildConfig.YH_MATCH_LIST_LI -> {
                                binding.ceremony.visibility = View.VISIBLE
                                binding.ceremony.text = it
                            }
                            BuildConfig.YH_MATCH_LIST_FIME -> {
                                binding.fine.visibility = View.VISIBLE
                                binding.fine.text = it
                            }
                            BuildConfig.YH_MATCH_LIST_HOT -> {
                                binding.headline.visibility = View.VISIBLE
                                binding.headline.text = "头条"
                            }
                        }
                    }
                }
            }
        }
    }
}


class ProductViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemHomeSearchHanderBinding by viewbind()

    fun bindData(pro: Product?) {
        val data = pro?.t
        data ?: return

    }
}

class ProductBBSViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemHomeBbsHanderBinding by viewbind()

    fun bindData(pro: Product?) {
        val data = pro?.t
        data ?: return
    }
}


class ProductViewHolderFooter(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemHomeMoreFooterBinding by viewbind()

    fun bindData(pro: Product?, position: Int) {
        val data = pro?.t
        data ?: return
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