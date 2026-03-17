package com.depogramming.ghaima.data.weather.datasource.remote

import com.depogramming.ghaima.data.weather.datasource.WeatherRemoteDataSource
import com.depogramming.ghaima.data.weather.datasource.remote.dto.CountriesListDTO
import com.depogramming.ghaima.data.weather.datasource.remote.dto.SingleWeatherDTO
import com.depogramming.ghaima.data.weather.datasource.remote.dto.WeatherForeCastDTO
import retrofit2.Response


class WeatherRemoteDataSourceImpl (private val weatherService: WeatherService) :
    WeatherRemoteDataSource {

    override suspend fun searchCity(query:String): Response<List<CountriesListDTO>> {
        return weatherService.searchCity(query)
    }
    override suspend fun getWeatherForeCast(latitude: Double, longitude: Double, language: String): Response<WeatherForeCastDTO> {
        return weatherService.getWeatherForeCast(latitude,longitude,language,"metric")
    }
    override suspend fun getSingleWeather(latitude: Double, longitude: Double, language: String): Response<SingleWeatherDTO>{
        return weatherService.getSingleWeather(latitude,longitude,language,"metric")
    }
}