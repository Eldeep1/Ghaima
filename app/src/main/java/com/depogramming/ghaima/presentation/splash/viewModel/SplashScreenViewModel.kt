package com.depogramming.ghaima.presentation.splash.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    val userSettingsRepo: UserSettingsRepo
) : ViewModel() {


    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState = _splashState.asStateFlow()

    init {
        checkUserData()
    }

    private fun checkUserData() {
        viewModelScope.launch {
            userSettingsRepo.getUserData().collect { settings ->

                if (settings == null || settings.languageCode == null||settings.units==null) {
                    _splashState.value = SplashState.GoToOnboarding
                } else {
                    _splashState.value = SplashState.GoToHome
                }
            }
        }
    }
}