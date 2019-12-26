package com.townwang.yaohuo.ui.fragment.list

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.R
import com.townwang.yaohuo.repo.data.HomeData
import kotlinx.android.synthetic.main.item_list_data.view.*

class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): ListViewHolder {
            return ListViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_list_data, parent, false)
            )
        }
    }
    @SuppressLint("SetTextI18n")
    fun bind(data: HomeData) {
        Log.d("解析", "结果：${data}")
        itemView.title.text = data.title
        itemView.auth.text = "楼主：${data.auth}"
        itemView.reply.text = data.reply
        itemView.read.text = data.read
        itemView.annex.visibility = View.GONE
        itemView.reward.visibility = View.GONE
        itemView.meat.visibility = View.GONE
        itemView.bear.visibility = View.GONE
        itemView.ceremony.visibility = View.GONE
        if (data.smailIng.isNotEmpty()) {
            data.smailIng.forEach {
                when (it) {
                    "附" -> {
                        itemView.annex.visibility = View.VISIBLE
                        itemView.annex.text = it
                    }
                    "赏" -> {
                        itemView.reward.visibility = View.VISIBLE
                        itemView.reward.text = it
                    }
                    "肉" -> {
                        itemView.meat.visibility = View.VISIBLE
                        itemView.meat.text = it
                    }
                    "结" -> {
                        itemView.bear.visibility = View.VISIBLE
                        itemView.bear.text = it
                    }
                    "礼" -> {
                        itemView.ceremony.visibility = View.VISIBLE
                        itemView.ceremony.text = it
                    }
                }
            }
        }
    }
}