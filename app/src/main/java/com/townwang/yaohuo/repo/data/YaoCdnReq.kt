package com.townwang.yaohuo.repo.data

import com.google.gson.annotations.SerializedName

data class YaoCdnReq(
    @SerializedName("code")
    val code: Int,
    @SerializedName("msg")
    val msg: String? = "",
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("imgid")
    val imgid: String? = "",
    @SerializedName("relative_path")
    val relative_path: String? = "https://tu.yaohuo.me/imgs/2021/01/988f5cbc5fa54b40.png",
    @SerializedName("url")
    val url: String? = "",
    @SerializedName("thumbnail_url")
    val thumbnail_url: String? = "",
    @SerializedName("width")
    val width: Int? = 0,
    @SerializedName("height")
    val height: Int? = 0,
    @SerializedName("delete")
    val delete: String? = ""
)