package com.depogramming.ghaima.data.weather.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Query("Select * from favourites")
    fun getAllFavourites(): Flow<List<FavouritesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouritesEntity)

    @Delete
    suspend fun deleteFavourite(favourite: FavouritesEntity)

}