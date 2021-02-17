package com.townwang.yaohuo.ui.fragment.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.*
import com.townwang.yaohuo.databinding.ItemHomeBbsHanderBinding
import com.townwang.yaohuo.databinding.ItemHomeSearchHanderBinding
import com.townwang.yaohuo.databinding.ItemListDataBinding
import com.townwang.yaohuo.repo.data.HomeBean
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class HomeAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(Product.CALLBACK) {
    var onItemListListener: OnItemClickListener? = null
    var onSearchListener: OnItemListener? = null
    var onBBSListener: OnBBSClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflateView(parent, viewType)
        return when (viewType) {
            R.layout.item_home_search_hander -> ProductViewHolderHeader(view)
            R.layout.item_home_bbs_hander -> ProductBBSViewHolderHeader(view)
            else -> ProductViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is ProductViewHolderHeader) {
            holder.binding.searchBtn.setOnClickListener {
                onSearchListener?.invoke(it, holder.binding.searchValue.text.toString())
            }
            holder.binding.searchValue.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchListener?.invoke(v, holder.binding.searchValue.text.toString())
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }


        if (holder is ProductBBSViewHolderHeader) {
            onBBSListener
            holder.binding.resourceSharing.setOnClickListener {
                onBBSListener?.invoke(
                    201,
                    R.string.bbs_res_share,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.integratedTechnology.setOnClickListener {
                onBBSListener?.invoke(
                    197,
                    R.string.bbs_integrated_technology,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.mlTalkOver.setOnClickListener {
                onBBSListener?.invoke(
                    203,
                    R.string.bbs_ml_talk_over,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.reward.setOnClickListener {
                onBBSListener?.invoke(204, R.string.bbs_reward, BuildConfig.YH_BBS_ACTION_CLASS)
            }
            holder.binding.teahouse.setOnClickListener {
                onBBSListener?.invoke(
                    177,
                    R.string.bbs_tea_house,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.rewardQuestionAndAnswer.setOnClickListener {
                onBBSListener?.invoke(
                    213,
                    R.string.bbs_quest_answer,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.texturedPhoto.setOnClickListener {
                onBBSListener?.invoke(
                    240,
                    R.string.bbs_textured_photo,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.stationService.setOnClickListener {
                onBBSListener?.invoke(
                    199,
                    R.string.bbs_stationService,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.complaint.setOnClickListener {
                onBBSListener?.invoke(
                    198,
                    R.string.bbs_complaint,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
            holder.binding.announcement.setOnClickListener {
                onBBSListener?.invoke(
                    288,
                    R.string.bbs_announcement,
                    BuildConfig.YH_BBS_ACTION_CLASS
                )
            }
        }
        if (holder is ProductViewHolder) {
            holder.bindData(data)
            holder.itemView.setOnClickListener {
                onItemListListener?.invoke(it, data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_home_search_hander
        1 -> R.layout.item_home_bbs_hander
        else -> R.layout.item_list_data
    }

    private fun inflateView(viewGroup: ViewGroup, @LayoutRes viewType: Int): View {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return layoutInflater.inflate(viewType, viewGroup, false)
    }
}

class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemListDataBinding by viewbind()

    @SuppressLint("SetTextI18n")
    fun bindData(pro: Product?) {
        val data = pro?.t
        pro ?: return
        if (data is HomeBean) {
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
}

class ProductBBSViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemHomeBbsHanderBinding by viewbind()
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