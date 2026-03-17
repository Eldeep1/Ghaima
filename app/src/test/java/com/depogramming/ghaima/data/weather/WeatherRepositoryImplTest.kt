package com.depogramming.ghaima.data.weather
import com.depogramming.ghaima.data.weather.datasource.WeatherRemoteDataSource
import com.depogramming.ghaima.data.weather.datasource.local.entity.FavouritesEntity
import com.depogramming.ghaima.data.weather.datasource.remote.dto.SingleWeatherDTO
import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response


@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {
    private lateinit var remoteDataSource: WeatherRemoteDataSource

    private lateinit var fakeLocalDataSource: FakeLocalDataSource

    private lateinit var repository: WeatherRepositoryImpl
    private lateinit var dummySingleWeatherDTO: SingleWeatherDTO
    private val randomEntity=FavouritesEntity(
        cityName = "Cairo",
        latt = 30.0,
        long = 31.0,
        dateAndTime = "2026-03-17 10:00 AM",
        temperature = 22.5,
        description = "Clear Sky",
        iconResId = 1,
        humidity = "45%",
        windSpeed = 5.2,
        pressure = "1015 hPa",
        cloudCover = "0%"
    )
    private val randomFavouriteModel= FavouriteWeatherModel(
        cityName = "Cairo",
        latt = 30.0,
        long = 31.0,
        dateAndTime = "2026-03-17 10:00 AM",
        description = "Clear Sky",
        iconResId = 1,
        humidity = "45%",
        pressure = "1015 hPa",
        cloudCover = "0%",
        temperature = "22.5",
        windSpeed = "5.2"
    )
    @Before
    fun setup() {
        remoteDataSource = mockk()
        fakeLocalDataSource = FakeLocalDataSource(
            mutableListOf(randomEntity)
        )

        repository = WeatherRepositoryImpl(remoteDataSource, fakeLocalDataSource)

        dummySingleWeatherDTO = mockk(relaxed = true)
    }

    @Test
    fun addToFavourites_apiCallSuccessful_insertsToLocalFake() = runTest {
        val successfulResponse = Response.success(dummySingleWeatherDTO)
        coEvery { remoteDataSource.getSingleWeather(any(), any(), any()) } returns successfulResponse
        repository.addToFavourites(32.0, 33.0, "en", "Alexandria")

        assertEquals(2, fakeLocalDataSource.weatherFavouritesEntityList.size)
        assertEquals("Alexandria", fakeLocalDataSource.weatherFavouritesEntityList.last().cityName)

    }

    @Test
    fun deleteFavourite_itemDeletedSuccessfully_removeFromLocalDataSource()=runTest {

        repository.deleteFavourite(randomFavouriteModel)
        assertEquals(0, fakeLocalDataSource.weatherFavouritesEntityList.size)

    }

    @Test
    fun getFavourite_itemsRetrievedSuccessfully_gotFromLocalDataSource()=runTest {
        fakeLocalDataSource.weatherFavouritesEntityList.clear()

        val dummyEntity = FavouritesEntity(
            cityName = "Cairo",
            latt = 30.0444, long = 31.2357, dateAndTime = "2026-03-17",
            temperature = 22.5, description = "Clear", iconResId = 1,
            humidity = "45%", windSpeed = 5.2, pressure = "1015", cloudCover = "0%"
        )
        fakeLocalDataSource.weatherFavouritesEntityList.add(dummyEntity)

        val resultList = repository.getFavourites("metric", "m/s").first()

        assertEquals(1, resultList.size)
        assertEquals("Cairo", resultList.first().cityName)
    }

}