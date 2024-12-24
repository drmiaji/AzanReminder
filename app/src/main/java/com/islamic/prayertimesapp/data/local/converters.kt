package com.islamic.prayertimesapp.data.local

import androidx.room.TypeConverter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.islamic.prayertimesapp.data.models.Data

class converters {

    private val gson = Gson()

    @TypeConverter
    fun fromDataList(dataList: List<Data>): String {
        return gson.toJson(dataList)
    }

    @TypeConverter
    fun toDataList(dataListString: String): List<Data> {
        val type = object : TypeToken<List<Data>>() {}.type
        return gson.fromJson(dataListString, type)
    }
}