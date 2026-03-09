package com.depogramming.ghaima.data.usersettings

import com.depogramming.ghaima.data.onBoarding.LanguageModel

interface UserSettingsRepo {
    suspend fun getLanguages(): List<LanguageModel>
}