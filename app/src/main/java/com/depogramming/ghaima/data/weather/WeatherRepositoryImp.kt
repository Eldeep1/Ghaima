package com.depogramming.ghaima.data.weather


import com.depogramming.ghaima.data.weather.datasource.remote.dto.CountriesListDTO
import com.depogramming.ghaima.data.weather.datasource.local.WeatherLocalDataSource
import com.depogramming.ghaima.data.weather.model.WeatherForecastModel
import com.depogramming.ghaima.data.weather.mapper.toWeatherForecastModel
import com.depogramming.ghaima.data.weather.datasource.remote.WeatherRemoteDataSource
import com.depogramming.ghaima.data.weather.mapper.toEntity
import com.depogramming.ghaima.data.weather.mapper.toFavouriteWeatherModel
import com.depogramming.ghaima.data.weather.mapper.toFavouritesEntity
import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel
import com.depogramming.ghaima.presentation.utils.TemperatureUnit
import com.depogramming.ghaima.presentation.utils.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherRepositoryImpl(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource,
) {

    suspend fun searchCity(query: String): Result<List<CountriesListDTO>> {

        return try {
            val response = weatherRemoteDataSource.searchCity(query)

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)

        }
    }

    //adding location to favourites flow
    //1. the user selects a place on the map
    //2. we get that place information
    //3. we store that information on map
    //4. the stored information is stored on the map
    //----------------------------------------------------
    //opening the favourites tab again:
    //1. get list of stored favourites
    //2. if there's connection, then hit the api and update the database
    //3. if there's no connection, then just show the retrieved data
    //--------------------------------------------------
    // so, functions should be:
    //1. add to favourites
    //2. remove from favourites
    //3. get favourites
    //no more ore less.
    suspend fun addToFavourites(
        latitude: Double,
        longitude: Double,
        language: String,
    ): Result<Unit> {
        try {
            val response = weatherRemoteDataSource.getSingleWeather(latitude, longitude, language)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    try {
                        weatherLocalDataSource.insertWeatherFavourite(body.toFavouritesEntity())
                        return Result.success(Unit)
                    } catch (e: Exception) {
                        return Result.failure(e)
                    }
                } else {
                    return Result.failure(Exception("Weather data is empty"))
                }

            } else {
                return Result.failure(Exception("Server Error Occurred, Try Again Later "))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }


    fun getFavourites(
        targetTempUnit: String,
        targetWindUnit: String
    ): Flow<List<FavouriteWeatherModel>> {

        return weatherLocalDataSource.getAllFavourites()
            .map { entityList ->
                entityList.map { entity ->
                    entity.toFavouriteWeatherModel(targetTempUnit, targetWindUnit)
                }
            }
    }

    suspend fun deleteFavourite(
        favouriteWeatherModel: FavouriteWeatherModel
    ): Result<Unit> {
        try {
            weatherLocalDataSource.deleteWeatherFavourites(favouriteWeatherModel.toFavouritesEntity())
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
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

                    insertWeatherForecast(mappedData)
                    Result.success(mappedData)

                } else {
                    val localData = getLocalWeatherForecast()
                    if (!localData.hourlyForecast.isEmpty() && !localData.cityName.isEmpty()) {
                        Result.success(localData)
                    } else
                        Result.failure(Exception("Weather data is empty"))
                }
            } else {
                val localData = getLocalWeatherForecast()
                if (!localData.hourlyForecast.isEmpty() && !localData.cityName.isEmpty()) {
                    Result.success(localData)
                } else {
                    println("API Error: ${response.code()} ${response.message()}")
                    Result.failure(Exception("Server Error Occurred, Try Again Later "))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val localData = getLocalWeatherForecast()
            if (!localData.hourlyForecast.isEmpty() && !localData.cityName.isEmpty()) {
                Result.success(localData)
            } else
                Result.failure(Exception("Check Your Connection And Try Again"))
        }
    }

    suspend fun getLocalWeatherForecast(): WeatherForecastModel {
        val localData = weatherLocalDataSource.getWeatherForecast()
        return localData.toWeatherForecastModel()
    }

    suspend fun insertWeatherForecast(weatherForecastModel: WeatherForecastModel) {
        val entity = weatherForecastModel.toEntity()
        weatherLocalDataSource.insertWeatherForecast(entity)
    }
}
