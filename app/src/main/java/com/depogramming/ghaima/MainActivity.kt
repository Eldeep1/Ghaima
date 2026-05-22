package com.depogramming.ghaima

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.depogramming.ghaima.presentation.alarms.view.AlarmsScreenUI
import com.depogramming.ghaima.presentation.alarms.viewmodel.AlarmsViewModel
import com.depogramming.ghaima.worker.BackgroundScheduler
import com.depogramming.ghaima.presentation.home.view.HomeScreenUI
import com.depogramming.ghaima.presentation.home.viewmodel.HomeScreenViewModel
import com.depogramming.ghaima.presentation.onboarding.views.utils.OnboardingScreens
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel
import com.depogramming.ghaima.presentation.onboarding.views.languageScreen.LanguageScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.locationscreen.LocationScreenUI
import com.depogramming.ghaima.presentation.mapselection.view.MapSelectionScreenUI
import com.depogramming.ghaima.presentation.mapselection.viewmodel.CurrentLocationMapSelection
import com.depogramming.ghaima.presentation.mapselection.viewmodel.FavouriteLocationMapSelection
import com.depogramming.ghaima.presentation.mapselection.viewmodel.MapSelectionViewModel
import com.depogramming.ghaima.presentation.onboarding.views.unitscreen.UnitsScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.welcomescreen.WelcomeScreenUI
import com.depogramming.ghaima.presentation.savedlocations.view.SavedLocationsUI
import com.depogramming.ghaima.presentation.savedlocations.viewmodel.SavedLocationsViewModel
import com.depogramming.ghaima.presentation.settings.view.SettingsUI
import com.depogramming.ghaima.presentation.settings.viewmodel.SettingsViewModel
import com.depogramming.ghaima.presentation.splash.view.SplashScreenUI
import com.depogramming.ghaima.presentation.splash.viewModel.SplashScreenViewModel
import com.depogramming.ghaima.presentation.utils.MyBottomNavigationBar
import com.depogramming.ghaima.ui.theme.GhaimaTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        BackgroundScheduler.schedulePeriodicChecks(this)
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.hierarchy?.any { destination ->
        destination.hasRoute(MainScreens.Home::class) || destination.hasRoute(MainScreens.Alarms::class) || destination.hasRoute(
            MainScreens.SavedLocations::class
        ) || destination.hasRoute(MainScreens.Settings::class)
    } == true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            ),
    ) {
        Scaffold(
            containerColor = Color.Transparent, bottomBar = {
                if (showBottomBar) {
                    MyBottomNavigationBar(navController, currentDestination)
                }
            }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = SplashScreen,
            ) {

                composable<SplashScreen> {
                    val viewModel = koinViewModel<SplashScreenViewModel>()
                    SplashScreenUI(
                        modifier = Modifier.padding(innerPadding),
                        onHomeScreen = {
                            navController.navigate(MainScreens.Home)
                        },
                        onOnBoardingScreen = {
                            navController.navigate(OnboardingScreens.WelcomeScreen)
                        },
                        viewModel = viewModel,
                    )
                }
                navigation<OnboardingGraph>(
                    startDestination = OnboardingScreens.WelcomeScreen
                ) {

                    composable<OnboardingScreens.WelcomeScreen> {
                        WelcomeScreenUI(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding), navController
                        )
                    }
                    composable<OnboardingScreens.LanguageScreen> {

                        val sharedViewModel: OnboardingViewModel =
                            koinViewModel<OnboardingViewModel>()
                        LanguageScreenUI(
                            Modifier.padding(innerPadding), onNextButtonClick = {
                                navController.navigate(OnboardingScreens.LocationScreen)
                            }, viewModel = sharedViewModel
                        )
                    }
                    composable<OnboardingScreens.LocationScreen> {


                        val sharedViewModel: OnboardingViewModel =
                            koinViewModel<OnboardingViewModel>()
                        LocationScreenUI(
                            Modifier.padding(innerPadding),
                            sharedViewModel,
                            onNextButtonClick = {
                                navController.navigate(OnboardingScreens.UnitsScreen)
                            },
                            onMapSelectionButtonClick = {
                                navController.navigate(
                                    MapSelectionScreen
                                )
                            },
                        )
                    }
                    composable<OnboardingScreens.UnitsScreen> {

                        val sharedViewModel: OnboardingViewModel =
                            koinViewModel<OnboardingViewModel>()
                        UnitsScreenUI(viewModel = sharedViewModel) {
                            navController.navigate(MainScreens.Home) {
                                popUpTo<OnboardingGraph> {
                                    inclusive = true
                                }

                            }
                        }
                    }
                }
                composable<MapSelectionScreen> { backStackEntry ->

                    val routeData = backStackEntry.toRoute<MapSelectionScreen>()
                    val isFavourites = routeData.isFavourite
                    val mapSelectionViewModel: MapSelectionViewModel = if (isFavourites) {
                        koinViewModel<FavouriteLocationMapSelection>()
                    } else {
                        koinViewModel<CurrentLocationMapSelection>()
                    }
                    MapSelectionScreenUI(
                        modifier = Modifier.padding(innerPadding),
                        mapSelectionViewModel,
                        onBackClick = {
                            navController.popBackStack()
                        })
                }

                navigation<MainScreensGraph>(
                    startDestination = MainScreens.Home
                ) {
                    composable<MainScreens.Home> {
                        val homeScreenViewModel: HomeScreenViewModel =
                            koinViewModel<HomeScreenViewModel>()
                        HomeScreenUI(modifier = Modifier.padding(innerPadding), homeScreenViewModel)
                    }
                    composable<MainScreens.SavedLocations> {
                        val savedLocationsViewModel: SavedLocationsViewModel =
                            koinViewModel<SavedLocationsViewModel>()
                        SavedLocationsUI(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = savedLocationsViewModel,
                            onMapClick = {
                                navController.navigate(MapSelectionScreen(isFavourite = true))
                            })
                    }
                    composable<MainScreens.Alarms> {
                        val alarmsViewModel: AlarmsViewModel = koinViewModel<AlarmsViewModel>()
                        AlarmsScreenUI(
                            modifier = Modifier.padding(innerPadding), viewModel = alarmsViewModel
                        )
                    }
                    composable<MainScreens.Settings> {
                        val settingsScreenViewModel: SettingsViewModel =
                            koinViewModel<SettingsViewModel>()
                        SettingsUI(
                            modifier = Modifier.padding(innerPadding),
                            settingsScreenViewModel,
                            onMapClick = {
                                navController.navigate(MapSelectionScreen(isFavourite = false))
                            })
                    }
                }
            }
        }
    }
}