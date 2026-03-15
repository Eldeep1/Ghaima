package com.depogramming.ghaima.presentation.alarms.viewmodel

sealed interface AlarmsUiEvent {
    data class ShowSnackBar(val message: String) : AlarmsUiEvent
    object DismissBottomSheet : AlarmsUiEvent
}