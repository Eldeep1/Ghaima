package com.depogramming.ghaima.data.weather.datasource.local.dao

import org.junit.Assert.*

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.depogramming.ghaima.data.db.AppDatabase
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouritesDaoTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private lateinit var favouritesDao: FavouritesDao

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val application = ApplicationProvider.getApplicationContext<Application>()

        db = Room.inMemoryDatabaseBuilder(
            application.applicationContext,
            AppDatabase::class.java //
        )
            .allowMainThreadQueries()
            .build()

        favouritesDao = db.favouritesDao()
    }

    @After
    fun clear() {
        db.close()
    }

    @Test
    fun insertFavourite_getAllFavourites_returnsInsertedItem() = runTest {
        val favourite = FavouritesEntity(
            cityName = "Cairo",
            latt = 30.0444, long = 31.2357, dateAndTime = "",
            temperature = 0.0, description = "", iconResId = 0,
            humidity = "", windSpeed = 8.0, pressure = "", cloudCover = ""
        )

        favouritesDao.insertFavourite(favourite)

        val loadedList = favouritesDao.getAllFavourites().first()

        assertThat(loadedList.size, `is`(1))
        assertThat(loadedList[0].cityName, `is`("Cairo"))
    }

    @Test
    fun deleteFavourite_getAllFavourites_returnsEmptyList() = runTest {
        val favourite = FavouritesEntity(
            cityName = "Alexandria",
            latt = 31.2001, long = 29.9187, dateAndTime = "",
            temperature = 0.0, description = "", iconResId = 0,
            humidity = "", windSpeed = 8.0, pressure = "", cloudCover = ""
        )
        favouritesDao.insertFavourite(favourite)

        favouritesDao.deleteFavourite(favourite)

        val loadedList = favouritesDao.getAllFavourites().first()

        assertThat(loadedList.size, `is`(0))
    }
}