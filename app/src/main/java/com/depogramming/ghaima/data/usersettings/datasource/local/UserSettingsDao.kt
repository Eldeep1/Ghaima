package com.depogramming.ghaima.data.usersettings.datasource.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM settings")
    fun getUserSettings(): Flow<UserSettingsEntity?>
}