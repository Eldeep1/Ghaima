package com.depogramming.ghaima.presentation.onboarding
import kotlinx.serialization.Serializable
sealed class OnboardingScreens {
    @Serializable
    object WelcomeScreen : OnboardingScreens()
    @Serializable
    object LanguageScreen : OnboardingScreens()
    @Serializable
    object LocationScreen : OnboardingScreens()
    @Serializable
    object UnitsScreen : OnboardingScreens()
}