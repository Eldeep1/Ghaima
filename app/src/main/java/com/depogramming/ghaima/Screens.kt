package com.depogramming.ghaima

import kotlinx.serialization.Serializable

@Serializable
object SplashScreen
@Serializable
data class MapSelectionScreen(val isFavourite:Boolean=false)
// 2. The route that represents the ENTIRE Onboarding Graph
@Serializable
object OnboardingGraph
@Serializable
object MainScreensGraph
sealed class MainScreens {
    @Serializable object Home : MainScreens()
    @Serializable object SavedLocations : MainScreens()
    @Serializable object Alarms : MainScreens()
    @Serializable object Settings : MainScreens()
}
