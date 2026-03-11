package com.depogramming.ghaima.presentation.onboarding.mapselection.viewmodel

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListNetworkResponse
import com.depogramming.ghaima.data.onBoarding.LocationModel
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.WeatherRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class MapSelectionViewModel(
    val userSettingsRepository: UserSettingsRepo,
    val weatherRepository: WeatherRepositoryImpl,
    val application: Application
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    private val _searchResults = MutableStateFlow<List<CountriesListNetworkResponse>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    private val _selectedLocation =
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

        }
    }

    fun onCitySelected(city: CountriesListNetworkResponse) {
        _selectedLocation.value = _selectedLocation.value.copy(
            latitude = city.lat,
            longitude = city.lon,
            place = "${city.name}, ${city.state}, ${city.country}"
        )
    }

    fun onSubmitButtonClick() {
        viewModelScope.launch {
            val currentSettings = userSettingsRepository.getUserData().firstOrNull()
                ?: UserSettingsModel(null, null, null)

            val newSettings = currentSettings.copy(
                location = LocationModel(
                    _selectedLocation.value.latitude,
                    _selectedLocation.value.longitude,
                    _selectedLocation.value.place
                )
            )

            userSettingsRepository.setUserSettings(newSettings)
        }
    }

    fun updateLocationFromMap(lat: Double, lng: Double) {
        getAddressFromLocation(lat, lng)
    }

    fun getAddressFromLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            val geocoder = Geocoder(application, Locale.getDefault())

            val addressName = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(lat, lon, 1) { addresses ->
                            continuation.resume(formatAddress(addresses, lat, lon))
                        }
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(lat, lon, 1)
                        formatAddress(addresses, lat, lon)
                    }
                }
            } catch (e: Exception) {
                "$lat, $lon"
            }

            _selectedLocation.value = _selectedLocation.value.copy(
                latitude = lat,
                longitude = lon,
                place = addressName
            )

        }
    }

    private fun formatAddress(addresses: List<Address>?, lat: Double, lon: Double): String {
        if (addresses.isNullOrEmpty()) return "$lat, $lon"

        val address = addresses[0]

        val validParts = listOfNotNull(
            address.locality,
            address.subAdminArea,
            address.adminArea,
            address.countryName
        ).distinct()

        return if (validParts.isNotEmpty()) {
            validParts.joinToString(", ")
        } else {
            "$lat, $lon"
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MapSelectionViewModelFactory(
    val userSettingsRepository: UserSettingsRepo,
    val weatherRepository: WeatherRepositoryImpl,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapSelectionViewModel(userSettingsRepository, weatherRepository, application) as T
    }
}