package com.townwang.yaohuo.repo.data

open class Neice(
    val data: List<Data>,
    val code: Int,
    val message: String
)

data class Data(
    val phone: String
)