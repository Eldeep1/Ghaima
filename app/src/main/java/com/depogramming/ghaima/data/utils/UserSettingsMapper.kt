package com.depogramming.ghaima.data.utils

import com.depogramming.ghaima.data.usersettings.datasource.local.UserSettingsEntity
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel

fun UserSettingsEntity.toModel(): UserSettingsModel {
    return UserSettingsModel(
        languageCode = this.languageCode,
        units = this.units,
        windSpeedUnit = this.windSpeedUnit,
        location = this.location
    )
}

fun UserSettingsModel.toEntity(): UserSettingsEntity {
    return UserSettingsEntity(
        languageCode = this.languageCode,
        units = this.units,
        location = this.location,
        windSpeedUnit = this.windSpeedUnit
    )
}