package com.wing.tree.android.wordle.data.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class TypeConverters {
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Int>>() {
    }

    @TypeConverter
    fun dateToJson(value: Date): String = gson.toJson(value)

    @TypeConverter
    fun jsonToDate(value: String): Date = gson.fromJson(value, Date::class.java)

    @TypeConverter
    fun indicesToJson(value: List<Int>): String = gson.toJson(value)

    @TypeConverter
    fun jsonToIndices(value: String): List<Int> = gson.fromJson(value, typeToken.type)
}