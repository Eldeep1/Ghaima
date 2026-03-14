package com.depogramming.ghaima.presentation.onboarding.views.unitscreen.view

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel
import com.depogramming.ghaima.presentation.onboarding.views.utils.NextButton
import com.depogramming.ghaima.presentation.onboarding.views.utils.SettingsSelectionCard

@Composable
fun UnitsScreenUI(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    onFinishButtonClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
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
            .padding(horizontal = 24.dp)
            .fillMaxSize(),
    ) {
        Spacer(Modifier.height(96.dp))
        PageTitle()
        Column(
            modifier = Modifier.weight(1f)
        ) {
            TempratureUnits(viewModel = viewModel)
            Spacer(Modifier.height(24.dp))
            WindSpeedUnits(viewModel = viewModel)
        }
        NextButton(
            text = stringResource(R.string.start_exploring_ghaima),
            onClick = {
                viewModel.finishOnboarding(onFinishButtonClick)
            })
        Spacer(Modifier.height(96.dp))

    }
}

@Composable
fun PageTitle() {
    Text(
        text = stringResource(R.string.choose_your_units),
        textAlign = TextAlign.Center,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.customize_how_you_view_weather_data),
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        color = Color.White.copy(alpha = .7f),
        modifier = Modifier.padding(horizontal = 32.dp)
    )
    Spacer(Modifier.height(42.dp))
}

@Composable
fun TempratureUnits(
    viewModel: OnboardingViewModel
) {
    val selectedTempIndex by viewModel.selectedUnitIndex.collectAsStateWithLifecycle()
    SettingsSelectionCard(
        titleResId = R.string.temperature,
        optionsResIds = viewModel.availableUnits.map { it.titleResId },
        selectedIndex = selectedTempIndex,
        onOptionSelected = { index -> viewModel.saveUnitSelection(index) }
    )
}

@Composable
fun WindSpeedUnits(
    viewModel: OnboardingViewModel
) {
    val selectedWindIndex by viewModel.selectedWindSpeedIndex.collectAsStateWithLifecycle()

    SettingsSelectionCard(
        titleResId = R.string.wind_speed_title,
        optionsResIds = viewModel.availableWindSpeeds.map { it.titleResId },
        selectedIndex = selectedWindIndex,
        onOptionSelected = { index -> viewModel.saveWindSpeedSelection(index) }
    )

}
