package com.depogramming.ghaima.presentation.settings.view


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.onboarding.views.utils.SettingsSelectionCard
import com.depogramming.ghaima.presentation.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsUI(modifier: Modifier=Modifier,viewModel: SettingsViewModel){
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize().padding(horizontal = 24.dp).verticalScroll(scrollState)

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

    //        SelectLocationButton()
        Spacer(Modifier.height(16.dp))


    }
}
@Composable
fun CustomAppBar(modifier: Modifier=Modifier){
    Text(
        text= stringResource(R.string.settings),
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier=modifier,
        textAlign = TextAlign.Center
    )
}
@Composable
fun LanguageDropDown(viewModel: SettingsViewModel){

    val selectedLanguage by viewModel.selectedLanguageIndex.collectAsStateWithLifecycle()
    SettingsSelectionCard(
        titleResId = R.string.language,
        optionsResIds = viewModel.languagesList.map { it.titleResId  },
        selectedIndex = selectedLanguage,
        onOptionSelected = { index -> viewModel.saveLanguageSelection(index) }
    )
}

@Composable
fun TemperatureDropDown(viewModel: SettingsViewModel){

    val selectedTemperature by viewModel.selectedTempUnitIndex.collectAsStateWithLifecycle()
    SettingsSelectionCard(
        titleResId = R.string.temperature,
        optionsResIds = viewModel.availableTempUnits.map { it.titleResId  },
        selectedIndex = selectedTemperature,
        onOptionSelected = { index -> viewModel.saveTempSelection(index) }
    )
}
@Composable
fun WindSpeedDropDown(viewModel: SettingsViewModel){

    val selectedWindSpeed by viewModel.selectedWindSpeedIndex.collectAsStateWithLifecycle()
    SettingsSelectionCard(
        titleResId = R.string.wind_speed_title,
        optionsResIds = viewModel.availableWindSpeeds.map { it.titleResId  },
        selectedIndex = selectedWindSpeed,
        onOptionSelected = { index -> viewModel.saveWindSpeedSelection(index) }
    )
}