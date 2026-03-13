package com.depogramming.ghaima.data.weather.model


data class WeatherForecastModel(
    val cityName: String,
    val current: CurrentWeatherModel,
    val hourlyForecast: List<HourlyWeatherModel>,
    val dailyForecast: List<DailyWeatherModel>
)

data class CurrentWeatherModel(
    val dateAndTime: String,
    val temperature: String,
    val description: String,
    val iconResId: Int,
    val humidity: String,
    val windSpeed: String,
    val pressure: String,
    val cloudCover: String
)


data class HourlyWeatherModel(
    val time: String,
    val temperature: String,
    val iconResId: Int
)

data class DailyWeatherModel(
    val dayOfWeek: String,
    val description: String,
    val highTemp: String,
    val lowTemp: String,
    val iconResId: Int
)