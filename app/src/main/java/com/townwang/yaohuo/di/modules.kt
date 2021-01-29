package com.townwang.yaohuo.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.GsonBuilder
import com.tencent.bugly.beta.Beta
import com.townwang.yaohuo.BuildConfig
import com.townwang.yaohuo.api.Api
import com.townwang.yaohuo.di.factory.DocumentConverterFactory
import com.townwang.yaohuo.di.interceptor.AddCookiesInterceptor
import com.townwang.yaohuo.di.interceptor.NetCookiesInterceptor
import com.townwang.yaohuo.di.interceptor.SaveCookiesInterceptor
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.repo.db.ApiCacheDB
import com.townwang.yaohuo.ui.fragment.details.DetailsModel
import com.townwang.yaohuo.ui.fragment.home.HomeModel
import com.townwang.yaohuo.ui.fragment.login.LoginModel
import com.townwang.yaohuo.ui.fragment.me.MeModel
import com.townwang.yaohuo.ui.fragment.pub.ListModel
import com.townwang.yaohuo.ui.fragment.search.SearchModel
import com.townwang.yaohuo.ui.fragment.send.SendModel
import com.townwang.yaohuo.ui.fragment.splash.SplashModel
import com.townwang.yaohuo.ui.fragment.theme.ThemeModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
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
            .baseUrl(BuildConfig.BASE_YAOHUO_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor {
                    it.proceed(
                        it.request().newBuilder()
                            .build()
                    )
                }
                .addInterceptor(NetCookiesInterceptor())
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .addInterceptor(AddCookiesInterceptor(get()))
                .addInterceptor(SaveCookiesInterceptor(get()))
                .build()
            )
            .addConverterFactory(DocumentConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single {
        get<Retrofit>().create(Api::class.java)
    }
    single {
        Beta.init(get(), false)
    }
}
private val storageModule = module {
    single {
        Room.databaseBuilder(get(), ApiCacheDB::class.java, "persist.db")
            .addMigrations(
                object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE UserInfo ADD COLUMN `id` TEXT")
                    }
                },
                object : Migration(2, 3) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("DROP TABLE IF EXISTS UserInfo")

                    }
                })
            .build()
    }

    single {
        get<ApiCacheDB>().apiCacheDAO()
    }
    single(named("config")) {
        get<Application>().getSharedPreferences("config", Context.MODE_PRIVATE)
    }
}
    private val repoModule = module {
        single { Repo(get()) }
    }
    private val viewModelModule = module {
        viewModel { ListModel(get()) }
        viewModel { SearchModel(get()) }
        viewModel { LoginModel(get()) }
        viewModel { ThemeModel() }
        viewModel { DetailsModel(get()) }
        viewModel { SplashModel(get()) }
        viewModel { MeModel(get()) }
        viewModel { HomeModel(get()) }
        viewModel { SendModel(get()) }

    }

    val koinModules = viewModelModule + repoModule + netModule