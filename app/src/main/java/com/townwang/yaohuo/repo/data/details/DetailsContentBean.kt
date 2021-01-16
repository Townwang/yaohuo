package com.townwang.yaohuo.repo.data.details

data class DetailsContentBean(
    val title:String,
    val reward:String,
    val giftMoney:String,
    val time:String,
    val praiseSize:String,
    val userName:String,
    val onLineState:Boolean = false,
    val headUrl:String,
    val content:String,
    val downloadList: List<DownloadBean>?
)