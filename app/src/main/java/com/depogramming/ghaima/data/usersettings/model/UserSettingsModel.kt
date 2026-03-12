package com.depogramming.ghaima.data.usersettings.model

import com.depogramming.ghaima.data.weather.model.LocationModel

data class UserSettingsModel (var languageCode:String?,var units:String?,var location: LocationModel?,val windSpeedUnit: String?)