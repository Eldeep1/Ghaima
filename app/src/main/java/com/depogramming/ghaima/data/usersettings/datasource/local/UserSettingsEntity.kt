package com.depogramming.ghaima.data.usersettings.datasource.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.depogramming.ghaima.data.weather.model.LocationModel

@Entity(tableName = "settings")
data class UserSettingsEntity (
    @PrimaryKey
    val id: Int = 1,
    val languageCode: String?,
    val units: String?,
    @Embedded
    val location: LocationModel?,
    val windSpeedUnit: String?,
)