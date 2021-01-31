package com.townwang.yaohuo.ui.fragment.details
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.townwang.yaohuo.common.OnItemListener
import com.townwang.yaohuo.repo.data.details.CommitListBean

class CommentAdapter : RecyclerView.Adapter<CommentViewHolder>() {
    var onItemListener: OnItemListener? = null
    var datas = arrayListOf<CommitListBean>()
        set(value) {
            if (field.isNotEmpty()) {
                if (field.last().content != value.last().content) {
                    field.addAll(value)
                }
            } else {
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
        onItemListener?.invoke(holder.binding, datas[position])
    }

}