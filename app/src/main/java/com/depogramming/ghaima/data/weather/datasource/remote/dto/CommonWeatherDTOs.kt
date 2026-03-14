package com.depogramming.ghaima.data.weather.datasource.remote.dto

import com.google.gson.annotations.SerializedName

data class Coord(
    val lon: Double,
    val lat: Double,
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String,
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Long,
    val humidity: Long,
    @SerializedName("sea_level")
    val seaLevel: Long,
    @SerializedName("grnd_level")
    val grndLevel: Long,
)

data class Wind(
    val speed: Double,
    val deg: Long,
    val gust: Double? = null
)

data class Clouds(
    val all: Long,
)

data class Sys(
    val type: Long? = null,
    val id: Long? = null,
    val country: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
)