package com.depogramming.ghaima.data.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.depogramming.ghaima.data.alerts.datasource.local.AlertEntity
import com.depogramming.ghaima.data.alerts.datasource.local.AlertsDao
import com.depogramming.ghaima.data.usersettings.datasource.local.UserSettingsDao
import com.depogramming.ghaima.data.usersettings.datasource.local.UserSettingsEntity
import com.depogramming.ghaima.data.weather.datasource.local.WeatherConverters
import com.depogramming.ghaima.data.weather.datasource.local.dao.FavouritesDao
import com.depogramming.ghaima.data.weather.datasource.local.dao.WeatherForecastDao
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import com.depogramming.ghaima.data.weather.datasource.local.entity.WeatherForecastEntity


@Database(entities = [UserSettingsEntity::class, WeatherForecastEntity::class, FavouritesEntity::class, AlertEntity::class], version = 3,exportSchema = false)
@TypeConverters(WeatherConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao

    abstract fun weatherForecastDao(): WeatherForecastDao

    abstract fun favouritesDao(): FavouritesDao

    abstract fun alertsDao(): AlertsDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movies_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

