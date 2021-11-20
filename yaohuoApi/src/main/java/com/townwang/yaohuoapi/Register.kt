package com.townwang.yaohuoapi

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.townwang.yaohuoapi.factory.DocumentConverterFactory
import com.townwang.yaohuoapi.interceptor.AddCookiesInterceptor
import com.townwang.yaohuoapi.interceptor.RequestHeaderInterceptor
import com.townwang.yaohuoapi.interceptor.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.util.concurrent.TimeUnit

open class Register {
    fun createGson(): Gson {
       return GsonBuilder()
            .serializeNulls()
            .setDateFormat(DateFormat.LONG)
            .setPrettyPrinting()
            .create()
    }

    fun createRetrofit(mContext:Context): Retrofit {
      return  Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_YAOHUO_URL)
            .client(OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(interceptor = AddCookiesInterceptor(mContext))
                .addInterceptor(interceptor = RequestHeaderInterceptor())
                .addInterceptor(interceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .addInterceptor(interceptor = SaveCookiesInterceptor(mContext))
                .build()
            )
            .addConverterFactory(DocumentConverterFactory())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()
    }
}