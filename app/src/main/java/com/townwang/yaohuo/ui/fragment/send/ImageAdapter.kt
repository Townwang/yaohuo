package com.townwang.yaohuo.ui.fragment.send

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.townwang.yaohuo.common.OnClickListener
import com.townwang.yaohuo.databinding.ItemImageDataBinding
import com.townwang.yaohuo.repo.data.YaoCdnReq

class ImageAdapter(val context: Context) : BaseAdapter() {
    val onClickListener: OnClickListener? = null
    var datas = arrayListOf<YaoCdnReq>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int {
        return datas.count()
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, arg2: ViewGroup): View {
        val binding =
            ItemImageDataBinding.inflate(LayoutInflater.from(context), arg2, false)
        Glide.with(context)
            .load(datas[position].url)
            .into(binding.chooseImage)
        if (position == datas.lastIndex) {
            binding.remove.visibility = View.GONE
        } else {
            binding.remove.visibility = View.VISIBLE
        }
        binding.remove.setOnClickListener {
            onClickListener?.invoke(binding.remove, datas[position])
            datas.removeAt(position)
            notifyDataSetChanged()
        }
        return binding.root
    }

}