package com.depogramming.ghaima.data.weather.datasource

import com.depogramming.ghaima.data.weather.datasource.remote.dto.CountriesListDTO
import com.depogramming.ghaima.data.weather.datasource.remote.dto.SingleWeatherDTO
import com.depogramming.ghaima.data.weather.datasource.remote.dto.WeatherForeCastDTO
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun searchCity(query: String): Response<List<CountriesListDTO>>

    suspend fun getWeatherForeCast(latitude: Double, longitude: Double, language: String): Response<WeatherForeCastDTO>

    suspend fun getSingleWeather(latitude: Double, longitude: Double, language: String): Response<SingleWeatherDTO>
}
