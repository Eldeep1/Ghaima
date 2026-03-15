package com.depogramming.ghaima.presentation.alarms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.alerts.model.ExactAlarmAlert
import com.depogramming.ghaima.data.alerts.model.NotificationAlert
import com.depogramming.ghaima.data.alerts.model.WeatherAlertModel
import com.depogramming.ghaima.data.alerts.repository.AlertsRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlarmsViewModel(
    private val alertsRepository: AlertsRepo
) : ViewModel() {


    val savedAlerts = alertsRepository.getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    private val _formState = MutableStateFlow(AlarmFormState())
    val formState = _formState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<AlarmsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateMode(isNotification: Boolean) {
        _formState.update { it.copy(isNotificationMode = isNotification) }
    }

    fun updateDescription(desc: String) {
        _formState.update { it.copy(description = desc) }
    }

    fun updateTempRange(min: Double, max: Double) {
        _formState.update { it.copy(minTemp = min, maxTemp = max) }
    }

    fun updateStartTime(hour: Int, minute: Int) {
        _formState.update { it.copy(startHour = hour, startMinute = minute) }
    }

    fun updateEndTime(hour: Int, minute: Int) {
        _formState.update { it.copy(endHour = hour, endMinute = minute) }
    }


    fun saveAlert() {
        viewModelScope.launch {
            val currentForm = _formState.value


            val startMinutesFromMidnight = (currentForm.startHour * 60L) + currentForm.startMinute
            val endMinutesFromMidnight = (currentForm.endHour * 60L) + currentForm.endMinute

            val newAlert = if (currentForm.isNotificationMode) {
                NotificationAlert(
                    id = 0,
                    description = currentForm.description,
                    minTemp = currentForm.minTemp,
                    maxTemp = currentForm.maxTemp,
                    isActive = true,
                    windowStart = startMinutesFromMidnight,
                    windowEnd = endMinutesFromMidnight
                )
            } else {
                ExactAlarmAlert(
                    id = 0,
                    description = currentForm.description,
                    minTemp = currentForm.minTemp,
                    maxTemp = currentForm.maxTemp,
                    isActive = true,
                    exactTime = startMinutesFromMidnight
                )
            }

            val result = alertsRepository.insertAlert(newAlert)
            result.onSuccess {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar("Alert saved successfully"))
                _uiEvent.emit(AlarmsUiEvent.DismissBottomSheet)
                _formState.value = AlarmFormState()
            }.onFailure {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar("Failed to save alert. Please try again."))
            }

        }
    }


    fun deleteAlert(alert: WeatherAlertModel) {
        viewModelScope.launch {
            val result = alertsRepository.deleteAlert(alert)

            result.onSuccess {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar("Alert deleted"))
            }.onFailure {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar("Could not delete alert"))
            }
        }
    }

    fun toggleAlertActiveState(alert: WeatherAlertModel, isActive: Boolean) {
        viewModelScope.launch {

            val updatedAlert = when (alert) {
                is ExactAlarmAlert -> alert.copy(isActive = isActive)
                is NotificationAlert -> alert.copy(isActive = isActive)
            }
            alertsRepository.insertAlert(updatedAlert)
            val result = alertsRepository.insertAlert(updatedAlert)

            result.onSuccess {
                val status = if (isActive) "enabled" else "disabled"
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar("Alert $status"))
            }.onFailure {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar("Failed to update status"))
            }
        }
    }
}