package com.depogramming.ghaima.presentation.mapselection.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.weather.datasource.remote.dto.CountriesListDTO
import com.depogramming.ghaima.data.weather.model.LocationModel
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.weather.WeatherRepository
import com.depogramming.ghaima.presentation.utils.location.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


abstract class MapSelectionViewModel(
    val userSettingsRepository: UserSettingsRepo,
    val weatherRepository: WeatherRepository,
    val locationHelper: LocationHelper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    private val _searchResults = MutableStateFlow<List<CountriesListDTO>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    val _selectedLocation =
        MutableStateFlow(LocationModel(30.0444, 31.2357, "Cairo, Egypt"))
    val selectedLocation = _selectedLocation.asStateFlow()

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
            //should tell the user to check his connection and try again...
        }
    }

    fun onCitySelected(city: CountriesListDTO) {
        _selectedLocation.value = _selectedLocation.value.copy(
            latitude = city.lat,
            longitude = city.lon,
            place = "${city.name}, ${city.state}, ${city.country}"
        )
    }

    abstract suspend fun onSubmitButtonClick()


    fun updateLocationFromMap(lat: Double, lng: Double) {
        viewModelScope.launch {

            val addressName = locationHelper.getAddressFromLocation(lat, lng)

            _selectedLocation.value = _selectedLocation.value.copy(
                latitude = lat,
                longitude = lng,
                place = addressName
            )
        }
    }
}
