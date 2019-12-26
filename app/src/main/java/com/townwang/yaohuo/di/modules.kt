package com.townwang.yaohuo.di

import com.google.gson.GsonBuilder
import com.townwang.yaohuo.api.Api
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.api.JsApi
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.ui.fragment.list.ListModel
import com.townwang.yaohuo.ui.fragment.details.DetailsModel
import com.townwang.yaohuo.ui.fragment.login.LoginModel
import com.townwang.yaohuo.ui.fragment.splash.SplashModel
import com.townwang.yaohuo.ui.fragment.theme.ThemeModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat

private val netModule = module {
    single {
        GsonBuilder()
            .serializeNulls()
            .setDateFormat(DateFormat.LONG)
            .setPrettyPrinting()
            .create()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor {
                    it.proceed(
                        it.request().newBuilder()
                            .build()
                    )
                }
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .build()
            )
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    single {
        get<Retrofit>().create(Api::class.java)
    }
    single {
        JsApi()
    }
}

private val repoModule = module {
    single { Repo(get(), get(),get()) }
}
private val viewModelModule = module {
    viewModel { ListModel(get()) }
    viewModel { LoginModel(get()) }
    viewModel { ThemeModel() }
    viewModel { DetailsModel(get()) }
    viewModel { SplashModel(get()) }
}

val koinModules = viewModelModule + repoModule + netModule