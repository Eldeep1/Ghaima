package com.depogramming.ghaima.data.weather

import com.depogramming.ghaima.data.weather.datasource.local.WeatherLocalDataSource
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import com.depogramming.ghaima.data.weather.datasource.local.entity.WeatherForecastEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(val weatherFavouritesEntityList: MutableList<FavouritesEntity>): WeatherLocalDataSource {
    override suspend fun getWeatherForecast(): WeatherForecastEntity {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeatherForecast(weatherForecast: WeatherForecastEntity) {
        TODO("Not yet implemented")
    }

    override fun getAllFavourites(): Flow<List<FavouritesEntity>> {
        return flowOf(weatherFavouritesEntityList.toList())
    }

    override suspend fun insertWeatherFavourite(favourite: FavouritesEntity) {
        weatherFavouritesEntityList.add(favourite)
    }

    override suspend fun deleteWeatherFavourites(favourite: FavouritesEntity) {
        // Delete by "Primary Key" (City Name), just like Room does!
        weatherFavouritesEntityList.removeAll { savedItem ->
            savedItem.cityName == favourite.cityName
        }
    }
}