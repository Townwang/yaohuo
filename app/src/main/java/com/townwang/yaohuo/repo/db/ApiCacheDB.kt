package com.townwang.yaohuo.repo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.townwang.yaohuo.repo.data.HomeData
import com.townwang.yaohuo.repo.data.details.SmailIngBean

//@Database(
//        entities = [
//            SmailIngBean::class
//        ],
//        exportSchema = true,
//        version = 1
//)
//@TypeConverters(AppTypeConverters::class)
//abstract class ApiCacheDB : RoomDatabase() {
//    abstract fun apiCacheDAO(): ApiCacheDAO
//}