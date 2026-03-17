package com.depogramming.ghaima.data.weather.datasource.local

import com.depogramming.ghaima.data.weather.datasource.local.dao.FavouritesDao
import com.depogramming.ghaima.data.weather.datasource.local.dao.WeatherForecastDao
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import com.depogramming.ghaima.data.weather.datasource.local.entity.WeatherForecastEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImp(
    private val weatherForecastDao: WeatherForecastDao,
    private val favouritesDao: FavouritesDao
    ) : WeatherLocalDataSource {
    override suspend fun getWeatherForecast(): WeatherForecastEntity {
        return weatherForecastDao.getWeatherForecast()
    }

    override suspend fun insertWeatherForecast(weatherForecast: WeatherForecastEntity) {
        weatherForecastDao.insertWeatherForecast(weatherForecast)
    }

    override fun getAllFavourites(): Flow<List<FavouritesEntity>> {
        return favouritesDao.getAllFavourites()
    }
    override suspend fun insertWeatherFavourite(favourite: FavouritesEntity) {
        favouritesDao.insertFavourite(favourite)
    }
    override suspend fun deleteWeatherFavourites(favourite: FavouritesEntity) {
        favouritesDao.deleteFavourite(favourite)
    }

}