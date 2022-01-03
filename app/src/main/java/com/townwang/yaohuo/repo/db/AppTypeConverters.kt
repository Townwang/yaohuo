package com.townwang.yaohuo.repo.db

//
//class AppTypeConverters {
//    @TypeConverter
//    fun uuid2String(id: UUID): String = id.toString()
//
//    @TypeConverter
//    fun string2UUID(string: String): UUID = UUID.fromString(string)
//
//
//    @TypeConverter
//    fun date2Long(date: Date?): Long? = date?.time
//
//    @TypeConverter
//    fun long2Date(long: Long?): Date? = if (long == null) null else Date(long)
//
//    @TypeConverter
//    fun elements2String(smailIng: Elements?): String? = smailIng?.toString()
//
//    @TypeConverter
//    fun string2Elements(string: String?): Elements? =
//        if (string == null) null else Jsoup.parse(string).select(IMG_GIF)
//
//
//}