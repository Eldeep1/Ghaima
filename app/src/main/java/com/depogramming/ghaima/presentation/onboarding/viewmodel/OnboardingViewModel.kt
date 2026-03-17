package com.depogramming.ghaima.presentation.onboarding.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.weather.model.LanguageModel
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit
import com.depogramming.ghaima.presentation.utils.location.LocationHelper
import com.depogramming.ghaima.presentation.utils.location.LocationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    val userSettingsRepo: UserSettingsRepo,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _languages = MutableStateFlow<List<LanguageModel>>(listOf())
    val language: StateFlow<List<LanguageModel>> = _languages.asStateFlow()

    private val _askForPermission = MutableSharedFlow<Unit>()
    val askForPermission = _askForPermission.asSharedFlow()
    var place: StateFlow<String> = userSettingsRepo.getUserData()
        .map { settings ->
            settings?.location?.place ?: ""
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _selectedUnitIndex = MutableStateFlow(0)
    val selectedUnitIndex = _selectedUnitIndex.asStateFlow()

    val availableUnits = TemperatureUnit.entries

    private val _openLocationSettingsEvent = MutableSharedFlow<Unit>()
    val openLocationSettingsEvent = _openLocationSettingsEvent.asSharedFlow()
    fun saveUnitSelection(selectedIndex: Int) {
        _selectedUnitIndex.value = selectedIndex
    }

    private val _selectedWindSpeedIndex = MutableStateFlow(0)
    val selectedWindSpeedIndex = _selectedWindSpeedIndex.asStateFlow()
    val availableWindSpeeds = WindSpeedUnit.entries
    fun saveWindSpeedSelection(selectedIndex: Int) {
        _selectedWindSpeedIndex.value = selectedIndex
    }

    fun finishOnboarding(onComplete: () -> Unit) {
        val unitSelectedIndex = _selectedUnitIndex.value
        val tempApiValueToSave = availableUnits[unitSelectedIndex].apiValue

        val windSelectedIndex = _selectedWindSpeedIndex.value
        val windDbValueToSave = availableWindSpeeds[windSelectedIndex].dbValue
        viewModelScope.launch {
            updateSettings {
                it.copy(units = tempApiValueToSave,windSpeedUnit=windDbValueToSave)
            }
            onComplete()
        }

    }


    private val _selectedLanguage = MutableStateFlow(LanguageModel("","",0,"en"))
    val selectedLanguage = _selectedLanguage.asStateFlow()

    init {
        getLanguages()
    }

    fun getLanguages() {
        viewModelScope.launch {
            _languages.value = userSettingsRepo.getLanguages()
        }
    }

    fun finishLanguageSelection(onNextClick:()->Unit){
        viewModelScope.launch {

            updateSettings { it.copy(languageCode = _selectedLanguage.value.languageCode) }
            onNextClick()
        }
    }
    fun selectLanguage(languageModel: LanguageModel) {
        _selectedLanguage.value = languageModel
        val languageCode = languageModel.languageCode

        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)

        viewModelScope.launch {
            updateSettings { it.copy(languageCode = languageModel.languageCode) }
        }

    }

    private suspend fun updateSettings(update: (UserSettingsModel) -> UserSettingsModel) {
        val currentSettings = userSettingsRepo.getUserData().firstOrNull()
            ?: UserSettingsModel(null, null, null, null)

        userSettingsRepo.setUserSettings(update(currentSettings))
    }
    fun onLocationPermissionResult(fineLocationGranted: Boolean, coarseLocationGranted: Boolean) {
        if (fineLocationGranted || coarseLocationGranted) {
            fetchLocation()
        } else {
            //consider showing snack bar that says that we have failed getting the location
        }
    }

    fun fetchLocation() {
        viewModelScope.launch {
            when (val result = locationHelper.getCurrentLocation()) {

                is LocationResult.Success -> {
                    updateSettings { it.copy(location = result.location) }
                }
                is LocationResult.MissingPermission -> {
                    _askForPermission.emit(Unit)
                }
                is LocationResult.GpsDisabled -> {
                    _openLocationSettingsEvent.emit(Unit)
                }
                is LocationResult.Error -> {
                   //emit an error...
                }
            }
        }
    }
}