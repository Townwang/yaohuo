package com.townwang.yaohuo.repo.data.details

data class DetailsContentBean(
    val reward:String,
    val giftMoney:String,
    val time:String,
    val praiseSize:String,
    val userName:String,
    val onLineState:Boolean = false,
    val headUrl:String,
    val content:String,
    val level:Int,
    val medal:List<String>,
    val downloadList: List<DownloadBean>?,
    val touserId:String?
)