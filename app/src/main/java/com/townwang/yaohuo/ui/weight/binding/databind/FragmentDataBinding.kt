package com.townwang.yaohuo.ui.weight.binding.databind

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.townwang.yaohuo.ui.weight.binding.base.FragmentDelegate
import com.townwang.yaohuo.ui.weight.binding.ext.inflateMethod
import kotlin.reflect.KProperty


class FragmentDataBinding<T : ViewDataBinding>(
    classes: Class<T>,
    val fragment: Fragment,
    private var block: (T.() -> Unit)? = null
) : FragmentDelegate<T>(fragment) {

    private val layoutInflater = classes.inflateMethod()

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return viewBinding?.run {
            return this

        } ?: let {

            val bind: T
            if (thisRef.view == null) {
                // 这里为了兼容在 navigation 中使用 Fragment
                bind = layoutInflater.invoke(null, thisRef.layoutInflater) as T
            } else {
                bind = DataBindingUtil.bind(thisRef.requireView())!!
            }

            return bind.apply {
                viewBinding = this
                lifecycleOwner = fragment.viewLifecycleOwner
                block?.invoke(this)
                block = null
            }
        }
    }
}