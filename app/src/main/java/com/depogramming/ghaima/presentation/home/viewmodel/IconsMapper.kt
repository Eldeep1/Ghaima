package com.depogramming.ghaima.presentation.home.viewmodel

import androidx.annotation.DrawableRes
import com.depogramming.ghaima.R

@DrawableRes
fun getWeatherIconRes(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.clear_day_ic
        "01n" -> R.drawable.clear_night_ic
        "02d", "03d", "04d" -> R.drawable.partly_cloudy_ic
        "02n", "03n", "04n" -> R.drawable.cloudy_night_ic
        "09d", "09n", "10d", "10n" -> R.drawable.rainy_ic
        "11d", "11n" -> R.drawable.storm_ic
        "13d", "13n" -> R.drawable.snow_ic
        "50d", "50n" -> R.drawable.mist_ic
        else -> R.drawable.clear_day_ic
    }
}