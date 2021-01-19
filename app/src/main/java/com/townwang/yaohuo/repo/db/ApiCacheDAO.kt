package com.townwang.yaohuo.repo.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.townwang.yaohuo.repo.db.entities.UserInfo
@Dao
interface ApiCacheDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUserInfo(info: UserInfo)
    @Query("select * from UserInfo where touserid = :id limit 1")
    fun getUserInfo(id: String): UserInfo?

    @Delete
    fun deleteUserInfo(userInfos: List<UserInfo>)

    @Update
    fun updateUserInfo(userInfo: UserInfo)

    @Query("select * from UserInfo")
    fun getUserInfo(): LiveData<List<UserInfo>>

    @Transaction
    fun cacheProfile(newUserInfo: UserInfo) {
        val userInfo = getUserInfo(newUserInfo.touserid)
        when {
            userInfo == null -> {
                addUserInfo(newUserInfo)
            }
            userInfo != userInfo -> {
                newUserInfo.avatarUrl = userInfo.avatarUrl
                newUserInfo.sex = userInfo.sex
                updateUserInfo(newUserInfo)
            }
            else -> {
                // TODO: 2021/1/19/019 dddd
            }
        }
    }
}