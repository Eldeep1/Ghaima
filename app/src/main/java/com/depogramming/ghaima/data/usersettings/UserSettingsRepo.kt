package com.depogramming.ghaima.data.usersettings

import com.depogramming.ghaima.data.weather.model.LanguageModel
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepo {
    suspend fun getLanguages(): List<LanguageModel>

    fun getUserData(): Flow<UserSettingsModel?>

    suspend fun setUserSettings(userSettingsModel: UserSettingsModel)

}