package com.depogramming.ghaima.data.mapselection.datasource.remote

import com.google.gson.annotations.SerializedName


data class CountriesListNetworkResponse(
    val name: String,
    @SerializedName("local_names")
    val localNames: LocalNames?,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?,
)

data class LocalNames(
    val ar: String?,
    val en: String?,
)
