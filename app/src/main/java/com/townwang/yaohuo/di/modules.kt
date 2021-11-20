package com.townwang.yaohuo.di

import android.app.Application
import android.content.Context
import com.tencent.bugly.beta.Beta
import com.townwang.yaohuo.repo.Repo
import com.townwang.yaohuo.ui.fragment.home.HomeModel
import com.townwang.yaohuo.ui.fragment.login.LoginModel
import com.townwang.yaohuo.ui.fragment.me.MeModel
import com.townwang.yaohuo.ui.fragment.msg.MsgModel
import com.townwang.yaohuo.ui.fragment.msg.details.MsgDetailsModel
import com.townwang.yaohuo.ui.fragment.pub.PubListModel
import com.townwang.yaohuo.ui.fragment.pub.details.PubDetailsModel
import com.townwang.yaohuo.ui.fragment.search.SearchModel
import com.townwang.yaohuo.ui.fragment.send.SendModel
import com.townwang.yaohuo.ui.fragment.send.UploadFileModel
import com.townwang.yaohuo.ui.fragment.splash.SplashModel
import com.townwang.yaohuo.ui.fragment.theme.ThemeModel
import com.townwang.yaohuoapi.Api
import com.townwang.yaohuoapi.Register
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private val netModule = module {
    single {
        Register().createGson()
    }

    single {
        Register().createRetrofit(get())
    }
    single {
        get<Retrofit>().create(Api::class.java)
    }
    single {
        Beta.init(get(), false)
    }
}
private val storageModule = module {
//    single {
//        Room.databaseBuilder(get(), ApiCacheDB::class.java, "persist.db")
//            .addMigrations(
//                object : Migration(1, 2) {
//                    override fun migrate(database: SupportSQLiteDatabase) {
//                        database.execSQL("ALTER TABLE UserInfo ADD COLUMN `id` TEXT")
//                    }
//                },
//                object : Migration(2, 3) {
//                    override fun migrate(database: SupportSQLiteDatabase) {
//                        database.execSQL("DROP TABLE IF EXISTS UserInfo")
//
//                    }
//                })
//            .build()
//    }
//
//    single {
//        get<ApiCacheDB>().apiCacheDAO()
//    }
    single(named("config")) {
        get<Application>().getSharedPreferences("config", Context.MODE_PRIVATE)
    }
}
private val repoModule = module {
    single { Repo(get()) }
}
private val viewModelModule = module {
    viewModel { PubListModel(get()) }
    viewModel { SearchModel(get()) }
    viewModel { LoginModel(get()) }
    viewModel { ThemeModel() }
    viewModel { PubDetailsModel(get()) }
    viewModel { SplashModel(get()) }
    viewModel { MeModel(get()) }
    viewModel { HomeModel(get()) }
    viewModel { SendModel(get()) }
    viewModel { MsgModel(get()) }
    viewModel { UploadFileModel(get()) }
    viewModel { MsgDetailsModel(get()) }

}

val koinModules = viewModelModule + repoModule + netModule