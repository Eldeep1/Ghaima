package com.depogramming.ghaima.data.weather.datasource.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherForeCastService {
    @GET("data/2.5/forecast?")
    suspend fun getWeatherForeCast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units:String
    ) : Response<WeatherForeCastDTO>
}