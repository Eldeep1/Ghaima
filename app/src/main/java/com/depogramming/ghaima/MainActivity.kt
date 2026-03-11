package com.depogramming.ghaima

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.depogramming.ghaima.data.db.AppDatabase
import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListRemoteDataSource
import com.depogramming.ghaima.data.network.Network
import com.depogramming.ghaima.data.usersettings.UserSettingsRepoImp
import com.depogramming.ghaima.data.usersettings.datasource.local.UserSettingsLocalDataSource
import com.depogramming.ghaima.data.weather.WeatherRepositoryImpl
import com.depogramming.ghaima.presentation.home.HomeScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.utils.OnboardingScreens
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModelFactory
import com.depogramming.ghaima.presentation.onboarding.views.languageScreen.LanguageScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.locationscreen.view.LocationScreenUI
import com.depogramming.ghaima.presentation.onboarding.mapselection.view.MapSelectionScreenUI
import com.depogramming.ghaima.presentation.onboarding.mapselection.viewmodel.MapSelectionViewModel
import com.depogramming.ghaima.presentation.onboarding.mapselection.viewmodel.MapSelectionViewModelFactory
import com.depogramming.ghaima.presentation.onboarding.views.unitscreen.view.UnitsScreenUI
import com.depogramming.ghaima.presentation.onboarding.views.welcomescreen.WelcomeScreenUI
import com.depogramming.ghaima.presentation.splash.view.SplashScreenUI
import com.depogramming.ghaima.presentation.splash.viewModel.SplashScreenViewModel
import com.depogramming.ghaima.presentation.splash.viewModel.SplashScreenViewModelFactory
import com.depogramming.ghaima.ui.theme.GhaimaTheme

class MainActivity : AppCompatActivity() {
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
    val settingsDao = AppDatabase.getInstance(LocalContext.current).userSettingsDao()
    val settingsDataSource = UserSettingsLocalDataSource(settingsDao)
    val userSettingsRepo = UserSettingsRepoImp(settingsDataSource)

    val countriesListService= Network.countriesListService
    val countriesListRemoteDataSource= CountriesListRemoteDataSource(countriesListService)
    val weatherRepository= WeatherRepositoryImpl(countriesListRemoteDataSource)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.hierarchy?.any { destination ->
        destination.hasRoute(MainScreens.Home::class) ||
                destination.hasRoute(MainScreens.Alarms::class) ||
                destination.hasRoute(MainScreens.SavedLocations::class) ||
                destination.hasRoute(MainScreens.Settings::class)
    } == true

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                // will be built later
//                MyBottomNavigationBar(navController, currentDestination)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SplashScreen,
        ) {

            composable<SplashScreen> {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry<SplashScreen>()
                }
                val viewModel: SplashScreenViewModel=viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = SplashScreenViewModelFactory(userSettingsRepo)
                    )
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
                            .padding(innerPadding),
                        navController
                    )
                }
                composable<OnboardingScreens.LanguageScreen> {

                    val parentEntry = remember(it) {
                        navController.getBackStackEntry<OnboardingGraph>()
                    }

                    val sharedViewModel: OnboardingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = OnboardingViewModelFactory(
                            userSettingsRepo,
                            LocalActivity.current?.application ?: Application()
                        )
                    )
                    LanguageScreenUI(
                        Modifier.padding(innerPadding),
                        navController,
                        viewModel = sharedViewModel
                    )
                }
                composable<OnboardingScreens.LocationScreen> {
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry<OnboardingGraph>()
                    }

                    val sharedViewModel: OnboardingViewModel = viewModel(
                        viewModelStoreOwner = parentEntry,
                        factory = OnboardingViewModelFactory(
                            userSettingsRepo,
                            LocalActivity.current?.application ?: Application()
                        )
                    )
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
                    UnitsScreenUI()
                }
            }
            composable<MapSelectionScreen> {
                val parentEntry = remember(it) {
                    navController.getBackStackEntry<MapSelectionScreen>()
                }
                //create the view model
                val mapSelectionViewModel: MapSelectionViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = MapSelectionViewModelFactory(
                        userSettingsRepo,
                        weatherRepository,
                        LocalActivity.current?.application ?: Application()
                    )
                )
                MapSelectionScreenUI(modifier = Modifier.padding(innerPadding),mapSelectionViewModel, onBackClick = {
                    navController.popBackStack()
                })
            }

            navigation<MainScreensGraph>(
                startDestination = MainScreens.Home
            ) {
                composable<MainScreens.Home> {
                    // Pass the innerPadding here so the BottomBar doesn't cover your Weather data!
                    HomeScreenUI(modifier = Modifier.padding(innerPadding))
                }
            }


        }
    }
}