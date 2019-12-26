package com.townwang.yaohuo.ui.fragment.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.common.OnItemClickListener
import com.townwang.yaohuo.repo.data.HomeData

class ListAdapter : RecyclerView.Adapter<ListViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    var datas = arrayListOf<HomeData>()
        set(value) {
            if (field.isNotEmpty()) {
                if (field.last().title != value.last().title) {
                    field.addAll(value)
                }
            }else{
                field.addAll(value)
            }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(holder.itemView,datas[position])
        }
        return holder.bind(datas[position])
    }

}