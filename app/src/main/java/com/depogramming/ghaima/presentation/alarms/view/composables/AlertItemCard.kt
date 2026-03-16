package com.depogramming.ghaima.presentation.alarms.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.alerts.model.ExactAlarmAlert
import com.depogramming.ghaima.data.alerts.model.NotificationAlert
import com.depogramming.ghaima.data.alerts.model.WeatherAlertModel
import com.depogramming.ghaima.presentation.utils.CustomConfirmDialog
import kotlin.math.roundToInt

@Composable
fun AlertItemCard(
    alert: WeatherAlertModel,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val (icon, typeText, timeText) = when (alert) {
        is ExactAlarmAlert -> {
            val hour = (alert.exactTime / 60).toInt()
            val min = (alert.exactTime % 60).toInt()
            Triple(
                painterResource(R.drawable.alarm_ic),
                stringResource(R.string.alarm),
                formatTime(hour, min, context)
            )
        }

        is NotificationAlert -> {
            val startHr = (alert.windowStart / 60).toInt()
            val startMin = (alert.windowStart % 60).toInt()
            val endHr = (alert.windowEnd / 60).toInt()
            val endMin = (alert.windowEnd % 60).toInt()
            Triple(
                painterResource(R.drawable.notification_ic),
                stringResource(R.string.notification),
                "${formatTime(startHr, startMin, context)} - ${formatTime(endHr, endMin, context)}"
            )
        }
    }
    var alertToDelete by remember { mutableStateOf<WeatherAlertModel?>(null) }


    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.2f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = icon,
                contentDescription = null
            )
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = typeText,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = alert.description.ifBlank { stringResource(R.string.no_description) },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${alert.minTemp.roundToInt()}°C to ${alert.maxTemp.roundToInt()}°C",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = timeText,
                    color = Color.White.copy(alpha = .7f),
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Switch(
                    checked = alert.isActive,
                    onCheckedChange = onToggle,
                    thumbContent = null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xff5F7295),
                        checkedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color(0xFF1B233A),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
                IconButton(
                    onClick = {
                        alertToDelete = alert
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Alert",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
    alertToDelete?.let { alert ->
        CustomConfirmDialog(
            title = stringResource(R.string.delete_alarm),
            message = stringResource(
                R.string.are_you_sure_you_want_to_delete_the_alarm_for,
            ),
            onDismiss = {
                alertToDelete = null
            },
            onConfirm = {
                onDelete()
                alertToDelete = null
            }
        )
    }

}
