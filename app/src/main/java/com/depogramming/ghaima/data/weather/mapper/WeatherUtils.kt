package com.depogramming.ghaima.data.weather.mapper

import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit

 fun formatTemperature(rawValue: Double, sourceUnit: TemperatureUnit, targetUnit: TemperatureUnit): String {
    val tempInCelsius = when (sourceUnit) {
        TemperatureUnit.FAHRENHEIT -> (rawValue - 32) * 5 / 9
        TemperatureUnit.KELVIN -> rawValue - 273.15
        TemperatureUnit.CELSIUS -> rawValue
    }
    return when (targetUnit) {
        TemperatureUnit.FAHRENHEIT -> "${((tempInCelsius * 9 / 5) + 32).toInt()}°F"
        TemperatureUnit.KELVIN -> "${(tempInCelsius + 273.15).toInt()}K"
        TemperatureUnit.CELSIUS -> "${tempInCelsius.toInt()}°C"
    }
}

 fun formatWindSpeed(rawValue: Double, sourceUnit: WindSpeedUnit, targetUnit: WindSpeedUnit): String {
    val windInMs = when (sourceUnit) {
        WindSpeedUnit.MILES_PER_HOUR -> rawValue / 2.23694  // mph to m/s
        WindSpeedUnit.METER_PER_SECOND -> rawValue          // Already m/s
    }
    return when (targetUnit) {
        WindSpeedUnit.MILES_PER_HOUR -> "${(windInMs * 2.23694).toInt()} mph"
        WindSpeedUnit.METER_PER_SECOND -> "${windInMs.toInt()} m/s"
    }
}

 fun getWeatherIconRes(iconCode: String): Int {
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