package com.depogramming.ghaima.presentation.alarms.view.composables

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.alarms.viewmodel.AlarmFormState
import java.util.Locale

enum class TimeSelection { START, END }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertFormContent(
    state: AlarmFormState,
    onModeChanged: (Boolean) -> Unit,
    onDescChanged: (String) -> Unit,
    onTempChanged: (Double, Double) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onStartTimeChanged: (Int, Int) -> Unit,
    onEndTimeChanged: (Int, Int) -> Unit,
) {
    var activeTimeSelection by remember { mutableStateOf<TimeSelection?>(null) }

    val context = LocalContext.current
    val timePickerState = rememberTimePickerState(
        initialHour = if (activeTimeSelection == TimeSelection.START) state.startHour else state.endHour,
        initialMinute = if (activeTimeSelection == TimeSelection.START) state.startMinute else state.endMinute,
        is24Hour = false
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xff223B63),
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.new_alert),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF395481)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (state.isNotificationMode) Color.White.copy(alpha = .15f) else Color.Transparent)
                    .clickable { onModeChanged(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.notification), color = Color.White)
            }


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (!state.isNotificationMode) Color.White.copy(alpha = .15f) else Color.Transparent)
                    .clickable { onModeChanged(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.alarm), color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TimeSelectionBox(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.from),
                timeString = formatTime(state.startHour, state.startMinute, context),
                onClick = { activeTimeSelection = TimeSelection.START }
            )


            if (state.isNotificationMode) {
                TimeSelectionBox(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.to),
                    timeString = formatTime(state.endHour, state.endMinute, context),
                    onClick = { activeTimeSelection = TimeSelection.END }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        //description tab
        Text(
            text = stringResource(R.string.description),
            fontSize = 12.sp,
            color = Color.White.copy(alpha = .4f),
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = onDescChanged,
            placeholder = {
                Text(
                    stringResource(R.string.e_g_ice_warning_on_commute),
                    color = Color.White.copy(alpha=.8f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFF395481),
                unfocusedContainerColor = Color(0xFF395481),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.temp_range),
            fontSize = 12.sp,
            color =  Color.White.copy(alpha=.6f),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${state.minTemp.toInt()}°C — ${state.maxTemp.toInt()}°C",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )

        RangeSlider(

            value = state.minTemp.toFloat()..state.maxTemp.toFloat(),
            onValueChange = { range ->
                onTempChanged(range.start.toDouble(), range.endInclusive.toDouble())
            },
            valueRange = -100f..100f,
            steps = 199,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(
                stringResource(R.string.create_alert),
                color = Color(0xFF1B233A),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(
            onClick = onCancel,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.cancel), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
    if (activeTimeSelection != null) {
        TimePickerDialog(
            onDismissRequest = { activeTimeSelection = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (activeTimeSelection == TimeSelection.START) {
                            onStartTimeChanged(timePickerState.hour, timePickerState.minute)
                        } else {
                            onEndTimeChanged(timePickerState.hour, timePickerState.minute)
                        }
                        activeTimeSelection = null // Close dialog
                    }
                ) { Text(stringResource(R.string.ok), color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { activeTimeSelection = null }) {
                    Text(stringResource(R.string.cancel), color = Color.LightGray)
                }
            }
        ) {

            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = Color(0xFF26324A),
                    timeSelectorSelectedContainerColor = Color(0xFF4C628A),
                    timeSelectorUnselectedContainerColor = Color(0xFF26324A),
                    timeSelectorSelectedContentColor = Color.White,
                    timeSelectorUnselectedContentColor = Color.LightGray,
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = Color.LightGray,

                    periodSelectorSelectedContainerColor = Color(0xFF4C628A),
                    periodSelectorUnselectedContainerColor = Color(0xFF26324A),
                    periodSelectorSelectedContentColor = Color.White,
                    periodSelectorUnselectedContentColor = Color.LightGray
                )
            )
        }
    }
}


fun formatTime(hour: Int, minute: Int, context: Context): String {
    val isPm = hour >= 12
    val amPm = if (isPm) context.getString(R.string.pm) else context.getString(R.string.am)
    val formattedHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, amPm)
}