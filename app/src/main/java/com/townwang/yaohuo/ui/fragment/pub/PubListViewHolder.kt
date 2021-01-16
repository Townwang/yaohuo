package com.townwang.yaohuo.ui.fragment.pub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.repo.data.HomeData
import kotlinx.android.synthetic.main.item_list_data.view.*

class PubListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): PubListViewHolder {
            return PubListViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_list_data, parent, false)
            )
        }
    }
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    fun bind(data: HomeData) {
        itemView.title.text = data.title
        itemView.auth.text = "楼主：${data.auth}"
        itemView.reply.text = data.reply
        itemView.read.text = data.read
        itemView.annex.visibility = View.GONE
        itemView.reward.visibility = View.GONE
        itemView.meat.visibility = View.GONE
        itemView.bear.visibility = View.GONE
        itemView.ceremony.visibility = View.GONE
        itemView.fine.visibility = View.GONE
        itemView.headline.visibility = View.GONE
        if (data.smailIng.isNotEmpty()) {
            data.smailIng.forEach {
                when (it) {
                    BuildConfig.YH_MATCH_LIST_FILE -> {
                        itemView.annex.visibility = View.VISIBLE
                        itemView.annex.text = it
                    }
                    BuildConfig.YH_MATCH_LIST_GIVE -> {
                        itemView.reward.visibility = View.VISIBLE
                        itemView.reward.text = it
                    }
                    BuildConfig.YH_MATCH_LIST_MEAT -> {
                        itemView.meat.visibility = View.VISIBLE
                        itemView.meat.text = it
                    }
                    BuildConfig.YH_MATCH_LIST_BEAR -> {
                        itemView.bear.visibility = View.VISIBLE
                        itemView.bear.text = it
                    }
                    BuildConfig.YH_MATCH_LIST_LI -> {
                        itemView.ceremony.visibility = View.VISIBLE
                        itemView.ceremony.text = it
                    }
                    BuildConfig.YH_MATCH_LIST_FIME -> {
                        itemView.fine.visibility = View.VISIBLE
                        itemView.fine.text = it
                    }
                    BuildConfig.YH_MATCH_LIST_HOT ->{
                        itemView.headline.visibility = View.VISIBLE
                        itemView.headline.text = "头条"
                    }
                }
            }
        }
    }
}