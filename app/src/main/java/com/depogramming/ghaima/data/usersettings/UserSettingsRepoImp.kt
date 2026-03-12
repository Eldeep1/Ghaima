package com.depogramming.ghaima.data.usersettings

import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.weather.model.LanguageModel
import com.depogramming.ghaima.data.usersettings.datasource.local.UserSettingsLocalDataSource
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.utils.toEntity
import com.depogramming.ghaima.data.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSettingsRepoImp (private val userSettingsLocalDataSource: UserSettingsLocalDataSource):UserSettingsRepo{

    override suspend fun getLanguages(): List<LanguageModel> {

        val languages = listOf(
            LanguageModel("English", "United Kingdom", R.drawable.ukflag,"en"),
            LanguageModel("العربية", "مصر", R.drawable.egflag,"ar")
        )

        return languages
    }

    override fun getUserData(): Flow<UserSettingsModel?> {
        return userSettingsLocalDataSource.getUserData().map { entity ->
            entity?.toModel()
        }
    }

    override suspend fun setUserSettings(userSettingsModel: UserSettingsModel){
        userSettingsLocalDataSource.setUserData(userSettingsModel.toEntity())
    }

}