package com.depogramming.ghaima.presentation.splash.viewModel

sealed interface SplashState {
    object Loading : SplashState
    object GoToOnboarding : SplashState
    object GoToHome : SplashState
}