package com.townwang.yaohuo.repo.db

import androidx.paging.DataSource
import androidx.room.*
import com.townwang.yaohuo.repo.data.HomeData
import java.util.*
//
//@Dao
//interface ApiCacheDAO {
//
//    @Query("select * from HomeData where classId = :classId order by date desc")
//    fun getVTHistoryDataSource(classId: Int): DataSource.Factory<Int, HomeData>
//
//    @Query("select * from HomeData where classId = :classId and date = :date limit 1")
//    fun getVTHistory(classId: Int, date: Date): HomeData?
//
//    @Query("select count(*) from HomeData where title = :title")
//    fun getVTHistoryCount(title: String) : Int
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun addVTHistory(history: HomeData)
//
//    @Transaction
//    fun cacheVTHistories(profileId: Int, histories: List<HomeData>) {
//        histories.sortedByDescending { it.date }.forEach { history ->
//            val date = history.date
//            val oldHistory = getVTHistory(profileId, date)
//            if (oldHistory == null) {
//                val vtHistory = HomeData(
//                    UUID.randomUUID(),
//                    history.classId,
//                    history.title,
//                    history.a,
//                    history.auth,
//                    history.reply,
//                    history.read,
//                    history.time,
//                    history.smailIng,
//                    date
//                )
//                addVTHistory(vtHistory)
//            }
//        }
//    }

//}