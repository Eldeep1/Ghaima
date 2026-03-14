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
    targetTempUnit: TemperatureUnit,
    targetWindUnit: WindSpeedUnit
): FavouriteWeatherModel {
    val weatherInfo = this.weather.firstOrNull()

    return FavouriteWeatherModel(
        cityName = this.name,
        latt = this.coord.lat,
        long = this.coord.lon,

        dateAndTime = this.dt.formatUnixDate("EEEE, dd MMM | hh:mm a"),

        temperature = formatTemperature(
            rawValue = this.main.temp,
            sourceUnit = TemperatureUnit.CELSIUS,
            targetUnit = targetTempUnit
        ),

        description = weatherInfo?.description?.replaceFirstChar { it.uppercase() } ?: "",
        iconResId = getWeatherIconRes(weatherInfo?.icon ?: ""),
        humidity = "${this.main.humidity}%",

        windSpeed = formatWindSpeed(
            rawValue = this.wind.speed,
            sourceUnit = WindSpeedUnit.METER_PER_SECOND,
            targetUnit = targetWindUnit
        ),

        pressure = "${this.main.pressure}",
        cloudCover = "${this.clouds.all}%"
    )
}

fun SingleWeatherDTO.toFavouritesEntity(): FavouritesEntity {

    val weatherInfo = this.weather.firstOrNull()

    return FavouritesEntity(
        cityName = this.name,
        latt = this.coord.lat,
        long = this.coord.lon,


        dateAndTime = this.dt.formatUnixDate("EEEE, dd MMM | hh:mm a"),


        temperature = this.main.temp,
        windSpeed = this.wind.speed,


        description = weatherInfo?.description?.replaceFirstChar { it.uppercase() } ?: "",
        iconResId = getWeatherIconRes(weatherInfo?.icon ?: ""),
        humidity = "${this.main.humidity}%",
        pressure = "${this.main.pressure}",
        cloudCover = "${this.clouds.all}%"
    )
}

fun FavouritesEntity.toFavouriteWeatherModel(
    targetTempUnit: String,
    targetWindUnit: String
): FavouriteWeatherModel {
    return FavouriteWeatherModel(
        cityName = this.cityName,
        latt = this.latt,
        long = this.long,
        dateAndTime = this.dateAndTime,

        temperature = formatTemperature(
            rawValue = this.temperature,
            sourceUnit = TemperatureUnit.CELSIUS,
            targetUnit = TemperatureUnit.fromApiValue(targetTempUnit)
        ),
        windSpeed = formatWindSpeed(
            rawValue = this.windSpeed,
            sourceUnit = WindSpeedUnit.METER_PER_SECOND,
            targetUnit = WindSpeedUnit.fromDbValue(targetWindUnit)
        ),

        description = this.description,
        iconResId = this.iconResId,
        humidity = this.humidity,
        pressure = this.pressure,
        cloudCover = this.cloudCover
    )
}

fun FavouriteWeatherModel.toFavouritesEntity(): FavouritesEntity {
    //it's ok to have dummy values here as the room only cares about the primary key
    return FavouritesEntity(
        cityName = this.cityName,
        latt = this.latt,
        long = this.long,
        dateAndTime = this.dateAndTime,
        temperature = 0.0,
        windSpeed = 0.0,
        description = this.description,
        iconResId = this.iconResId,
        humidity = this.humidity,
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

