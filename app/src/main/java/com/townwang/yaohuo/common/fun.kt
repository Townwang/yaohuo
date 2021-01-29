@file:Suppress("UNCHECKED_CAST")

package com.townwang.yaohuo.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.YaoApplication
import com.townwang.yaohuo.common.utils.LoginHelper
import com.townwang.yaohuo.repo.enum.ErrorCode
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import com.xiasuhuei321.loadingdialog.view.LoadingDialog.Speed
import java.net.SocketTimeoutException
import java.net.UnknownHostException


typealias OnItemClickListener = (view: View, data: T) -> Unit
typealias OnItemListener = (view: View, data: T) -> Unit

var gson = Gson()

val handler = Handler()

inline fun <T> T?.work(block: T.() -> Unit) {
    if (this != null) block.invoke(this)
}

private var lastClickTime: Long = 0
fun <T : View> T.onClickListener(delay: Long = 500, block: (T) -> Unit) {
    setOnClickListener {
        val currentTime: Long = System.currentTimeMillis()
        if (currentTime - lastClickTime > delay) {
            lastClickTime = currentTime
            block(this)
        }
    }
}


fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this

fun <T> Fragment.safeObserver(block: (value: T) -> Unit) = Observer<T> {
    if (!isAdded) return@Observer
    it ?: return@Observer
    block(it)
}

fun <T> safeObserver(block: (value: T) -> Unit) = Observer<T> {
    it ?: return@Observer
    block(it)
}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

/**
 * animator
 */
fun startAnimator(drawable: Drawable) {
    if (drawable is Animatable) {
        drawable.start()
    }
}

fun Context.config(key: String, value: String? = null): String {
    var config: String by Preference(this, key, default = "1")
    return if (value.isNullOrEmpty()) {
        config
    } else {
        config = value
        config
    }
}

fun Context.clearConfig(vararg key: String) {
    key.forEach {
        val sp = getSharedPreferences(
            it,
            Context.MODE_PRIVATE
        )
        sp.all.clear()
    }
}

fun Activity.setActTheme(string: String? = null) {
    setTheme(config(THEME_KEY, string).toInt())
}

fun Activity.reload(string: String?) {
    setTheme(config(THEME_KEY, string).toInt())
    finish()
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
}


fun Activity.setSharedElement() {
    setExitSharedElementCallback(object : SharedElementCallback() {
        override fun onCaptureSharedElementSnapshot(
            sharedElement: View?,
            viewToGlobalMatrix: Matrix?,
            screenBounds: RectF?
        ): Parcelable {
            sharedElement?.alpha = 1f
            return super.onCaptureSharedElementSnapshot(
                sharedElement,
                viewToGlobalMatrix,
                screenBounds
            )
        }
    })
}

fun isCookieBoolean(): Boolean {
    val cookieMaps =
        YaoApplication.getContext().getSharedPreferences(COOKIE_KEY, Context.MODE_PRIVATE).all
    return cookieMaps.isNullOrEmpty()
}

fun setTitleCenter(toolbar: Toolbar) {
    val title = "title"
    val originalTitle = toolbar.title
    toolbar.title = title
    for (i in 0 until toolbar.childCount) {
        val view = toolbar.getChildAt(i)
        if (view is TextView) {
            if (title == view.text) {
                view.gravity = Gravity.CENTER
                val params = Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.MATCH_PARENT
                )
                params.gravity = Gravity.CENTER
                view.layoutParams = params
            }
        }
        toolbar.title = originalTitle
    }
}

fun Fragment.Loading(text: String? = null): LoadingDialog {
    return LoadingDialog(context).apply {
        setLoadingText(text ?: getString(R.string.loading))
        interceptBack = false
        setLoadSpeed(Speed.SPEED_TWO)
    }
}

fun Activity.Loading(text: String? = null): LoadingDialog {
    return LoadingDialog(this).apply {
        setLoadingText(text ?: getString(R.string.loading))
        interceptBack = false
        setLoadSpeed(Speed.SPEED_TWO)
    }
}

fun View.widthWithoutPadding() = width - _paddingStart() - _paddingEnd()
fun View.heightWithoutPadding() = height - paddingTop - paddingBottom

@SuppressLint("ObsoleteSdkInt")
fun View._paddingStart() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    paddingStart
} else {
    paddingLeft
}

@SuppressLint("ObsoleteSdkInt")
fun View._paddingEnd() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    paddingEnd
} else {
    paddingRight
}

fun Context.px2mm(px: Float): Float {
    return px / resources.displayMetrics.xdpi * 25.4f
}

fun Context.dp2px(dipValue: Int): Int {
    val scale = resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun Context.dp(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
}

fun Context.mm(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, resources.displayMetrics)
}


fun Context.sp(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics)
}

fun Context.handleException(
    t: Throwable,
    onNetworkError: (() -> Unit)? = null,
    onApiError: ((code: Int, error: String) -> Unit)? = null,
    onUnknownError: ((e: Throwable) -> Unit)? = null
) {
    t.printStackTrace()
    when (t) {
        is ResponseNullPointerException -> {
            toast(getString(R.string.error_qesponse_null_pointer_exception))
        }
        is ApiErrorException -> {
            when (t.code) {
                ErrorCode.E_1001.hashCode(),
                ErrorCode.E_1009.hashCode(),
                ErrorCode.E_1004.hashCode() -> {
                    //2021/1/17/017 找不到帖子 和 审核 无需处理 提示即可
                    toast(t.message.orEmpty())
                }
                ErrorCode.E_1002.hashCode() -> {
                    // TODO: 2021/1/17/017 访问验证
                    toast(t.message.orEmpty())
                }
                ErrorCode.E_1003.hashCode(),
                ErrorCode.E_1006.hashCode(),
                ErrorCode.E_1007.hashCode() -> {
                    clearConfig(THEME_KEY, TROUSER_KEY, COOKIE_KEY, HOME_LIST_THEME_SHOW)
                    LoginHelper.instance.restartLogin(this)
                }
                ErrorCode.E_1005.hashCode() -> {
                    // TODO: 2021/1/17/017 校验密码
                    toast(t.message.orEmpty())
                }
            }
            onApiError?.invoke(t.code, t.message.orEmpty())
        }
        is UnknownHostException,
        is NetworkFailureException,
        is SocketTimeoutException -> {
            toast(getString(R.string.error_socket_time_out_exception))
            onNetworkError?.invoke()
        }
        is UseVPNException -> {
            toast(getString(R.string.error_use_vpn_exception))
        }
        else -> {
            toast(getString(R.string.error_unknown_exception))
            onUnknownError?.invoke(t)
        }
    }
    BuglyLog.e(BuildConfig.FLAVOR, t.message)
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun getParam(url: String, name: String): String {
    var result = ""
    val index = url.indexOf("?")
    val temp = url.substring(index + 1)
    val keyValue = temp.split("&")
    keyValue.forEach {
        if (it.contains(name)) {
            result = it.split("=").last()
            return@forEach
        }
    }
    return result
}

fun getUrlString(url: String): String {
    return if (url.contains("https://", true) || url.contains("http://", true)) {
        url
    } else {
        BuildConfig.BASE_YAOHUO_URL + url
    }
}

val options = RequestOptions()
    .error(R.drawable.ic_picture_error)
    .placeholder(R.drawable.loading_anim)