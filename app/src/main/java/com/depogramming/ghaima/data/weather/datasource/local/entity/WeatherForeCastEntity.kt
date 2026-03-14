package com.depogramming.ghaima.data.weather.datasource.local.entity

import androidx.room.Entity
import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.depogramming.ghaima.data.weather.model.WeatherData
import com.depogramming.ghaima.data.weather.model.DailyWeatherModel
import com.depogramming.ghaima.data.weather.model.HourlyWeatherModel


@Entity(tableName = "weather")
data class WeatherForecastEntity(

    @PrimaryKey
    val id: Int = 1,

    val cityName: String,


    @Embedded
    val current: WeatherData,

    val hourlyForecast: List<HourlyWeatherModel>,
    val dailyForecast: List<DailyWeatherModel>
)