package com.depogramming.ghaima.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.WeatherRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val weatherRepository: WeatherRepositoryImpl,
    private val userSettingsRepo: UserSettingsRepo
) : ViewModel() {

    private val _homeWeatherState = MutableStateFlow<HomeWeatherStates>(HomeWeatherStates.Loading)

    val homeWeatherState: StateFlow<HomeWeatherStates> = _homeWeatherState.asStateFlow()

    private var cachedSettings: UserSettingsModel? = null

    init {

        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            userSettingsRepo.getUserData()
                .filterNotNull()
                .collectLatest { settings ->
                    cachedSettings = settings
                    fetchWeatherUsingSettings(settings)
                }
        }
    }

    private suspend fun fetchWeatherUsingSettings(settings: UserSettingsModel) {
        _homeWeatherState.value = HomeWeatherStates.Loading

        val lat = settings.location?.latitude ?: 0.0
        val lon = settings.location?.longitude ?: 0.0
        val lang = settings.languageCode ?: "en"
        val tempUnit = settings.units ?: "metric"
        val windUnit = settings.windSpeedUnit ?: "m_s"

        val result = weatherRepository.getWeatherForecast(lat, lon, lang, tempUnit, windUnit)

        result.onSuccess { weatherData ->
            val fixedLocationWeather=weatherData.copy(cityName = settings.location?.place ?: weatherData.cityName)
            _homeWeatherState.value = HomeWeatherStates.Success(fixedLocationWeather)
        }.onFailure { error ->
            _homeWeatherState.value = HomeWeatherStates.Error(error.message ?: "Unknown error")
        }
    }


    fun refreshWeather() {
        cachedSettings?.let { currentSettings ->
            viewModelScope.launch {
                fetchWeatherUsingSettings(currentSettings)
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class HomeScreenViewModelFactory(
    private val weatherRepository: WeatherRepositoryImpl,
    private val userSettingsRepo: UserSettingsRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeScreenViewModel(weatherRepository, userSettingsRepo) as T
    }
}