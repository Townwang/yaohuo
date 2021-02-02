package com.townwang.yaohuo.ui.weight.binding.viewbind

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.townwang.yaohuo.ui.weight.binding.base.FragmentDelegate
import com.townwang.yaohuo.ui.weight.binding.ext.bindMethod
import com.townwang.yaohuo.ui.weight.binding.ext.inflateMethod
import kotlin.reflect.KProperty

class FragmentViewBinding<T : ViewBinding>(
    classes: Class<T>,
    fragment: Fragment
) : FragmentDelegate<T>(fragment) {

    private val layoutInflater = classes.inflateMethod()
    private val bindView = classes.bindMethod()

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return viewBinding?.run {
            return this

        } ?: let {

            val bind: T
            if (thisRef.view == null) {
                // 这里为了兼容在 navigation 中使用 Fragment
                bind = layoutInflater.invoke(null, thisRef.layoutInflater) as T
            } else {
                bind = bindView.invoke(null, thisRef.view) as T

            }

            return bind.apply { viewBinding = this }
        }
    }
}