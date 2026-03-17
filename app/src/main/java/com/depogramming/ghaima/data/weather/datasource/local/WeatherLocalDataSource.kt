package com.depogramming.ghaima.data.weather.datasource.local

import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import com.depogramming.ghaima.data.weather.datasource.local.entity.WeatherForecastEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun getWeatherForecast(): WeatherForecastEntity

    suspend fun insertWeatherForecast(weatherForecast: WeatherForecastEntity)
    fun getAllFavourites(): Flow<List<FavouritesEntity>>

    suspend fun insertWeatherFavourite(favourite: FavouritesEntity)

    suspend fun deleteWeatherFavourites(favourite: FavouritesEntity)
}