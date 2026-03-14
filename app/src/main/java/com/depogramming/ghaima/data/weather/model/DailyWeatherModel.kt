package com.depogramming.ghaima.data.weather.model

data class DailyWeatherModel(
    val dayOfWeek: String,
    val description: String,
    val highTemp: String,
    val lowTemp: String,
    val iconResId: Int
)