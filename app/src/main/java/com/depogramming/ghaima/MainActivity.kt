package com.depogramming.ghaima

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.depogramming.ghaima.data.usersettings.UserSettingsRepoImp
import com.depogramming.ghaima.presentation.onboarding.views.utils.OnboardingScreens
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModelFactory
import com.depogramming.ghaima.presentation.onboarding.views.languageScreen.LanguageScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.locationscreen.view.LocationScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.unitscreen.view.UnitsScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.welcomescreen.WelcomeScreenUI
import com.depogramming.ghaima.presentation.splash.SplashScreenUI
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
                SplashScreenUI { screen ->
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

                val userSettingsRepo= UserSettingsRepoImp()

                composable<OnboardingScreens.WelcomeScreen> {
                    WelcomeScreenUI(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        navController
                    )
                }
                composable<OnboardingScreens.LanguageScreen> {

                    val parentEntry = remember(it) {
                        navController.getBackStackEntry<OnboardingGraph>()
                    }

                    val sharedViewModel: OnboardingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = OnboardingViewModelFactory(userSettingsRepo,
                            LocalActivity.current?.application ?: Application()
                        )
                    )
                    LanguageScreenUI(Modifier.padding(innerPadding),
                        navController,
                        viewModel =sharedViewModel
                    )
                }
                composable<OnboardingScreens.LocationScreen> {
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry<OnboardingGraph>()
                    }

                    val sharedViewModel: OnboardingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = OnboardingViewModelFactory(userSettingsRepo,
                            LocalActivity.current?.application ?: Application()
                        )
                    )
                    LocationScreenUI(Modifier.padding(innerPadding),sharedViewModel)
                }
                composable<OnboardingScreens.UnitsScreen> {
                    UnitsScreenUI()
                }
            }

        }
    }
}