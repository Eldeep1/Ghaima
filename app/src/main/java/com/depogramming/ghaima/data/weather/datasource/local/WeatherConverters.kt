package com.depogramming.ghaima.data.weather.datasource.local

import androidx.room.TypeConverter
import com.depogramming.ghaima.data.weather.model.DailyWeatherModel
import com.depogramming.ghaima.data.weather.model.HourlyWeatherModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromHourlyList(hourlyList: List<HourlyWeatherModel>): String {
        return gson.toJson(hourlyList)
    }

    @TypeConverter
    fun toHourlyList(json: String): List<HourlyWeatherModel> {
        val type = object : TypeToken<List<HourlyWeatherModel>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromDailyList(dailyList: List<DailyWeatherModel>): String {
        return gson.toJson(dailyList)
    }

    @TypeConverter
    fun toDailyList(json: String): List<DailyWeatherModel> {
        val type = object : TypeToken<List<DailyWeatherModel>>() {}.type
        return gson.fromJson(json, type)
    }
}