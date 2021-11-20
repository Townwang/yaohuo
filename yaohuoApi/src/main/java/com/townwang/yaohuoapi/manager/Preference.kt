package com.townwang.yaohuoapi.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.townwang.yaohuoapi.clearList
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import kotlin.jvm.Throws
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Context.config(key: String, value: String? = null): String {
    var config: String by Preference(
        this,
        key,
        default = "1"
    )
    return if (value.isNullOrEmpty()) {
        config
    } else {
        config = value
        config
    }
}

@SuppressLint("CommitPrefEdits", "NewApi")
fun Context.clearConfig() {
    clearList.forEach {
        val sp = getSharedPreferences(
            it,
            Context.MODE_PRIVATE
        )
        val editor  = sp.edit()
        editor.clear()
        editor.apply()
    }
}
@Suppress("UNCHECKED_CAST")
class Preference<T>(private val context: Context, private val string:String, private val default : T) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(string, default)
    }

    private val prefs: SharedPreferences by lazy{context.getSharedPreferences("Realnen",Context.MODE_PRIVATE)}

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(string, value)
    }

    @SuppressLint("NewApi")
    private fun<A> putPreference(name:String, value:A)= with(prefs.edit()){
        when(value){
            is Long -> putLong(name,value)
            is String -> putString(name,value)
            is Int -> putInt(name,value)
            is Boolean -> putBoolean(name,value)
            is Float -> putFloat(name,value)
            else ->   putString(name,serialize(value))
        }.apply()
    }
    private fun <U> findPreference(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> this.getString(name, default)!!
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can not be saved")
        }
        res as U
    }

    @Throws(IOException::class)
    private fun<A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
            objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()

        return serStr}

}