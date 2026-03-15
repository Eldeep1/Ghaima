package com.depogramming.ghaima.data.alerts.repository
import com.depogramming.ghaima.data.alerts.datasource.local.AlertsLocalDataSource
import com.depogramming.ghaima.data.alerts.mapper.toEntity
import com.depogramming.ghaima.data.alerts.mapper.toModel
import com.depogramming.ghaima.data.alerts.model.WeatherAlertModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlertsRepositoryImpl(
    private val alertsLocalDataSource: AlertsLocalDataSource
) : AlertsRepo {

    override fun getAllAlerts(): Flow<List<WeatherAlertModel>> {

        return alertsLocalDataSource.getAllAlertsFlow().map { entitiesList ->
            entitiesList.map { entity -> entity.toModel() }
        }
    }

    override suspend fun getActiveAlertsSync(): Result<List<WeatherAlertModel>> {
        return try {
            val entities = alertsLocalDataSource.getActiveAlertsSync()
            Result.success(entities.map { it.toModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertAlert(alert: WeatherAlertModel): Result<Unit> {
        return try {
            alertsLocalDataSource.insertAlert(alert.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAlert(alert: WeatherAlertModel): Result<Unit> {
        return try {
            alertsLocalDataSource.deleteAlert(alert.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}