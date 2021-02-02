package com.townwang.yaohuo.ui.weight.binding.ext
import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.townwang.yaohuo.ui.weight.binding.viewbind.*

inline fun <reified T : ViewBinding> Activity.viewbind() =
    ActivityViewBinding(T::class.java, this)

inline fun <reified T : ViewBinding> Fragment.viewbind() =
    FragmentViewBinding(T::class.java, this)

inline fun <reified T : ViewBinding> Dialog.viewbind() =
    DialogViewBinding(T::class.java)

inline fun <reified T : ViewBinding> Dialog.viewbind(lifecycle: Lifecycle) =
    DialogViewBinding(T::class.java, lifecycle)

inline fun <reified T : ViewBinding> RecyclerView.ViewHolder.viewbind() =
    ViewHolderViewBinding(T::class.java)

inline fun <reified T : ViewBinding> ViewGroup.viewbind() = ViewGroupViewBinding(
    T::class.java,
    LayoutInflater.from(getContext())
)