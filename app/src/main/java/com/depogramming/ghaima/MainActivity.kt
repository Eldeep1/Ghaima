package com.depogramming.ghaima

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.depogramming.ghaima.onboarding.OnboardingScreens
import com.depogramming.ghaima.onboarding.languagescreen.view.LanguageScreenUI
import com.depogramming.ghaima.onboarding.locationscreen.view.LocationScreenUI
import com.depogramming.ghaima.onboarding.unitscreen.view.UnitsScreenUI
import com.depogramming.ghaima.onboarding.welcomescreen.WelcomeScreenUI
import com.depogramming.ghaima.splash.SplashScreenUI
import com.depogramming.ghaima.ui.theme.GhaimaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            GhaimaTheme {
                GhaimaApp(navController = navController)
            }
        }
    }
}

@Composable
fun GhaimaApp(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SplashScreen,
        ) {

            composable<SplashScreen> {
                SplashScreenUI() { screen ->
                    navController.navigate(screen){
                        popUpTo<SplashScreen> {
                            inclusive = true
                        }
                    }
                }
            }
            navigation<OnboardingGraph>(
                startDestination = OnboardingScreens.WelcomeScreen
            ) {
                composable<OnboardingScreens.WelcomeScreen> {
                    WelcomeScreenUI(
                        modifier = Modifier.padding(innerPadding),
                        onButtonClick = {
                            screen->navController.navigate(screen){
                            popUpTo<OnboardingScreens.WelcomeScreen> {
                                inclusive = true
                            }
                        }
                        }
                    )
                }
                composable<OnboardingScreens.LanguageScreen> {
                    LanguageScreenUI(Modifier.padding(innerPadding))
                }
                composable<OnboardingScreens.LocationScreen> {
                    LocationScreenUI()
                }
                composable<OnboardingScreens.UnitsScreen> {
                    UnitsScreenUI()
                }
            }

        }
    }
}