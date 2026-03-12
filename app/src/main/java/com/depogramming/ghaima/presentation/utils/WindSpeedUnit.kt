package com.depogramming.ghaima.presentation.utils

import androidx.annotation.StringRes
import com.depogramming.ghaima.R

enum class WindSpeedUnit(val dbValue: String, @StringRes val titleResId: Int) {
    METER_PER_SECOND("m_s", R.string.meter_per_second),
    MILES_PER_HOUR("mph", R.string.miles_per_hour);

    companion object {
        // Helper to translate the database string back into an Enum
        fun fromDbValue(value: String?): WindSpeedUnit {
            return entries.find { it.dbValue == value } ?: METER_PER_SECOND
        }
    }
}