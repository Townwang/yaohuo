package com.townwang.yaohuo.repo.data.details

data class CommitListBean(
    val floor:Int,
    val url:String,
    var auth:String,
    var avatar:String,
    val time:String,
    val content:String,
    val b:String = "",
    var level:Int? = 0
)