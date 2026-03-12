package com.depogramming.ghaima.data.mapselection.datasource.remote

import retrofit2.Response

class CountriesListRemoteDataSource(
    val countriesListService: CountriesListService
) {
    suspend fun searchCity(query:String): Response<List<CountriesListNetworkResponse>> {
        return countriesListService.searchCity(query)
    }
}