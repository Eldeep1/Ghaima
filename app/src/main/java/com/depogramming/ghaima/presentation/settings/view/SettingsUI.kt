package com.depogramming.ghaima.presentation.settings.view


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.onboarding.views.utils.SettingsSelectionCard
import com.depogramming.ghaima.presentation.settings.viewmodel.SettingsViewModel
import com.depogramming.ghaima.presentation.utils.LocationBottomSheetContent
import com.depogramming.ghaima.presentation.utils.location.LocationPermissionHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsUI(modifier: Modifier = Modifier, viewModel: SettingsViewModel,onMapClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    var showLocationSheet by remember { mutableStateOf(false) }

    LocationPermissionHandler(
        askForPermissionFlow = viewModel.askForPermission,
        openSettingsFlow = viewModel.openLocationSettingsEvent,
        onPermissionResult = { fine, coarse -> viewModel.onLocationPermissionResult(fine, coarse) }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)

    ) {
        Spacer(Modifier.height(48.dp))
        CustomAppBar(Modifier.fillMaxWidth())
        Spacer(Modifier.height(56.dp))

        LanguageDropDown(viewModel)
        Spacer(Modifier.height(8.dp))

        TemperatureDropDown(viewModel)
        Spacer(Modifier.height(8.dp))

        WindSpeedDropDown(viewModel)
        Spacer(Modifier.height(8.dp))

        SelectLocationButton(viewModel){
            showLocationSheet=true
        }
        Spacer(Modifier.height(16.dp))
        if (showLocationSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLocationSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xff223B63)
            ) {

                LocationBottomSheetContent(
                    onCurrentLocationClick = {
                        showLocationSheet = false
                        viewModel.fetchCurrentLocation()
                    },
                    onMapClick = {
                        showLocationSheet = false
                        onMapClick()
                    }
                )
            }
        }

    }
}

@Composable
fun CustomAppBar(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.settings),
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
fun LanguageDropDown(viewModel: SettingsViewModel) {

    val selectedLanguage by viewModel.selectedLanguageIndex.collectAsStateWithLifecycle()
    SettingsSelectionCard(
        titleResId = R.string.language,
        optionsResIds = viewModel.languagesList.map { it.titleResId },
        selectedIndex = selectedLanguage,
        onOptionSelected = { index -> viewModel.saveLanguageSelection(index) }
    )
}

@Composable
fun TemperatureDropDown(viewModel: SettingsViewModel) {

    val selectedTemperature by viewModel.selectedTempUnitIndex.collectAsStateWithLifecycle()
    SettingsSelectionCard(
        titleResId = R.string.temperature,
        optionsResIds = viewModel.availableTempUnits.map { it.titleResId },
        selectedIndex = selectedTemperature,
        onOptionSelected = { index -> viewModel.saveTempSelection(index) }
    )
}

@Composable
fun WindSpeedDropDown(viewModel: SettingsViewModel) {

    val selectedWindSpeed by viewModel.selectedWindSpeedIndex.collectAsStateWithLifecycle()
    SettingsSelectionCard(
        titleResId = R.string.wind_speed_title,
        optionsResIds = viewModel.availableWindSpeeds.map { it.titleResId },
        selectedIndex = selectedWindSpeed,
        onOptionSelected = { index -> viewModel.saveWindSpeedSelection(index) }
    )
}

@Composable
fun SelectLocationButton(viewModel: SettingsViewModel,modifier: Modifier= Modifier,onClicked:()->Unit) {
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color  =  Color.White.copy(alpha = .2f),
        onClick = {
            onClicked()
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 32.dp).height(56.dp),
            verticalAlignment = Alignment.CenterVertically
            ) {

            Text(
                text= stringResource(R.string.location),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text=userLocation.place,
                modifier=Modifier.width(100.dp),
                color=Color.White.copy(alpha=.4f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.location_change_ic),
                contentDescription = null,
                modifier=Modifier.size(16.dp),
                tint = Color.White
            )
        }
    }
}
