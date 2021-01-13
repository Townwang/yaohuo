package com.townwang.yaohuo.ui.fragment.pub

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.common.OnItemClickListener
import com.townwang.yaohuo.common.onClickListener
import com.townwang.yaohuo.repo.data.HomeData

class PubListAdapter : RecyclerView.Adapter<PubListViewHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubListViewHolder {
        return PubListViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: PubListViewHolder, position: Int) {
        holder.itemView.onClickListener {
            onItemClickListener?.invoke(holder.itemView,datas[position])
        }
        return holder.bind(datas[position])
    }

}