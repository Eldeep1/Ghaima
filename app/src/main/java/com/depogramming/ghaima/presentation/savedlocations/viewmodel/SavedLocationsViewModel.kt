package com.depogramming.ghaima.presentation.savedlocations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.WeatherRepositoryImpl
import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel
import com.depogramming.ghaima.data.weather.model.LocationModel
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit
import com.depogramming.ghaima.presentation.utils.location.LocationHelper
import com.depogramming.ghaima.presentation.utils.location.LocationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class SavedLocationsViewModel(
    private val weatherRepository: WeatherRepositoryImpl,
    private val userSettingsRepo: UserSettingsRepo,
    private val locationHelper: LocationHelper
) : ViewModel() {
    private val _favouritesState = MutableStateFlow<SavedStates>(SavedStates.Loading)
    val favouritesState: StateFlow<SavedStates> = _favouritesState.asStateFlow()
    private val _askForPermission = MutableSharedFlow<Unit>()
    val askForPermission = _askForPermission.asSharedFlow()
    private val _openLocationSettingsEvent = MutableSharedFlow<Unit>()
    val openLocationSettingsEvent = _openLocationSettingsEvent.asSharedFlow()

    var userSettings: UserSettingsModel?=null
    init {
        observeFavourites()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeFavourites() {
        viewModelScope.launch {
            userSettingsRepo.getUserData()
                .flatMapLatest { result ->
                    userSettings=result
                    val targetTemp = result?.units ?: TemperatureUnit.CELSIUS.apiValue
                    val targetWind = result?.windSpeedUnit ?: WindSpeedUnit.METER_PER_SECOND.dbValue

                    weatherRepository.getFavourites(
                        targetTempUnit = targetTemp,
                        targetWindUnit = targetWind
                    )
                }
                .collect { favourites ->
                    if (favourites.isEmpty()) {
                        _favouritesState.value = SavedStates.EmptyList
                    } else
                    _favouritesState.value = SavedStates.Success(favourites)
                }
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            when (val result = locationHelper.getCurrentLocation()) {
                is LocationResult.Success -> {
                    addToFavourites(result.location)
                }
                is LocationResult.MissingPermission -> {
                    _askForPermission.emit(Unit)
                }
                is LocationResult.GpsDisabled -> {
                    _openLocationSettingsEvent.emit(Unit)
                }
                is LocationResult.Error -> {
                    // sad
                }
            }
        }
    }
    fun onLocationPermissionResult(fineLocationGranted: Boolean, coarseLocationGranted: Boolean) {
        if (fineLocationGranted || coarseLocationGranted) {
            fetchCurrentLocation()
        } else {
            //consider showing snack bar that says that we have failed getting the location
        }
    }
    fun addToFavourites(location: LocationModel){
        viewModelScope.launch {
            weatherRepository.addToFavourites(
                location.latitude,
                location.longitude,
                userSettings?.languageCode?:"en",
                location.place
            )
        }
    }

    fun deleteFavourite(favourite: FavouriteWeatherModel) {
        viewModelScope.launch {
            weatherRepository.deleteFavourite(favourite)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SavedLocationsViewModelFactory(
    private val weatherRepository: WeatherRepositoryImpl,
    private val userSettingsRepo: UserSettingsRepo,
    private val locationHelper: LocationHelper,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SavedLocationsViewModel(weatherRepository, userSettingsRepo,locationHelper) as T
    }
}
