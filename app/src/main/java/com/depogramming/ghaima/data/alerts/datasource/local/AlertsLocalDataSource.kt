package com.depogramming.ghaima.data.alerts.datasource.local

import kotlinx.coroutines.flow.Flow

class AlertsLocalDataSource(private val alertsDao: AlertsDao) {

    fun getAllAlertsFlow(): Flow<List<AlertEntity>> {
        return alertsDao.getAllAlertsFlow()
    }

    suspend fun getActiveAlertsSync(): List<AlertEntity> {
        return alertsDao.getActiveAlertsSync()
    }

    suspend fun insertAlert(alert: AlertEntity):Long {
        return alertsDao.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertEntity) {
        alertsDao.deleteAlert(alert)
    }
    suspend fun getAlertById(id: Int): AlertEntity? {
        return alertsDao.getAlertById(id)
    }

}