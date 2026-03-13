package com.depogramming.ghaima.presentation.utils.location

import com.depogramming.ghaima.data.weather.model.LocationModel

sealed interface LocationResult {
    data class Success(val location: LocationModel) : LocationResult
    object MissingPermission : LocationResult
    object GpsDisabled : LocationResult
    data class Error(val message: String) : LocationResult
}