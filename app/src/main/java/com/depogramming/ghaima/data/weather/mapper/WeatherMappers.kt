package com.depogramming.ghaima.data.weather.mapper

import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.weather.datasource.local.WeatherForecastEntity
import com.depogramming.ghaima.data.weather.datasource.remote.dto.Data
import com.depogramming.ghaima.data.weather.datasource.remote.dto.WeatherForeCastDTO
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.depogramming.ghaima.data.weather.model.WeatherForecastModel
import com.depogramming.ghaima.data.weather.model.CurrentWeatherModel
import com.depogramming.ghaima.data.weather.model.HourlyWeatherModel
import com.depogramming.ghaima.data.weather.model.DailyWeatherModel
import com.depogramming.ghaima.data.weather.datasource.remote.dto.SingleWeatherDTO
import java.text.SimpleDateFormat
import java.util.Date

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
): CurrentWeatherModel {
    return CurrentWeatherModel(
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

private fun formatTemperature(
    rawValue: Double,
    sourceUnit: TemperatureUnit,
    targetUnit: TemperatureUnit
): String {


    val tempInCelsius = when (sourceUnit) {
        TemperatureUnit.FAHRENHEIT -> (rawValue - 32) * 5 / 9
        TemperatureUnit.KELVIN -> rawValue - 273.15
        TemperatureUnit.CELSIUS -> rawValue
    }


    return when (targetUnit) {
        TemperatureUnit.FAHRENHEIT -> {
            val fahrenheit = (tempInCelsius * 9 / 5) + 32
            "${fahrenheit.toInt()}°F"
        }

        TemperatureUnit.KELVIN -> {
            val kelvin = tempInCelsius + 273.15
            "${kelvin.toInt()}K"
        }

        TemperatureUnit.CELSIUS -> {
            "${tempInCelsius.toInt()}°C"
        }
    }
}

private fun formatWindSpeed(
    rawValue: Double,
    sourceUnit: WindSpeedUnit,
    targetUnit: WindSpeedUnit
): String {


    val windInMs = when (sourceUnit) {
        WindSpeedUnit.MILES_PER_HOUR -> rawValue / 2.23694  // mph to m/s
        WindSpeedUnit.METER_PER_SECOND -> rawValue          // Already m/s
    }


    return when (targetUnit) {
        WindSpeedUnit.MILES_PER_HOUR -> {
            val mph = windInMs * 2.23694
            "${mph.toInt()} mph"
        }

        WindSpeedUnit.METER_PER_SECOND -> {
            "${windInMs.toInt()} m/s"
        }
    }
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

fun SingleWeatherDTO.toCurrentWeatherModel(): CurrentWeatherModel {
    val weatherData = this.weather.firstOrNull()

    return CurrentWeatherModel(
        dateAndTime = formatUnixTime(this.dt),
        temperature = "${this.main.temp.toInt()}°",
        description = weatherData?.description?.replaceFirstChar { it.uppercase() } ?: "Unknown",
        iconResId = getWeatherIconRes(weatherData?.icon ?: ""),
        humidity = "${this.main.humidity}%",
        windSpeed = "${this.wind.speed}",
        pressure = "${this.main.pressure} hPa",
        cloudCover = "${this.clouds.all}%"
    )
}

private fun formatUnixTime(unixSeconds: Long): String {

    val date = Date(unixSeconds * 1000L)

    val sdf = SimpleDateFormat("EEE, d MMM HH:mm", Locale.getDefault())
    return sdf.format(date)
}

private fun getWeatherIconRes(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.clear_day_ic
        "01n" -> R.drawable.clear_night_ic
        "02d", "03d", "04d" -> R.drawable.partly_cloudy_ic
        "02n", "03n", "04n" -> R.drawable.cloudy_night_ic
        "09d", "09n", "10d", "10n" -> R.drawable.rainy_ic
        "11d", "11n" -> R.drawable.storm_ic
        "13d", "13n" -> R.drawable.snow_ic
        "50d", "50n" -> R.drawable.mist_ic
        else -> R.drawable.clear_day_ic
    }
}
