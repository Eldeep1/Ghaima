package com.depogramming.ghaima.data.weather.model

data class FavouriteWeatherModel(
    val cityName: String,
    val latt: Double,
    val long: Double,
    val dateAndTime: String,
    val temperature: String,
    val description: String,
    val iconResId: Int,
    val humidity: String,
    val windSpeed: String,
    val pressure: String,
    val cloudCover: String
)