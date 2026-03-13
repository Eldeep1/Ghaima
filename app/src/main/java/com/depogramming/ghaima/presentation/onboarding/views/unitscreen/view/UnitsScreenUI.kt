package com.depogramming.ghaima.presentation.onboarding.views.unitscreen.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel
import com.depogramming.ghaima.presentation.onboarding.views.utils.NextButton

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
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel
) {
    val selectedIndex by viewModel.selectedUnitIndex.collectAsStateWithLifecycle()

    val availableUnits = viewModel.availableUnits

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.temperature),
                color = Color.White.copy(alpha = .6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))


            availableUnits.forEachIndexed { index, unit ->

                val isSelected = index == selectedIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable {

                            viewModel.saveUnitSelection(index)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = unit.titleResId),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (isSelected) {
                        Image(
                            painter = painterResource(R.drawable.selected_row),
                            contentDescription = "Selected"
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.unselected_row),
                            contentDescription = "Selected"
                        )
                    }
                }

                if (index < availableUnits.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun WindSpeedUnits(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel
) {
    val selectedIndex by viewModel.selectedWindSpeedIndex.collectAsStateWithLifecycle()

    val availableUnits = viewModel.availableWindSpeeds

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.wind_speed_title),
                color = Color.White.copy(alpha = .6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))


            availableUnits.forEachIndexed { index, unit ->

                val isSelected = index == selectedIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable {

                            viewModel.saveWindSpeedSelection(index)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = unit.titleResId),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (isSelected) {
                        Image(
                            painter = painterResource(R.drawable.selected_row),
                            contentDescription = "Selected"
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.unselected_row),
                            contentDescription = "Selected"
                        )
                    }
                }

                if (index < availableUnits.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
