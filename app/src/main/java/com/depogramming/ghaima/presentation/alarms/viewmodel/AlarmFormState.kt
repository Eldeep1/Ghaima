package com.depogramming.ghaima.presentation.alarms.viewmodel

data class AlarmFormState(
    val isNotificationMode: Boolean = true,
    val description: String = "",
    val minTemp: Double = 5.0,
    val maxTemp: Double = 19.0,

    val startHour: Int = 8,
    val startMinute: Int = 0,
    val endHour: Int = 18,
    val endMinute: Int = 0
)