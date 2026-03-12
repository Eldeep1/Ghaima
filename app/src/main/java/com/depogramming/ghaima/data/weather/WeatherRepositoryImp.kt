package com.depogramming.ghaima.data.weather


import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListNetworkResponse
import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListRemoteDataSource

class WeatherRepositoryImpl(
    private val countriesListRemoteDataSource: CountriesListRemoteDataSource
) {

    suspend fun searchCity(query: String): Result<List<CountriesListNetworkResponse>> {

        return try {
            val response = countriesListRemoteDataSource.searchCity(query)
            if (response.isSuccessful) {
                println("ther's no error acutally")
                Result.success(response.body() ?: emptyList())
            } else {
                println("the error is:")
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("the error is: ${e.printStackTrace()}")

            Result.failure(e)

        }
    }
}