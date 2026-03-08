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
import androidx.navigation.compose.rememberNavController
import com.depogramming.ghaima.onboarding.welcomescreen.WelcomeScreen
import com.depogramming.ghaima.splash.SplashScreen
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
            startDestination = Screens.WelcomeScreen,
        ) {

            composable<Screens.SplashScreen> {
                SplashScreen() {screen->
                    //the onboarding or the home
                    navController.navigate(screen)
                }
            }
            composable<Screens.WelcomeScreen> {
                WelcomeScreen(Modifier.padding(innerPadding))
            }

        }
    }
}