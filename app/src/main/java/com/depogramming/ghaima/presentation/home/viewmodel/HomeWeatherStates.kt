package com.depogramming.ghaima.presentation.home.viewmodel

import com.depogramming.ghaima.data.weather.model.WeatherForecastModel

sealed class HomeWeatherStates {
    data object Loading: HomeWeatherStates()
    data class Success(val data:WeatherForecastModel): HomeWeatherStates()
    data class Error(val error:String): HomeWeatherStates()
}