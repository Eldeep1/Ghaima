package com.depogramming.ghaima.presentation.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depogramming.ghaima.R

@Composable
fun HomeScreenUI(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()

    ) {
        HomeScreenContent()
    }
}

@Composable
fun HomeScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp)
    ) {
        CustomAppBar()
        TodayWeather()
        HourlyForeCast()
        WeekForeCast()
    }
}

@Composable
fun HomeScreenLoading(modifier: Modifier = Modifier) {

}

@Composable
fun HomeScreenError(modifier: Modifier = Modifier) {

}

@Composable
fun CustomAppBar(modifier: Modifier = Modifier) {
    Spacer(Modifier.height(48.dp))
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.ghaima),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.weight(1f))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(152.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp),

                ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp),
            ) {

                Image(
                    painter = painterResource(R.drawable.location_ic),
                    contentDescription = null
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "value from view model",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
@Composable
fun TodayWeather() {
    Spacer(Modifier.height(16.dp))

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.6f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(24.dp),
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        ) {

            Text(
                text = "Monday, 12 Feb | 10:30 AM",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.snow_ic),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "2°C",
                fontSize = 80.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Light Snowfall",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(32.dp))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 32.dp),
                thickness = 1.dp,
                color = Color.White.copy(alpha = 0.15f)
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherStatItem(R.drawable.humidity_ic, "HUMID", "84%")
                WeatherStatItem(R.drawable.wind_ic, "WIND", "12km/h")
                WeatherStatItem(R.drawable.pressure_ic, "PRES", "1012")
                WeatherStatItem(R.drawable.cloud_ic, "CLOUD", "92%")
            }
        }
    }
}

@Composable
fun HourlyForeCast() {

}

@Composable
fun WeekForeCast() {

}

@Composable
fun WeatherStatItem(
    iconRes: Int,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}