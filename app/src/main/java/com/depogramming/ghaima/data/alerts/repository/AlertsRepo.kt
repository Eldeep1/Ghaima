package com.depogramming.ghaima.data.alerts.repository

import com.depogramming.ghaima.data.alerts.model.WeatherAlertModel
import kotlinx.coroutines.flow.Flow

interface AlertsRepo {
    suspend fun getActiveAlertsSync(): Result<List<WeatherAlertModel>>
    suspend fun insertAlert(alert: WeatherAlertModel): Result<Long>
    suspend fun deleteAlert(alert: WeatherAlertModel): Result<Unit>
    fun getAllAlerts(): Flow<List<WeatherAlertModel>>
    suspend fun getAlertById(alarmId: Int):WeatherAlertModel?
}
