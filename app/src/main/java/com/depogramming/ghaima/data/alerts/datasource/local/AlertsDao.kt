package com.depogramming.ghaima.data.alerts.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {

    @Query("SELECT * FROM alerts ORDER BY startTime ASC")
    fun getAllAlertsFlow(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts")
    suspend fun getActiveAlertsSync(): List<AlertEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity):Long

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("SELECT * FROM alerts WHERE id = :id LIMIT 1")
    suspend fun getAlertById(id: Int): AlertEntity?
}