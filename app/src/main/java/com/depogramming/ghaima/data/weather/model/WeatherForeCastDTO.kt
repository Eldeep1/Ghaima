package com.depogramming.ghaima.data.weather.model

import com.google.gson.annotations.SerializedName

data class WeatherForeCastDTO(
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<Data>,
    val city: City,
)

data class Data(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Double,
    val rain: Rain?,
    val sys: Sys,
    @SerializedName("dt_txt")
    val dtTxt: String,
    val snow: Snow?,
)
data class Rain(
    @SerializedName("3h")
    val n3h: Double,
)
data class Snow(
    @SerializedName("3h")
    val n3h: Double,
)
data class City(
    val id: Long,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Long,
    val timezone: Long,
    val sunrise: Long,
    val sunset: Long,
)
