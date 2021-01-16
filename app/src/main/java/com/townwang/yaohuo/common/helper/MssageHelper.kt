package com.townwang.yaohuo.common.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.tencent.bugly.crashreport.BuglyLog
import com.townwang.yaohuo.App
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.R
import com.townwang.yaohuo.common.IMG_GIF
import com.townwang.yaohuo.common.WEB_VIEW_URL_KEY
import com.townwang.yaohuo.common.WEB_VIEW_URL_TITLE
import com.townwang.yaohuo.ui.activity.ActivityWebView
import org.jsoup.nodes.Document


const val NOTIFY_ID = 0x123

var isHaveMessage = false
fun isHaveMsg(doc: Document): Document {
    isHaveMessage =  try {
        val image = doc.select(IMG_GIF)
        if (image.first().attr("src") == "/tupian/news.gif") {
            BuglyLog.i(BuildConfig.FLAVOR, "有新消息")
            true
        } else {
            BuglyLog.i(BuildConfig.FLAVOR, "无消息")
            false
        }
    }catch (e:Exception){
        BuglyLog.i(BuildConfig.FLAVOR, "无消息")
        false
    }
    return doc
}

fun sendNotification(context: Context) {
    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE)
    if (notificationManager is NotificationManager) {
        val builder = Notification.Builder(context)
        builder.setContentText("点击查看消息")
            .setContentTitle("新消息")
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.drawable.ic_baseline_notification_24)
            .setWhen(System.currentTimeMillis()) //设置通知时间，默认为系统发出通知的时间，通常不用设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("001", "my_channel", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true) //是否在桌面icon右上角展示小红点
            channel.lightColor = Color.RED //小红点颜色
            channel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId("001")
        }
        val intent = Intent(context, ActivityWebView::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra(WEB_VIEW_URL_KEY, "https://yaohuo.me/bbs/messagelist.aspx")
            putExtra(WEB_VIEW_URL_TITLE, "消息")
        }
        val pi = PendingIntent.getActivity(
            context, 0, intent, 0
        )
        builder.setContentIntent(pi)
        notificationManager.notify(NOTIFY_ID, builder.build())
    }
}
