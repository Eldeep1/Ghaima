package com.depogramming.ghaima.data.alerts.model

data class ExactAlarmAlert(
    override val id: Int,
    override val description: String,
    override val minTemp: Double,
    override val maxTemp: Double,
    override val isActive:Boolean,
    val exactTime: Long
) : WeatherAlertModel