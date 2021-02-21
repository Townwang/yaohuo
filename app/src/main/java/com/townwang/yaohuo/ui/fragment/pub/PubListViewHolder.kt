package com.townwang.yaohuo.ui.fragment.pub

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.databinding.ItemListDataBinding
import com.townwang.yaohuo.repo.data.HomeBean
import com.townwang.yaohuo.ui.weight.binding.ext.viewbind

class PubListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding: ItemListDataBinding by viewbind()

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    fun bind( data: HomeBean) {
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