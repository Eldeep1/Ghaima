package com.depogramming.ghaima.data.weather.datasource.remote

import com.depogramming.ghaima.data.weather.datasource.remote.dto.CountriesListDTO
import com.depogramming.ghaima.data.weather.datasource.remote.dto.SingleWeatherDTO
import com.depogramming.ghaima.data.weather.datasource.remote.dto.WeatherForeCastDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {

    @GET("geo/1.0/direct")
    suspend fun searchCity(@Query("q") query:String,@Query("limit") limit: Int = 5): Response<List<CountriesListDTO>>

    @GET("data/2.5/forecast?")
    suspend fun getWeatherForeCast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units:String
    ) : Response<WeatherForeCastDTO>

    @GET("/data/2.5/weather?")
    suspend fun getSingleWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units:String,
    ):Response<SingleWeatherDTO>
}