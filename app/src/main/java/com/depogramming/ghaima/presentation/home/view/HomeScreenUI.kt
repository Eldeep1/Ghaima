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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.depogramming.ghaima.data.weather.model.CurrentWeatherModel
import com.depogramming.ghaima.data.weather.model.DailyWeatherModel
import com.depogramming.ghaima.data.weather.model.HourlyWeatherModel
import com.depogramming.ghaima.data.weather.model.WeatherForecastModel
import com.depogramming.ghaima.presentation.home.viewmodel.HomeScreenViewModel
import com.depogramming.ghaima.presentation.home.viewmodel.HomeWeatherStates

@Composable
fun HomeScreenUI(modifier: Modifier = Modifier,viewModel: HomeScreenViewModel) {
    val uiState by viewModel.homeWeatherState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()

    ) {
        when (uiState) {
            is HomeWeatherStates.Loading -> {
                HomeScreenLoading()
            }
            is HomeWeatherStates.Error -> {
                val errorMessage = (uiState as HomeWeatherStates.Error).error
                HomeScreenError(errorMessage = errorMessage) {
                    viewModel.refreshWeather()
                }
            }
            is HomeWeatherStates.Success -> {
                val weatherData = (uiState as HomeWeatherStates.Success).data
                HomeScreenContent(weatherData = weatherData)
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    weatherData: WeatherForecastModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
    ) {
        CustomAppBar(cityName = weatherData.cityName)
        TodayWeather(currentWeather = weatherData.current)
        HourlyForeCast(hourlyList = weatherData.hourlyForecast)

        Spacer(modifier = Modifier.height(24.dp))

        WeekForeCast(dailyList = weatherData.dailyForecast)

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun HomeScreenLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun HomeScreenError(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Oops!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = errorMessage, color = Color.White.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "Try Again")
        }
    }
}

@Composable
fun CustomAppBar(cityName: String,modifier: Modifier = Modifier) {
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
                    text = cityName,
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
fun TodayWeather(currentWeather: CurrentWeatherModel) {
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
                text = currentWeather.dateAndTime,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(currentWeather.iconResId),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = currentWeather.temperature,
                fontSize = 80.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = currentWeather.description,
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
                WeatherStatItem(R.drawable.humidity_ic, "HUMID", currentWeather.humidity)
                WeatherStatItem(R.drawable.wind_ic, "WIND", currentWeather.windSpeed)
                WeatherStatItem(R.drawable.pressure_ic, "PRES", currentWeather.pressure)
                WeatherStatItem(R.drawable.cloud_ic, "CLOUD", currentWeather.cloudCover)
            }
        }
    }
}

@Composable
fun HourlyForeCast(hourlyList: List<HourlyWeatherModel>, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(42.dp))
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Today",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Next 24 Hours",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(hourlyList) { item ->
                HourlyWeatherItem(
                    time = item.time,
                    iconRes = item.iconResId,
                    temperature = item.temperature
                )
            }
        }
    }
}

@Composable
fun WeekForeCast(dailyList: List<DailyWeatherModel>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "5-Day Forecast",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        dailyList.forEach { day->
            DailyWeatherItem(
                day = day.dayOfWeek,
                iconRes = day.iconResId,
                description = day.description,
                highTemp = day.highTemp,
                lowTemp = day.lowTemp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
@Composable
fun HourlyWeatherItem(
    time: String,
    iconRes: Int,
    temperature: String
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .height(132.dp)
            .clip(RoundedCornerShape(32.dp))
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
                shape = RoundedCornerShape(32.dp)
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = time,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = temperature,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
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

@Composable
fun DailyWeatherItem(
    day: String,
    iconRes: Int,
    description: String,
    highTemp: String,
    lowTemp: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
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
            )
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = day,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = highTemp,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lowTemp,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}