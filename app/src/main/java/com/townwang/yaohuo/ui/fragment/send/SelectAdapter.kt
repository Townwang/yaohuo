package com.townwang.yaohuo.ui.fragment.send

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.R
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
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return SelectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }
    override fun getItemViewType(position: Int): Int {
        return R.layout.item_select_list_data
    }
    override fun onBindViewHolder(holder: SelectViewHolder, position: Int) {
        holder.itemView.onClickListener {
            onItemClickListener?.invoke(holder.itemView,datas[position])
        }
        return holder.bind(datas[position])
    }

}