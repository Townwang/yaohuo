package com.townwang.yaohuo.ui.weight.binding.viewbind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.townwang.yaohuo.ui.weight.binding.ext.inflateMethod
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewGroupViewBinding<T : ViewBinding>(
    classes: Class<T>,
    val inflater: LayoutInflater
) : ReadOnlyProperty<ViewGroup, T> {

    private var viewBinding: T? = null
    private val layoutInflater = classes.inflateMethod()

    override fun getValue(thisRef: ViewGroup, property: KProperty<*>): T {
        return viewBinding?.run {
            this

        } ?: let {
            val bind = layoutInflater.invoke(null, inflater) as T
            bind.apply {
                thisRef.addView(bind.root)
                viewBinding = this
            }
        }
    }

    private fun destroyed() {
        viewBinding = null
    }
}