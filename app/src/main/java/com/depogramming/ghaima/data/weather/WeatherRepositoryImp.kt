package com.depogramming.ghaima.data.weather


import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListNetworkResponse
import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListRemoteDataSource
import com.depogramming.ghaima.data.weather.model.WeatherForecastModel
import com.depogramming.ghaima.data.weather.model.toWeatherForecastModel
import com.depogramming.ghaima.data.weather.remote.WeatherRemoteDataSource
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit

class WeatherRepositoryImpl(
    private val countriesListRemoteDataSource: CountriesListRemoteDataSource,
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
//    private val weatherLocalDataSource: WeatherLocalDataSource,
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

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        language: String,
        tempUnit: String,
        windUnit: String,
    ): Result<WeatherForecastModel> {
        return try {

            val response = weatherRemoteDataSource.getWeatherForeCast(latitude, longitude, language)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {

                    val targetTempEnum = TemperatureUnit.fromApiValue(tempUnit)
                    val targetWindEnum = WindSpeedUnit.fromDbValue(windUnit)


                    val mappedData = body.toWeatherForecastModel(
                        sourceTempUnit = TemperatureUnit.CELSIUS,
                        targetTempUnit = targetTempEnum,
                        sourceWindUnit = WindSpeedUnit.METER_PER_SECOND,
                        targetWindUnit = targetWindEnum
                    )

                    Result.success(mappedData)

                } else {
                    Result.failure(Exception("Weather data is empty"))
                }
            } else {
                println("API Error: ${response.code()} ${response.message()}")
                Result.failure(Exception("Server Error Occurred, Try Again Later "))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Check Your Connection And Try Again"))
        }
    }
}
