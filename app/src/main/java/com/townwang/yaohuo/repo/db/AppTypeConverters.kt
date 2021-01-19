package com.townwang.yaohuo.repo.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

class AppTypeConverters {

    companion object {
        val gson = Gson()
    }
   @TypeConverter
    fun uuid2String(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun string2UUID(string: String): UUID = UUID.fromString(string)

    @TypeConverter
    fun date2Long(date: Date?): Long? = date?.time

    @TypeConverter
    fun long2Date(long: Long?): Date? = if (long == null) null else Date(long)

}