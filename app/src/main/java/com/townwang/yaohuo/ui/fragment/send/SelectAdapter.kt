package com.townwang.yaohuo.ui.fragment.send

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.common.OnItemClickListener
import com.townwang.yaohuo.common.onClickListener
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.repo.data.SelectBean

class SelectAdapter : RecyclerView.Adapter<SelectViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    var datas = arrayListOf<SelectBean>()
        set(value) {
            if (field.isNotEmpty()) {
                if (field.last().type != value.last().type) {
                    field.addAll(value)
                }
            }else{
                field.addAll(value)
            }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectViewHolder {
        return SelectViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: SelectViewHolder, position: Int) {
        holder.itemView.onClickListener {
            onItemClickListener?.invoke(holder.itemView,datas[position])
        }
        return holder.bind(datas[position])
    }

}