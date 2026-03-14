package com.depogramming.ghaima.data.weather.datasource.remote

import com.depogramming.ghaima.data.weather.datasource.remote.dto.WeatherForeCastDTO
import retrofit2.Response

class WeatherRemoteDataSource (private val weatherForeCastService: WeatherForeCastService){

    suspend fun getWeatherForeCast(latitude: Double, longitude: Double, language: String): Response<WeatherForeCastDTO> {
        return weatherForeCastService.getWeatherForeCast(latitude,longitude,language,"metric")
    }
}