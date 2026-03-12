package com.depogramming.ghaima.presentation.utils

import androidx.annotation.StringRes
import com.depogramming.ghaima.R

enum class TemperatureUnit(val apiValue: String, @StringRes val titleResId: Int) {
    CELSIUS("metric", R.string.celsius_c),
    FAHRENHEIT("imperial", R.string.fahrenheit_f),
    KELVIN("standard", R.string.kelvin_k);

    companion object {
        fun fromApiValue(value: String?): TemperatureUnit {
            return entries.find { it.apiValue == value } ?: CELSIUS
        }
    }
}