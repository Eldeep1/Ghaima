package com.depogramming.ghaima.presentation.alarms.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.alerts.model.ExactAlarmAlert
import com.depogramming.ghaima.data.alerts.model.NotificationAlert
import com.depogramming.ghaima.data.alerts.model.WeatherAlertModel
import com.depogramming.ghaima.data.alerts.repository.AlertsRepo
import com.depogramming.ghaima.worker.BackgroundScheduler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

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


    fun saveAlert(context: Context) {
        viewModelScope.launch {
            val currentForm = _formState.value


            val startMinutesFromMidnight = (currentForm.startHour * 60L) + currentForm.startMinute
            val endMinutesFromMidnight = (currentForm.endHour * 60L) + currentForm.endMinute

            if (currentForm.isNotificationMode) {
                val durationInMinutes = if (endMinutesFromMidnight >= startMinutesFromMidnight) {
                    endMinutesFromMidnight - startMinutesFromMidnight
                } else {
                    (24 * 60 - startMinutesFromMidnight) + endMinutesFromMidnight
                }
                if (durationInMinutes < 60) {
                    _uiEvent.emit(AlarmsUiEvent.ShowSnackBar(context.getString(R.string.time_window_must_be_at_least_1_hour)))
                    return@launch
                }
            }
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

            result.onSuccess { insertedId ->

                if (!currentForm.isNotificationMode) {

                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, currentForm.startHour)
                        set(Calendar.MINUTE, currentForm.startMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)


                        //schedule for tomorrow if the time has already passed...
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }


                    BackgroundScheduler.scheduleExactAlarm(
                        context = context,
                        timeInMillis = calendar.timeInMillis,
                        alarmId = insertedId.toInt()
                    )
                }

                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar(context.getString(R.string.alert_saved_successfully)))
                _uiEvent.emit(AlarmsUiEvent.DismissBottomSheet)
                _formState.value = AlarmFormState()

            }.onFailure {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar(context.getString(R.string.failed_to_save_alert_please_try_again)))
            }
        }
    }


    fun deleteAlert(context: Context, alert: WeatherAlertModel) {
        viewModelScope.launch {
            val result = alertsRepository.deleteAlert(alert)

            result.onSuccess {
                if (alert is ExactAlarmAlert) {
                    BackgroundScheduler.cancelExactAlarm(context, alert.id)
                }

                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar(context.getString(R.string.alert_deleted)))
            }.onFailure {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar(context.getString(R.string.could_not_delete_alert)))
            }
        }
    }

    fun toggleAlertActiveState(context: Context, alert: WeatherAlertModel, isActive: Boolean) {
        viewModelScope.launch {

            val updatedAlert = when (alert) {
                is ExactAlarmAlert -> alert.copy(isActive = isActive)
                is NotificationAlert -> alert.copy(isActive = isActive)
            }

            val result = alertsRepository.insertAlert(updatedAlert)

            result.onSuccess {

                if (updatedAlert is ExactAlarmAlert) {
                    if (isActive) {

                        val alertHour = (updatedAlert.exactTime / 60).toInt()
                        val alertMinute = (updatedAlert.exactTime % 60).toInt()

                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, alertHour)
                            set(Calendar.MINUTE, alertMinute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)

                            if (timeInMillis <= System.currentTimeMillis()) {
                                add(Calendar.DAY_OF_YEAR, 1)
                            }
                        }

                        BackgroundScheduler.scheduleExactAlarm(
                            context = context,
                            timeInMillis = calendar.timeInMillis,
                            alarmId = updatedAlert.id
                        )
                    } else {
                        BackgroundScheduler.cancelExactAlarm(context, updatedAlert.id)
                    }
                }

                val status = if (isActive) context.getString(R.string.enabled) else context.getString(R.string.disabled)
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar(context.getString(R.string.alert, status)))

            }.onFailure {
                _uiEvent.emit(AlarmsUiEvent.ShowSnackBar(context.getString(R.string.failed_to_update_status)))
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AlarmsViewModelFactory(
    private val alertsRepository: AlertsRepo
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmsViewModel(alertsRepository) as T
    }
}