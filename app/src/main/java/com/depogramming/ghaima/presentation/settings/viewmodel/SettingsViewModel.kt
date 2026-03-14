package com.depogramming.ghaima.presentation.settings.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.model.LocationModel
import com.depogramming.ghaima.presentation.utils.Languages
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit
import com.depogramming.ghaima.presentation.utils.location.LocationHelper
import com.depogramming.ghaima.presentation.utils.location.LocationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userSettingsRepo: UserSettingsRepo,
    private val locationHelper: LocationHelper
) : ViewModel() {
    //the temperature drop down
    private val _selectedTempUnitIndex = MutableStateFlow(0)
    val selectedTempUnitIndex = _selectedTempUnitIndex.asStateFlow()
    val availableTempUnits = TemperatureUnit.entries

    fun saveTempSelection(selectedIndex: Int) {
        _selectedTempUnitIndex.value = selectedIndex
        val value = availableTempUnits[selectedIndex].apiValue

        viewModelScope.launch {
            updateSettings { it.copy(units = value) }
        }
    }

    //the wind drop down
    private val _selectedWindSpeedIndex = MutableStateFlow(0)
    val selectedWindSpeedIndex = _selectedWindSpeedIndex.asStateFlow()
    val availableWindSpeeds = WindSpeedUnit.entries

    fun saveWindSpeedSelection(selectedIndex: Int) {
        _selectedWindSpeedIndex.value = selectedIndex
        val dbValueToSave = availableWindSpeeds[selectedIndex].dbValue

        viewModelScope.launch { updateSettings { it.copy(windSpeedUnit = dbValueToSave) } }
    }

    //the language dropdown
    private val _selectedLanguageIndex = MutableStateFlow(0)
    val selectedLanguageIndex = _selectedLanguageIndex.asStateFlow()
    val languagesList = Languages.entries
    fun saveLanguageSelection(selectedIndex: Int) {
        _selectedLanguageIndex.value = selectedIndex

        val languageCode = if (selectedIndex == 0) "en" else "ar"

        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)

        viewModelScope.launch {
            updateSettings { it.copy(languageCode = languageCode) }
        }
    }

    //THE  LOCATION FIGHT ROUND TWO
    private val _userLocation=MutableStateFlow(LocationModel(0.0,0.0,""))
    val userLocation=_userLocation.asStateFlow()
    private val _askForPermission = MutableSharedFlow<Unit>()
    val askForPermission = _askForPermission.asSharedFlow()
    private val _openLocationSettingsEvent = MutableSharedFlow<Unit>()
    val openLocationSettingsEvent = _openLocationSettingsEvent.asSharedFlow()
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            when (val result = locationHelper.getCurrentLocation()) {
                is LocationResult.Success -> {
                    updateSettings { it.copy(location = result.location) }
                    _userLocation.value=result.location
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
    private suspend fun updateSettings(update: (UserSettingsModel) -> UserSettingsModel) {
        val currentSettings = userSettingsRepo.getUserData().firstOrNull()
            ?: UserSettingsModel(null, null, null, null)

        userSettingsRepo.setUserSettings(update(currentSettings))
    }

    init {
        viewModelScope.launch {
            userSettingsRepo.getUserData().firstOrNull()?.let { savedSettings ->

                val tempIndex =
                    availableTempUnits.indexOfFirst { it.apiValue == savedSettings.units }

                _selectedTempUnitIndex.value = tempIndex


                val windIndex =
                    availableWindSpeeds.indexOfFirst { it.dbValue == savedSettings.windSpeedUnit }
                _selectedWindSpeedIndex.value = windIndex

                _selectedLanguageIndex.value = if (savedSettings.languageCode == "ar") 1 else 0

                _userLocation.value=savedSettings.location?:LocationModel(0.0,0.0,"")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class SettingsViewModelFactory(
        private val userSettingsRepo: UserSettingsRepo,
        private val locationHelper: LocationHelper
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(userSettingsRepo,locationHelper) as T
        }
    }
}