package com.depogramming.ghaima.data.weather.model

data class HourlyWeatherModel(
    val time: String,
    val temperature: String,
    val iconResId: Int
)