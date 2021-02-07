package com.townwang.yaohuo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.multidex.MultiDex
import com.bumptech.glide.request.target.ViewTarget
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.BuglyLog
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.townwang.yaohuo.common.isCrack
import com.townwang.yaohuo.di.koinModules
import com.townwang.yaohuo.ui.weight.header.TaurusHeader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.*

class YaoApplication : Application() {
    companion object {
        var application: Application? = null


        /**
         * 维护Activity 的list
         */
        private val mActivates = Collections
            .synchronizedList(LinkedList<Activity>())

        fun getContext(): Context {
            return application!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        startKoin {
            androidContext(this@YaoApplication)
            androidLogger(Level.INFO)
            modules(koinModules)
        }
        initApp()
        initBugFly()
        registerActivityListener()
        ViewTarget.setTagId(R.id.glideIndexTag)
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun initApp() {
        try {
            val a = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                ).signingInfo.apkContentsSigners[0]
            } else {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                ).signatures[0]
            }
            val d = a.hashCode()
            if (d == -508714960 || d == 1375692864) {
                isCrack = true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }


    private fun initBugFly() {
        Beta.autoInit = true
        Beta.autoCheckUpgrade = true
        Beta.upgradeCheckPeriod = 60 * 1000
        val strategy = UserStrategy(applicationContext)
        strategy.appChannel = BuildConfig.FLAVOR
        strategy.appVersion = BuildConfig.VERSION_NAME
        strategy.appPackageName = BuildConfig.APPLICATION_ID
        strategy.appReportDelay = 20000
        Bugly.init(applicationContext, "56bf507146", false)
        CrashReport.setIsDevelopmentDevice(applicationContext, BuildConfig.DEBUG)
    }
    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    fun pushActivity(activity: Activity?) {
        mActivates.add(activity)
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    fun popActivity(activity: Activity?) {
        mActivates.remove(activity)
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return if (mActivates.isEmpty()) {
            null
        } else mActivates[mActivates.size - 1]
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    fun finishCurrentActivity() {
        if (mActivates.isEmpty()) {
            return
        }
        val activity = mActivates[mActivates.size - 1]
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        if (mActivates.isEmpty()) {
            return
        }
        if (activity != null) {
            mActivates.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        if (mActivates.isEmpty()) {
            return
        }
        for (activity in mActivates) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 按照指定类名找到activity
     *
     * @param cls
     * @return
     */
    fun findActivity(cls: Class<*>): Activity? {
        var targetActivity: Activity? = null
        for (activity in mActivates) {
            if (activity.javaClass == cls) {
                targetActivity = activity
                break
            }
        }
        return targetActivity
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    fun getTopActivity(): Activity? {
        var mBaseActivity: Activity?
        synchronized(mActivates) {
            val size: Int = mActivates.size - 1
            if (size < 0) {
                return null
            }
            mBaseActivity = mActivates[size]
        }
        return mBaseActivity
    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    fun getTopActivityName(): String? {
        var mBaseActivity: Activity?
        synchronized(mActivates) {
            val size: Int = mActivates.size - 1
            if (size < 0) {
                return null
            }
            mBaseActivity = mActivates[size]
        }
        return mBaseActivity?.javaClass?.name
    }

    /**
     * 结束所有Activity
     */
    private fun finishAllActivity() {
        for (activity in mActivates) {
            activity.finish()
        }
        mActivates.clear()
    }

    /**
     * 退出应用程序
     */
    fun appExit() {
        try {
            BuglyLog.i(BuildConfig.FLAVOR, "app == exit")
            finishAllActivity()
        } catch (e: Exception) {
            BuglyLog.e(BuildConfig.FLAVOR, "app == exit${e.message}")
        }
    }

    private fun registerActivityListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                pushActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (mActivates.isEmpty()) {
                    return
                }
                if (mActivates.contains(activity)) {
                    popActivity(activity)
                }
            }
        })
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
            TaurusHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }

    }

}