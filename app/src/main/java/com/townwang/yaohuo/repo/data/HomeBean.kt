package com.townwang.yaohuo.repo.data

data class HomeBean(
    var title:String,
    val a:String,
    val auth:String,
    val reply:String,
    val read:String,
    val time:String,
    val smailIng:List<String>
)