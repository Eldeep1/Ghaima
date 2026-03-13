package com.depogramming.ghaima.presentation.onboarding.views.locationscreen.view

import android.Manifest
import android.provider.Settings
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depogramming.ghaima.presentation.onboarding.views.utils.ExpandingPageIndicator
import com.depogramming.ghaima.presentation.onboarding.views.utils.NextButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel


@Composable
fun LocationScreenUI(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    onNextButtonClick:()->Unit,
    onMapSelectionButtonClick:()->Unit,
    ) {


    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val fineLocationGranted = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        viewModel.onLocationPermissionResult(fineLocationGranted, coarseLocationGranted)
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.askForPermission.collect {
            locationPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.openLocationSettingsEvent.collect {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }

    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier=Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(96.dp))
            Text(
                text = stringResource(R.string.set_your_location),
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.get_accurate_local_weather_forecasts_and_real_time_updates),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = .7f)
            )
            Spacer(Modifier.height(32.dp))

            NextButton(
                text = stringResource(R.string.use_my_current_location)
            ) {
                viewModel.fetchLocation()
            }
            Spacer(Modifier.height(24.dp))

            LocationSelectionCard {
                onMapSelectionButtonClick()
            }

            SelectedLocationColumn(viewModel)
        }


        ExpandingPageIndicator(
            3,
            1
        )
        Spacer(Modifier.height(32.dp))
        NextButton(onClick={

            //TODO: do some shits on the view model then decide to navigate or not
            onNextButtonClick()
        })
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
fun SelectedLocationColumn(viewModel: OnboardingViewModel){
    val selectedCountry=viewModel.place.collectAsStateWithLifecycle()
    if(!selectedCountry.value.isEmpty()){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.selected_location),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = .6f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                textAlign = TextAlign.Center,
                text = selectedCountry.value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

}

@Composable
fun LocationSelectionCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(196.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {

        Image(
            painter = painterResource(id = R.drawable.mapimage),
            contentDescription = "Map Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6B8A7A).copy(alpha = 0.6f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = "Map Icon",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = stringResource(R.string.select_location_on_map),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}