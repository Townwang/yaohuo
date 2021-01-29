package com.townwang.yaohuo.ui.fragment.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.R
import com.townwang.yaohuo.repo.data.SelectBean
import kotlinx.android.synthetic.main.item_list_data.view.*

class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): SelectViewHolder {
            return SelectViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_select_list_data, parent, false)
            )
        }
    }

    fun bind(data: SelectBean) {
        itemView.title.text = data.string
    }
}