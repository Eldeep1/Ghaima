package com.depogramming.ghaima.data.usersettings.datasource.local

import kotlinx.coroutines.flow.Flow

class UserSettingsLocalDataSource(private val userSettingsDao: UserSettingsDao) {
    fun getUserData(): Flow<UserSettingsEntity?> {
        return userSettingsDao.getUserSettings()
    }
    suspend fun setUserData(userSettingsEntity: UserSettingsEntity){
        userSettingsDao.insertUserSettings(userSettingsEntity)
    }
}