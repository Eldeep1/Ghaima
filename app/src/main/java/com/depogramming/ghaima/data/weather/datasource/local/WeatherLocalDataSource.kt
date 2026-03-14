package com.depogramming.ghaima.data.weather.datasource.local

import com.depogramming.ghaima.data.weather.datasource.local.dao.FavouritesDao
import com.depogramming.ghaima.data.weather.datasource.local.dao.WeatherForecastDao
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import com.depogramming.ghaima.data.weather.datasource.local.entity.WeatherForecastEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(
    private val weatherForecastDao: WeatherForecastDao,
    private val favouritesDao: FavouritesDao
    ) {
    suspend fun getWeatherForecast(): WeatherForecastEntity {
        return weatherForecastDao.getWeatherForecast()
    }

    suspend fun insertWeatherForecast(weatherForecast: WeatherForecastEntity) {
        weatherForecastDao.insertWeatherForecast(weatherForecast)
    }

    fun getAllFavourites(): Flow<List<FavouritesEntity>> {
        return favouritesDao.getAllFavourites()
    }
    suspend fun insertWeatherFavourite(favourite: FavouritesEntity) {
        favouritesDao.insertFavourite(favourite)
    }
    suspend fun deleteWeatherFavourites(favourite: FavouritesEntity) {
        favouritesDao.deleteFavourite(favourite)
    }

}