package com.townwang.yaohuo.ui.weight.binding.viewbind

import android.app.Activity
import androidx.viewbinding.ViewBinding
import com.townwang.yaohuo.ui.weight.binding.base.ActivityDelegate
import com.townwang.yaohuo.ui.weight.binding.ext.inflateMethod
import kotlin.reflect.KProperty

class ActivityViewBinding<T : ViewBinding>(
    classes: Class<T>,
    val activity: Activity
) : ActivityDelegate<T>(activity) {

    private var layoutInflater = classes.inflateMethod()

    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        return viewBinding?.run {
            this

        } ?: let {
            // 当继承 Activity 且 Build.VERSION.SDK_INT < Build.VERSION_CODES.Q 时触发
            addLifecycleFragment(activity)

            // 获取 ViewBinding 实例
            val bind = layoutInflater.invoke(null, thisRef.layoutInflater) as T
            thisRef.setContentView(bind.root)
            return bind.apply { viewBinding = this }
        }
    }

}