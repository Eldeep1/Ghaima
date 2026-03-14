package com.depogramming.ghaima.data.weather.datasource.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favourites",
    primaryKeys = ["latt", "long"]
)
data class FavouritesEntity(
    val cityName:String,
    val latt: Double,
    val long: Double,
    val dateAndTime: String,
    val temperature: Double,
    val description: String,
    val iconResId: Int,
    val humidity: String,
    val windSpeed: Double,
    val pressure: String,
    val cloudCover: String
)