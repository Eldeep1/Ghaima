package com.depogramming.ghaima.presentation.utils.location

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun LocationPermissionHandler(
    askForPermissionFlow: SharedFlow<Unit>,
    openSettingsFlow: SharedFlow<Unit>,
    onPermissionResult: (fineGranted: Boolean, coarseGranted: Boolean) -> Unit
) {
    val context = LocalContext.current


    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val fineLocationGranted = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        onPermissionResult(fineLocationGranted, coarseLocationGranted)
    }

    LaunchedEffect(Unit) {
        askForPermissionFlow.collect {
            locationPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        openSettingsFlow.collect {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }
}