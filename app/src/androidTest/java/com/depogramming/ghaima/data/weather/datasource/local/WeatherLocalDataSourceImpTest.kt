package com.depogramming.ghaima.data.weather.datasource.local

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.depogramming.ghaima.data.db.AppDatabase
import com.depogramming.ghaima.data.weather.datasource.local.dao.FavouritesDao
import com.depogramming.ghaima.data.weather.datasource.local.dao.WeatherForecastDao
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceImpTest {
    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: WeatherLocalDataSourceImp
    private lateinit var favouritesDao: FavouritesDao
    private lateinit var weatherForecastDao: WeatherForecastDao
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val application= ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
        )
            .build()
        favouritesDao=db.favouritesDao()
        weatherForecastDao=db.weatherForecastDao()
        localDataSource= WeatherLocalDataSourceImp(weatherForecastDao,favouritesDao)
    }
    @After
    fun clear(){
        db.close()
    }
    @Test
    fun saveFavourite_getFavourite() = runTest {

        val favourite = FavouritesEntity(
            cityName = "Cairo",
            latt = 30.0444,
            long = 31.2357,
            dateAndTime = "",
            temperature = 0.0,
            description = "",
            iconResId = 0,
            humidity = "",
            windSpeed = 8.0,
            pressure="",
            cloudCover = ""
        )


        localDataSource.insertWeatherFavourite(favourite)


        val expectedFavouriteList = localDataSource.getAllFavourites().first { it.isNotEmpty() }


        MatcherAssert.assertThat(expectedFavouriteList.size, `is`(1))
        MatcherAssert.assertThat(expectedFavouriteList[0].cityName, `is`("Cairo"))
    }
    @Test
    fun saveMultipleFavourites_getAllFavourites_returnsCorrectSize() = runTest {

        val favourite1 = FavouritesEntity(
            cityName = "Cairo",
            latt = 30.0444, long = 31.2357, dateAndTime = "",
            temperature = 0.0, description = "", iconResId = 0,
            humidity = "", windSpeed = 8.0, pressure = "", cloudCover = ""
        )

        val favourite2 = FavouritesEntity(
            cityName = "Alexandria",
            latt = 31.2001, long = 29.9187, dateAndTime = "",
            temperature = 0.0, description = "", iconResId = 0,
            humidity = "", windSpeed = 8.0, pressure = "", cloudCover = ""
        )


        localDataSource.insertWeatherFavourite(favourite1)
        localDataSource.insertWeatherFavourite(favourite2)

        val expectedFavouriteList = localDataSource.getAllFavourites().first { it.size == 2 }


        MatcherAssert.assertThat(expectedFavouriteList.size, `is`(2))

        val savedCityNames = expectedFavouriteList.map { it.cityName }
        MatcherAssert.assertThat(savedCityNames.contains("Cairo"), `is`(true))
        MatcherAssert.assertThat(savedCityNames.contains("Alexandria"), `is`(true))
    }
    @Test
    fun deleteFavourite_getFavourite() = runTest {

        val favourite = FavouritesEntity(
            cityName = "Cairo",
            latt = 30.0444,
            long = 31.2357,
            dateAndTime = "",
            temperature = 0.0,
            description = "",
            iconResId = 0,
            humidity = "",
            windSpeed = 8.0,
            pressure = "",
            cloudCover = ""
        )


        localDataSource.insertWeatherFavourite(favourite)

        val listAfterInsert = localDataSource.getAllFavourites().first { it.size == 1 }

        MatcherAssert.assertThat(listAfterInsert.size, `is`(1))

        localDataSource.deleteWeatherFavourites(favourite)

        val listAfterDelete = localDataSource.getAllFavourites().first { it.isEmpty() }

        MatcherAssert.assertThat(listAfterDelete.size, `is`(0))
    }
}