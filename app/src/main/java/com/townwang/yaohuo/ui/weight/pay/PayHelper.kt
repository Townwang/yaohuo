package com.townwang.yaohuo.ui.weight.pay

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.service.quicksettings.TileService
import android.view.View
import android.widget.Toast
import com.townwang.yaohuo.R
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException

object PayHelper {
    internal const val EXTRA_KEY_PAY_CONFIG = "pay_config"
    internal val aliZhi = AliZhi()
    internal val weZhi = WeZhi()

    fun setupPay(cxt: Context, config: PayConfig) {
        val i = Intent(cxt, ZhiActivity::class.java)
        i.putExtra(EXTRA_KEY_PAY_CONFIG, config)
        cxt.startActivity(i)
    }

    class AliZhi {
        // 支付宝包名
        private val ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone"

        // 旧版支付宝二维码通用 Intent Scheme Url 格式
        private val INTENT_URL_FORMAT =
            "intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end"

        /**
         * 打开转账窗口
         * 旧版支付宝二维码方法，需要使用 https://fama.alipay.com/qrcode/index.htm 网站生成的二维码
         * 这个方法最好，但在 2016 年 8 月发现新用户可能无法使用
         *
         * @param urlCode 手动解析二维码获得地址中的参数，例如 https://qr.alipay.com/aehvyvf4taua18zo6e 最后那段
         * @return 是否成功调用
         */
        fun startAliPayClient(activity: Activity, urlCode: String): Boolean {
            return startIntentUrl(
                activity,
                INTENT_URL_FORMAT.replace(
                    "{urlCode}",
                    urlCode
                )
            )
        }

        /**
         * 打开 Intent Scheme Url
         *
         * @param intentFullUrl Intent 跳转地址
         * @return 是否成功调用
         */
        private fun startIntentUrl(activity: Activity, intentFullUrl: String): Boolean {
            return try {
                val intent = Intent.parseUri(
                    intentFullUrl,
                    Intent.URI_INTENT_SCHEME
                )
                activity.startActivity(intent)
                true
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                false
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(activity, "未安装支付宝", Toast.LENGTH_SHORT).show()
                false
            }
        }

        /**
         * 判断支付宝客户端是否已安装，建议调用转账前检查
         *
         * @param context Context
         * @return 支付宝客户端是否已安装
         */
        fun hasInstalledAliPayClient(context: Context): Boolean {
            val pm = context.packageManager
            return try {
                val info = pm.getPackageInfo(
                    ALIPAY_PACKAGE_NAME,
                    0
                )
                info != null
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 获取支付宝客户端版本名称，作用不大
         *
         * @param context Context
         * @return 版本名称
         */
        fun getAliPayClientVersion(context: Context): String? {
            val pm = context.packageManager
            return try {
                val info = pm.getPackageInfo(
                    ALIPAY_PACKAGE_NAME,
                    0
                )
                info.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 打开支付宝扫一扫界面
         *
         * @param context Context
         * @return 是否成功打开 Activity
         */
        @SuppressLint("NewApi")
        fun openAliPayScan(context: Context): Boolean {
            return try {
                val uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (context is TileService) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        context.startActivityAndCollapse(intent)
                    }
                } else {
                    context.startActivity(intent)
                }
                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * 打开支付宝付款码
         *
         * @param context Context
         * @return 是否成功打开 Activity
         */
        @SuppressLint("NewApi")
        fun openAliPayBarcode(context: Context): Boolean {
            return try {
                val uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (context is TileService) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        context.startActivityAndCollapse(intent)
                    }
                } else {
                    context.startActivity(intent)
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    class WeZhi {
        private val errorHandler = CoroutineExceptionHandler { _, _ ->

        }

        fun startWeZhi(c: Context, view: View) {
            val dir = c.getExternalFilesDir("pay_img")
            if (dir != null && !dir.exists() && !dir.mkdirs()) {
                return
            } else {
                val f = dir?.listFiles()
                f?.forEach {
                    it.delete()
                }
            }
            val fileName = System.currentTimeMillis().toString() + "weixin_qa.png"
            val file = File(dir, fileName)
            GlobalScope.launch(errorHandler) {
                withContext(Dispatchers.IO) {
                    // 执行你的耗时操作代码
                    snapShot(c, file, view)
                    startWechat(c)
                }
            }
        }

        @SuppressLint("InlinedApi")
        private fun snapShot(context: Context, file: File, view: View) {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas()
            canvas.setBitmap(bitmap)
            view.draw(canvas)
            var fos: FileOutputStream? = null
            var isSuccess = false
            try {
                fos = FileOutputStream(file)
                //通过io流的方式来压缩保存图片
                isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)
                fos.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (isSuccess) {
                val contentResolver = context.contentResolver
                val values = ContentValues(4)
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                values.put(MediaStore.Images.Media.ORIENTATION, 0)
                values.put(
                    MediaStore.Images.Media.TITLE,
                    context.getString(R.string.donate)
                )
                values.put(
                    MediaStore.Images.Media.DESCRIPTION,
                    context.getString(R.string.donate_qa)
                )
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                values.put(
                    MediaStore.Images.Media.DATE_MODIFIED,
                    System.currentTimeMillis() / 1000
                )
                var url: Uri? = null
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        context.grantUriPermission(
                            context.packageName,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                    url = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    ) //其实质是返回 Image.Meida.DATA中图片路径path的转变而成的uri
                    url?.also {
                        val imageOut = contentResolver.openOutputStream(it)
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOut)
                        } finally {
                            try {
                                imageOut?.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        val id = ContentUris.parseId(it)
                        MediaStore.Images.Thumbnails.getThumbnail(
                            contentResolver,
                            id,
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null
                        ) //获取缩略图
                    }
                } catch (e: Exception) {
                    if (url != null) {
                        contentResolver.delete(url, null, null)
                    }
                }
            }
        }

        @SuppressLint("WrongConstant", "QueryPermissionsNeeded")
        private fun startWechat(c: Context) {
            val intent = Intent()
            intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
            intent.flags = 335544320
            intent.action = "android.intent.action.VIEW"
            val pm = c.packageManager
            if (!(pm == null || pm.queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                ).size <= 0)
            ) {
                c.startActivity(intent)
            } else {
                Toast.makeText(c, "未安装微信～", Toast.LENGTH_SHORT).show()
            }
        }
        @SuppressLint("WrongConstant", "QueryPermissionsNeeded")
        fun startWechatSearch(c: Context) {
            val intent = Intent()
            intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.plugin.fts.ui.FTSMainUI")
            intent.flags = 335544320
            intent.action = "android.intent.action.VIEW"
            val pm = c.packageManager
            if (!(pm == null || pm.queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                ).size <= 0)
            ) {
                c.startActivity(intent)
            } else {
                Toast.makeText(c, "未安装微信～", Toast.LENGTH_SHORT).show()
            }
        }
    }
}