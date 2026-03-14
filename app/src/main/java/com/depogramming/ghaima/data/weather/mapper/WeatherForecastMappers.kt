package com.depogramming.ghaima.data.weather.mapper

import com.depogramming.ghaima.data.weather.datasource.local.entity.WeatherForecastEntity
import com.depogramming.ghaima.data.weather.datasource.remote.dto.Data
import com.depogramming.ghaima.data.weather.datasource.remote.dto.WeatherForeCastDTO
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.depogramming.ghaima.data.weather.model.WeatherForecastModel
import com.depogramming.ghaima.data.weather.model.WeatherData
import com.depogramming.ghaima.data.weather.model.HourlyWeatherModel
import com.depogramming.ghaima.data.weather.model.DailyWeatherModel

fun WeatherForeCastDTO.toWeatherForecastModel(
    sourceTempUnit: TemperatureUnit,
    targetTempUnit: TemperatureUnit,
    sourceWindUnit: WindSpeedUnit,
    targetWindUnit: WindSpeedUnit
): WeatherForecastModel {

    val currentData = this.list.first()

    return WeatherForecastModel(
        cityName = this.city.name,
        current = currentData.toCurrentWeather(
            sourceTempUnit,
            targetTempUnit,
            sourceWindUnit,
            targetWindUnit,
        ),

        hourlyForecast = this.list.take(8)
            .map { it.toHourlyWeather(sourceTempUnit, targetTempUnit) },


        dailyForecast = this.list.toDailyWeather(sourceTempUnit, targetTempUnit)
    )
}

private fun Data.toCurrentWeather(
    sourceTemp: TemperatureUnit,
    targetTemp: TemperatureUnit,
    sourceWind: WindSpeedUnit,
    targetWind: WindSpeedUnit
): WeatherData {
    return WeatherData(
        dateAndTime = this.dt.formatUnixDate("EEEE, dd MMM | hh:mm a"),

        temperature = formatTemperature(this.main.temp, sourceTemp, targetTemp),
        description = this.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
            ?: "",
        iconResId = getWeatherIconRes(this.weather.firstOrNull()?.icon ?: ""),
        humidity = "${this.main.humidity}%",
        windSpeed = formatWindSpeed(this.wind.speed, sourceWind, targetWind),
        pressure = "${this.main.pressure}",
        cloudCover = "${this.clouds.all}%"
    )
}

private fun Data.toHourlyWeather(
    sourceTemp: TemperatureUnit,
    targetTemp: TemperatureUnit
): HourlyWeatherModel {
    return HourlyWeatherModel(
        time = this.dt.formatUnixDate("HH:mm"),
        temperature = formatTemperature(this.main.temp, sourceTemp, targetTemp),
        iconResId = getWeatherIconRes(this.weather.firstOrNull()?.icon ?: "")
    )
}

private fun List<Data>.toDailyWeather(
    sourceTemp: TemperatureUnit,
    targetTemp: TemperatureUnit
): List<DailyWeatherModel> {
    return this.groupBy { it.dt.formatUnixDate("yyyy-MM-dd") }
        .map { (_, dailyDataList) ->
            val maxTempRaw = dailyDataList.maxOf { it.main.tempMax }
            val minTempRaw = dailyDataList.minOf { it.main.tempMin }
            val midDayWeather =
                dailyDataList.firstOrNull { it.dtTxt.contains("12:00:00") } ?: dailyDataList.first()

            DailyWeatherModel(
                dayOfWeek = midDayWeather.dt.formatUnixDate("EEEE"),
                description = midDayWeather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "",
                highTemp = formatTemperature(maxTempRaw, sourceTemp, targetTemp),
                lowTemp = formatTemperature(minTempRaw, sourceTemp, targetTemp),
                iconResId = getWeatherIconRes(midDayWeather.weather.firstOrNull()?.icon ?: "")
            )
        }.take(5)
}


private fun Long.formatUnixDate(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

fun WeatherForecastModel.toEntity(): WeatherForecastEntity {
    return WeatherForecastEntity(
        id = 1,
        cityName = this.cityName,
        current = this.current,
        hourlyForecast = this.hourlyForecast,
        dailyForecast = this.dailyForecast
    )
}


fun WeatherForecastEntity.toWeatherForecastModel(): WeatherForecastModel {
    return WeatherForecastModel(
        cityName = this.cityName,
        current = this.current,
        hourlyForecast = this.hourlyForecast,
        dailyForecast = this.dailyForecast
    )
}
