package com.depogramming.ghaima.data.weather.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherForecastDao {
    @Query("SELECT * FROM weather")
    suspend fun getWeatherForecast(): WeatherForecastEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherForecast(weatherForecast: WeatherForecastEntity)

}