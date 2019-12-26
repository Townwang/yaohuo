package com.townwang.yaohuo.ui.fragment.details

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.common.OnItemClickListener
import com.townwang.yaohuo.repo.data.CommentData

class CommentAdapter : RecyclerView.Adapter<CommentViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    var datas = arrayListOf<CommentData>()
        set(value) {
           if (field.isNotEmpty()) {
               if (field.last().content != value.last().content) {
                   field.addAll(value)
               }
           }else{
               field.addAll(value)
           }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(holder.itemView,datas[position])
        }
        return holder.bind(datas[position])
    }

}