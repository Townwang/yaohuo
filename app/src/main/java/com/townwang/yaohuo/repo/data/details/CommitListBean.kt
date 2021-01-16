package com.townwang.yaohuo.repo.data.details

data class CommitListBean(
    val floor:Int,
    val url:String,
    val auth:String,
    val avatar:String,
    val time:String,
    val content:String,
    val b:String = ""
)