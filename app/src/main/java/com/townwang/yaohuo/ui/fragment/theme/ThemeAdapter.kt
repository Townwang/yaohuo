package com.townwang.yaohuo.ui.fragment.theme

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.common.OnItemClickListener
import com.townwang.yaohuo.common.onClickListener
import com.townwang.yaohuo.repo.data.ThemeList

class ThemeAdapter : RecyclerView.Adapter<ThemeViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    var datas = emptyList<ThemeList>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.itemView.onClickListener {
            onItemClickListener?.invoke(holder.itemView,datas[position])
        }
        return holder.bind(datas[position])
    }

}