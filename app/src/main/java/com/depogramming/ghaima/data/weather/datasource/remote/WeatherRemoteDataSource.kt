package com.depogramming.ghaima.data.weather.datasource.remote

import com.depogramming.ghaima.data.weather.datasource.remote.dto.CountriesListDTO
import com.depogramming.ghaima.data.weather.datasource.remote.dto.WeatherForeCastDTO
import retrofit2.Response

class WeatherRemoteDataSource (private val weatherService: WeatherService){

    suspend fun searchCity(query:String): Response<List<CountriesListDTO>> {
        return weatherService.searchCity(query)
    }
    suspend fun getWeatherForeCast(latitude: Double, longitude: Double, language: String): Response<WeatherForeCastDTO> {
        return weatherService.getWeatherForeCast(latitude,longitude,language,"metric")
    }
}