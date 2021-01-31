package com.townwang.yaohuo.ui.weight.pay

import androidx.annotation.DrawableRes
import java.io.Serializable

data class PayConfig(
    val aliZhiKey: String,
    @DrawableRes val aliQaImage: Int,
    @DrawableRes val weChatQaImage: Int,
    val weChatTip: String? = null,
    val aliTip: String? = null
): Serializable