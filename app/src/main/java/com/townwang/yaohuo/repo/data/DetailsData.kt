package com.townwang.yaohuo.repo.data

data class DetailsData(
    val title: String,
    val time: String,
    val content:String,
    val giftMoney: String? =null,
    val reward:String? = null
)