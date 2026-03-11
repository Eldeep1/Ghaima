package com.depogramming.ghaima.presentation.onboarding.mapselection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListNetworkResponse
import com.depogramming.ghaima.data.onBoarding.LocationModel
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.WeatherRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MapSelectionViewModel(
    val userSettingsRepository: UserSettingsRepo,
    val weatherRepository: WeatherRepositoryImpl,

    ) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    private val _searchResults = MutableStateFlow<List<CountriesListNetworkResponse>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .filter { query ->
                    query.length >= 3
                }
                .distinctUntilChanged()
                .collectLatest { validQuery ->
                    performSearch(validQuery)
                }
        }
    }


    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery

        if (newQuery.isBlank()) {
            _searchResults.value = emptyList()
        }
    }

    private suspend fun performSearch(query: String) {
        val result = weatherRepository.searchCity(query)

        if (result.isSuccess) {
            _searchResults.value = result.getOrNull() ?: emptyList()
        } else {

        }
    }

    fun onCitySelected(city: CountriesListNetworkResponse) {
        viewModelScope.launch {
            val currentSettings = userSettingsRepository.getUserData().firstOrNull()
                ?: UserSettingsModel(null, null, null)

            val newSettings = currentSettings.copy(
                location = LocationModel(city.lat, city.lon,  "${city.name}, ${city.state}, ${city.country}")
            )

            userSettingsRepository.setUserSettings(newSettings)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MapSelectionViewModelFactory(
    val userSettingsRepository: UserSettingsRepo,
    val weatherRepository: WeatherRepositoryImpl,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapSelectionViewModel(userSettingsRepository,weatherRepository) as T
    }
}