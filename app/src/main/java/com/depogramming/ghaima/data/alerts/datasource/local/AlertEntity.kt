package com.depogramming.ghaima.data.alerts.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. The Type Flag
enum class AlertType {
    ALARM,
    NOTIFICATION
}

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: AlertType,
    val description: String,
    val minTemp: Double,
    val maxTemp: Double,
    val startTime: Long,
    val endTime: Long?,
    val isActive: Boolean
)