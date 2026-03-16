package com.depogramming.ghaima.data.alerts.model

sealed interface WeatherAlertModel {
    val id: Int
    val description: String
    val minTemp: Double
    val maxTemp: Double
    val isActive:Boolean
}