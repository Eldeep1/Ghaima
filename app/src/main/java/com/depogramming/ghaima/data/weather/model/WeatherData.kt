package com.depogramming.ghaima.data.weather.model

data class WeatherData(
    val dateAndTime: String,
    val temperature: String,
    val description: String,
    val iconResId: Int,
    val humidity: String,
    val windSpeed: String,
    val pressure: String,
    val cloudCover: String
)
