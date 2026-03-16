package com.depogramming.ghaima.presentation.mapselection.viewmodel


import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.WeatherRepository
import com.depogramming.ghaima.presentation.utils.location.LocationHelper
import kotlinx.coroutines.flow.firstOrNull

class FavouriteLocationMapSelection(
    userSettingsRepository: UserSettingsRepo,
    weatherRepository: WeatherRepository,
    locationHelper: LocationHelper
) : MapSelectionViewModel(userSettingsRepository, weatherRepository, locationHelper) {
    override suspend fun onSubmitButtonClick() {

        val currentSettings = userSettingsRepository.getUserData().firstOrNull()
            ?: UserSettingsModel(null, null, null, null)

        weatherRepository.addToFavourites(
            _selectedLocation.value.latitude,
            _selectedLocation.value.longitude,
            currentSettings.languageCode ?: "en",
            place = locationHelper.getAddressFromLocation(
                _selectedLocation.value.latitude,
                _selectedLocation.value.longitude
            )
        )
    }
}
