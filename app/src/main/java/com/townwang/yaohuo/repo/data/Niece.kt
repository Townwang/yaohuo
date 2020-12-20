package com.townwang.yaohuo.repo.data

open class Niece(
    val data: List<Data>,
    val code: Int,
    val message: String
)

data class Data(
    val phone: String
)