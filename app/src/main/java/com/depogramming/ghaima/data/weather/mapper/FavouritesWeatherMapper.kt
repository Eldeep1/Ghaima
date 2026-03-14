package com.depogramming.ghaima.data.weather.mapper

import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import com.depogramming.ghaima.data.weather.datasource.remote.dto.SingleWeatherDTO
import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun SingleWeatherDTO.toFavouriteWeatherModel(
    sourceTempUnit: TemperatureUnit,
    targetTempUnit: TemperatureUnit,
    sourceWindUnit: WindSpeedUnit,
    targetWindUnit: WindSpeedUnit
): FavouriteWeatherModel {
    val weatherInfo = this.weather.firstOrNull()

    return FavouriteWeatherModel(

        cityName = this.name,
        latt = this.coord.lat,
        long = this.coord.lon,

        dateAndTime = this.dt.formatUnixDate("EEEE, dd MMM | hh:mm a"),
        temperature = formatTemperature(this.main.temp, sourceTempUnit, targetTempUnit),
        description = weatherInfo?.description?.replaceFirstChar { it.uppercase() } ?: "",
        iconResId = getWeatherIconRes(weatherInfo?.icon ?: ""),
        humidity = "${this.main.humidity}%",
        windSpeed = formatWindSpeed(this.wind.speed, sourceWindUnit, targetWindUnit),
        pressure = "${this.main.pressure}",
        cloudCover = "${this.clouds.all}%"
    )
}

fun FavouriteWeatherModel.toFavouritesEntity(): FavouritesEntity {
    return FavouritesEntity(
        cityName = this.cityName,
        latt = this.latt,
        long = this.long,
        dateAndTime = this.dateAndTime,
        temperature = this.temperature,
        description = this.description,
        iconResId = this.iconResId,
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        pressure = this.pressure,
        cloudCover = this.cloudCover
    )
}


fun FavouritesEntity.toFavouriteWeatherModel(): FavouriteWeatherModel {
    return FavouriteWeatherModel(
        cityName = this.cityName,
        latt = this.latt,
        long = this.long,
        dateAndTime = this.dateAndTime,
        temperature = this.temperature,
        description = this.description,
        iconResId = this.iconResId,
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        pressure = this.pressure,
        cloudCover = this.cloudCover
    )
}



private fun Long.formatUnixDate(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

