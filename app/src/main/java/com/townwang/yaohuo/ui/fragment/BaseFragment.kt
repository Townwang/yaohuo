package com.townwang.yaohuo.ui.fragment

import androidx.fragment.app.Fragment
import com.townwang.yaohuo.common.T
import com.xiasuhuei321.loadingdialog.view.LoadingDialog

open class BaseFragment : Fragment() {
    var _binding: T? = null
    var loading: LoadingDialog? = null
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (loading != null) {
            loading?.close()
        }
        loading = null
    }
}