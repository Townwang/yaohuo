package com.townwang.yaohuo.common.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.townwang.yaohuo.R
import com.townwang.yaohuo.YaoApplication
import com.townwang.yaohuo.ui.activity.ActivityLogin


class LoginHelper {
    companion object{
        val instance = LoginHelper()
    }
    fun restartLogin(context:Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.alert_dialog_tip_title))
        builder.setMessage("你的Cookie已过期，请重新登陆。")
        builder.setCancelable(false)
        builder.setPositiveButton("确定") { dialog, which ->
            val app = context.applicationContext
            if (app is YaoApplication) {
                app.appExit()
            }
            ContextCompat.startActivity(context, Intent(context, ActivityLogin::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }, null)
            dialog.dismiss()
        }
        val alterDialog = builder.create()
        alterDialog.show()
    }
}