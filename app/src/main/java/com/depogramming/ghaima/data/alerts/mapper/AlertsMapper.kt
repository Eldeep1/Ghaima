package com.depogramming.ghaima.data.alerts.mapper

import com.depogramming.ghaima.data.alerts.datasource.local.AlertEntity
import com.depogramming.ghaima.data.alerts.datasource.local.AlertType
import com.depogramming.ghaima.data.alerts.model.ExactAlarmAlert
import com.depogramming.ghaima.data.alerts.model.NotificationAlert
import com.depogramming.ghaima.data.alerts.model.WeatherAlertModel

fun AlertEntity.toModel(): WeatherAlertModel {
    return when (this.type) {
        AlertType.ALARM -> ExactAlarmAlert(
            id = this.id,
            description = this.description,
            minTemp = this.minTemp,
            maxTemp = this.maxTemp,
            exactTime = this.startTime,
            isActive = this.isActive,
        )
        AlertType.NOTIFICATION -> NotificationAlert(
            id = this.id,
            description = this.description,
            minTemp = this.minTemp,
            maxTemp = this.maxTemp,
            windowStart = this.startTime,
            windowEnd = this.endTime ?: 0L,
            isActive = this.isActive
        )
    }
}
fun WeatherAlertModel.toEntity(): AlertEntity {
    return when (this) {
        is ExactAlarmAlert -> AlertEntity(
            id = this.id,
            type = AlertType.ALARM,
            description = this.description,
            minTemp = this.minTemp,
            maxTemp = this.maxTemp,
            isActive = this.isActive,
            startTime = this.exactTime,
            endTime = null
        )
        is NotificationAlert -> AlertEntity(
            id = this.id,
            type = AlertType.NOTIFICATION,
            description = this.description,
            minTemp = this.minTemp,
            maxTemp = this.maxTemp,
            isActive = this.isActive,
            startTime = this.windowStart,
            endTime = this.windowEnd
        )
    }
}