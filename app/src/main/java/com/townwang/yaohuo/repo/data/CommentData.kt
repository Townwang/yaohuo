package com.townwang.yaohuo.repo.data

data class CommentData(
    val floor:Int,
    val url:String,
    val auth:String,
    val avatar:String,
    val time:String,
    val content:String,
    val b:String = ""
)