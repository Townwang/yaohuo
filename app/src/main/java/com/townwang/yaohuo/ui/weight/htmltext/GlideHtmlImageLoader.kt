package com.townwang.yaohuo.ui.weight.htmltext

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GlideHtmlImageLoader(
    private val context: Context,
    resources: Resources,
    private val v: View
) : HtmlImageLoader {
    override fun loadImage(url: String?, callback: HtmlImageLoader.Callback?) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val requestBuilder =
                    Glide.with(context)
                        .asDrawable()
                        .load(url)
                        .apply(options)
                        .submit()
                        .get()
                callback?.onLoadComplete(requestBuilder)
            } catch (e: Exception) {
                callback?.onLoadFailed()
            }
        }
    }

    private val dm: DisplayMetrics = resources.displayMetrics

    override val defaultDrawable: Drawable?
        get() = ContextCompat.getDrawable(context,R.drawable.ic_picture)
    override val errorDrawable:Drawable?
        get() = ContextCompat.getDrawable(context,R.drawable.ic_picture_error)
    override val maxWidth: Int
        get() = dm.widthPixels - v.paddingLeft - v.paddingRight

    override fun fitWidth(): Boolean {
        return false
    }

}