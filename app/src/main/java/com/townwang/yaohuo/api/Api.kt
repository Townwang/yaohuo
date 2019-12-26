package com.townwang.yaohuo.api
import com.townwang.yaohuo.repo.data.Neice
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("app/json/yaohuo/neice.do")
    fun neice(): Call<Neice>
}