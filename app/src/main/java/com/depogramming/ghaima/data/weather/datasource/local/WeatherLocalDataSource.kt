package com.depogramming.ghaima.data.weather.datasource.local

class WeatherLocalDataSource(val weatherForecastDao: WeatherForecastDao) {
    suspend fun getWeatherForecast(): WeatherForecastEntity {
        return weatherForecastDao.getWeatherForecast()
    }

    suspend fun insertWeatherForecast(weatherForecast: WeatherForecastEntity) {
        weatherForecastDao.insertWeatherForecast(weatherForecast)
    }

}