package com.depogramming.ghaima

import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable
    object SplashScreen : Screens()

    @Serializable
    object OnboardingScreen: Screens()
}