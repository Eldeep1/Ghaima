package com.depogramming.ghaima.presentation.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.depogramming.ghaima.MainScreens
import com.depogramming.ghaima.R
data class BottomNavItem(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val route: Any
)
@Composable
fun MyBottomNavigationBar(
    navController: NavController,
    currentDestination: NavDestination?
) {

    val items = listOf(
        BottomNavItem(R.string.nav_home, R.drawable.home_ic, MainScreens.Home),
        BottomNavItem(R.string.nav_saved, R.drawable.saved_ic, MainScreens.SavedLocations),
        BottomNavItem(R.string.nav_alarms, R.drawable.alarms_ic, MainScreens.Alarms),
        BottomNavItem(R.string.nav_settings, R.drawable.settings_ic, MainScreens.Settings)
    )

    NavigationBar(
        containerColor = Color.White.copy(alpha=.2f),
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->

            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.route::class)
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = stringResource(id = item.labelRes),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.labelRes).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    // Fades out the unselected items exactly like your image
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    // MAGIC LINE: This removes the ugly standard Material 3 pill shape!
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}