package com.depogramming.ghaima.data.weather

import com.depogramming.ghaima.data.weather.datasource.remote.dto.CountriesListDTO
import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel
import com.depogramming.ghaima.data.weather.model.WeatherForecastModel
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun searchCity(query: String): Result<List<CountriesListDTO>>
    fun getFavourites(
        targetTempUnit: String, targetWindUnit: String
    ): Flow<List<FavouriteWeatherModel>>

    suspend fun addToFavourites(
        latitude: Double,
        longitude: Double,
        language: String,
        place: String? = null,
    ): Result<Unit>

    suspend fun deleteFavourite(
        favouriteWeatherModel: FavouriteWeatherModel
    ): Result<Unit>

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        language: String,
        tempUnit: String,
        windUnit: String,
    ): Result<WeatherForecastModel>

    suspend fun getLocalWeatherForecast(): WeatherForecastModel

    suspend fun insertWeatherForecast(weatherForecastModel: WeatherForecastModel)
}