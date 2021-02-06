package com.townwang.yaohuo.ui.weight.binding.databind

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.townwang.yaohuo.ui.weight.binding.base.ActivityDelegate
import kotlin.reflect.KProperty


class ActivityDataBinding<T : ViewDataBinding>(
    val activity: Activity,
    @LayoutRes val resId: Int,
    private var block: (T.() -> Unit)? = null
) : ActivityDelegate<T>(activity) {

    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        return viewBinding?.run {
            this

        } ?: let {
            // 当继承 Activity 且 Build.VERSION.SDK_INT < Build.VERSION_CODES.Q 时触发
            addLifecycleFragment(activity)

            // 获取 ViewDataBinding
            val bind: T = DataBindingUtil.setContentView(thisRef, resId)
            return bind.apply {
                if (activity is ComponentActivity) {
                    bind.lifecycleOwner = activity
                }
                viewBinding = this
                block?.invoke(this)
                block = null
            }
        }
    }

}