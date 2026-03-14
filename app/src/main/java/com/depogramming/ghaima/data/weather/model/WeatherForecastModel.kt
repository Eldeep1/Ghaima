package com.depogramming.ghaima.data.weather.model


data class WeatherForecastModel(
    val cityName: String,
    val current: WeatherData,
    val hourlyForecast: List<HourlyWeatherModel>,
    val dailyForecast: List<DailyWeatherModel>
)





