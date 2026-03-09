package com.depogramming.ghaima.data.usersettings

import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.onBoarding.LanguageModel

class UserSettingsRepoImp :UserSettingsRepo{
    override suspend fun getLanguages(): List<LanguageModel> {

        val languages = listOf(
            LanguageModel("English", "United Kingdom", R.drawable.ukflag),
            LanguageModel("Arabic", "Egypt", R.drawable.egflag)
        )

        return languages
    }

}