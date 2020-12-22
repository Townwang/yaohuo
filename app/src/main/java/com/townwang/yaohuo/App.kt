package com.townwang.yaohuo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.multidex.MultiDex
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.townwang.yaohuo.di.koinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.Bugly
import com.townwang.yaohuo.common.isCrack

@Suppress("DEPRECATION")
class App : Application() {
    companion object {
        var application: Application? = null
        fun getContext(): Context {
            return application!!
        }
    }
    @SuppressLint("PackageManagerGetSignatures")
    override fun onCreate() {
        super.onCreate()
        application = this
        startKoin {
            androidContext(this@App)
            androidLogger(Level.INFO)
            modules(koinModules)
        }
        Bugly.init(applicationContext, "56bf507146", false);
        try {
            val a = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            val b = a.signatures
            val c = b[0]
            val d = c.hashCode()
            Log.d("解析", "当前hashCode:$d")
            if (d == -508714960 || d == 1375692864) {
                isCrack = true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this@App)
    }

    init {
        SmartRefreshLayout.setDefaultRefreshInitializer { _, layout ->
            layout.setEnableAutoLoadMore(true)
            layout.setEnableOverScrollDrag(false)
            layout.setEnableOverScrollBounce(true)
            layout.setEnableLoadMoreWhenContentNotFull(true)
            layout.setEnableScrollContentWhenRefreshed(true)
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setEnableRefresh(true)
            layout.autoRefresh()
            layout.setEnableHeaderTranslationContent(true)
            ClassicsHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }

    }

}