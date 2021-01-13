package com.townwang.yaohuo.ui.weight.htmltext

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
internal class HtmlImageGetter : ImageGetter {
    private var textView: TextView? = null
    private var imageLoader: HtmlImageLoader? = null
    private val imageSizeList: MutableList<ImageSize>
    private var index = 0
    fun setTextView(textView: TextView?) {
        this.textView = textView
    }

    fun setImageLoader(imageLoader: HtmlImageLoader?) {
        this.imageLoader = imageLoader
    }

    fun getImageSize(source: String) {
        val imageMatcher =
            IMAGE_TAG_PATTERN.matcher(source)
        while (imageMatcher.find()) {
            val attrs = imageMatcher.group(2).trim { it <= ' ' }
            var width = -1
            var height = -1
            val widthMatcher =
                IMAGE_WIDTH_PATTERN.matcher(attrs)
            if (widthMatcher.find()) {
                width = parseSize(widthMatcher.group(2).trim { it <= ' ' })
            }
            val heightMatcher =
                IMAGE_HEIGHT_PATTERN.matcher(attrs)
            if (heightMatcher.find()) {
                height = parseSize(heightMatcher.group(2).trim { it <= ' ' })
            }
            val imageSize = ImageSize(width, height)
            imageSizeList.add(imageSize)
        }
    }

    override fun getDrawable(source: String): Drawable {
        val imageDrawable = ImageDrawable(index++)
        imageLoader?.run {
            imageDrawable.setDrawable(defaultDrawable, false)
            loadImage(source, object : HtmlImageLoader.Callback {
                override fun onLoadComplete(bitmap: Drawable?) {
                    GlobalScope.launch(Dispatchers.Main) {
                        imageDrawable.setDrawable(bitmap, true)
                        textView?.text = textView?.text
                    }
                }

                override fun onLoadFailed() {
                    GlobalScope.launch(Dispatchers.Main) {
                        imageDrawable.setDrawable(errorDrawable, false)
                        textView?.text = textView?.text
                    }
                }
            })
        }
        return imageDrawable
    }

    private class ImageSize(val width: Int, val height: Int) {
        fun valid(): Boolean {
            return width >= 0 && height >= 0
        }

    }

    private inner class ImageDrawable(// img 标签出现的位置
        private val position: Int
    ) : Drawable() {
        private var mDrawable: Drawable? = null
        fun setDrawable(drawable: Drawable?, fitSize: Boolean) {
            mDrawable = drawable
            if (mDrawable == null) {
                setBounds(0, 0, 0, 0)
                return
            }
            val maxWidth = if (imageLoader == null) 0 else imageLoader!!.maxWidth
            val fitWidth = imageLoader != null && imageLoader!!.fitWidth()
            var width: Int
            var height: Int
            if (fitSize) { // real image
                val imageSize =
                    if (imageSizeList.size > position) imageSizeList[position] else null
                if (imageSize != null && imageSize.valid()) {
                    width = dp2px(imageSize.width.toFloat())
                    height = dp2px(imageSize.height.toFloat())
                } else {
                    width = mDrawable?.intrinsicWidth ?: 0
                    height = mDrawable?.intrinsicHeight ?: 0
                }
            } else { // placeholder image
                width = mDrawable?.intrinsicWidth ?: 0
                height = mDrawable?.intrinsicHeight ?: 0
            }
            if (width > 0 && height > 0) {
                // too large or should fit width
                if (maxWidth > 0 && (width > maxWidth || fitWidth)) {
                    height = (height.toFloat() / width * maxWidth).toInt()
                    width = maxWidth
                }
            }
            mDrawable?.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)
        }

        override fun draw(canvas: Canvas) {
            if (mDrawable != null) {
                if (mDrawable is Drawable) {
                    if (mDrawable == null) {
                        return
                    }
                }
                mDrawable?.draw(canvas)
            }
        }

        override fun setAlpha(alpha: Int) {
            mDrawable?.alpha = alpha
        }

        override fun getOpacity(): Int {
            return mDrawable?.opacity?:0
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            mDrawable?.colorFilter = colorFilter
        }

        private fun dp2px(dpValue: Float): Int {
            val scale = textView?.resources?.displayMetrics?.density
            return (dpValue * scale!! + 0.5f).toInt()
        }

    }

    companion object {
        private const val IMAGE_TAG_REGULAR = "<(img|IMG)\\s+([^>]*)>"
        private val IMAGE_TAG_PATTERN =
            Pattern.compile(IMAGE_TAG_REGULAR)
        private val IMAGE_WIDTH_PATTERN =
            Pattern.compile("(width|WIDTH)\\s*=\\s*\"?(\\w+)\"?")
        private val IMAGE_HEIGHT_PATTERN =
            Pattern.compile("(height|HEIGHT)\\s*=\\s*\"?(\\w+)\"?")

        private fun parseSize(size: String): Int {
            return try {
                Integer.valueOf(size)
            } catch (e: NumberFormatException) {
                -1
            }
        }
    }

    init {
        imageSizeList = ArrayList()
    }
}