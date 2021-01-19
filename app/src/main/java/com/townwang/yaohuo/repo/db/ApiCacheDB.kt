package com.townwang.yaohuo.repo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.townwang.yaohuo.repo.db.entities.UserInfo

@Database(
        entities = [
            UserInfo::class
        ],
        exportSchema = true,
        version = 3
)
@TypeConverters(AppTypeConverters::class)
abstract class ApiCacheDB : RoomDatabase() {
    abstract fun apiCacheDAO(): ApiCacheDAO
}