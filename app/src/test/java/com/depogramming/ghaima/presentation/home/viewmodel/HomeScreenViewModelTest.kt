package com.depogramming.ghaima.presentation.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.WeatherRepository
import com.depogramming.ghaima.data.weather.model.LocationModel
import com.depogramming.ghaima.data.weather.model.WeatherData
import com.depogramming.ghaima.data.weather.model.WeatherForecastModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)

class HomeScreenViewModelTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var userSettingsRepo: UserSettingsRepo
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var viewModel: HomeScreenViewModel

    private lateinit var dummyLocation : LocationModel
    private lateinit var dummySettings : UserSettingsModel
    lateinit var dumbWeatherForecastModel: WeatherForecastModel
    private lateinit var dummyWeatherData : WeatherData

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dummyLocation = LocationModel(30.0444, 31.2357, "Cairo")
        dummySettings = UserSettingsModel("en", "metric", dummyLocation, "m/s")
        dummyWeatherData = mockk<WeatherData>(relaxed = true)
        dumbWeatherForecastModel=WeatherForecastModel("Cairo", dummyWeatherData, emptyList(), emptyList())

        userSettingsRepo = mockk()
        weatherRepository = mockk()

        every { userSettingsRepo.getUserData() } returns flowOf(dummySettings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }



    @Test
    fun init_settingsEmittedAndWeatherFetchSuccess_updatesStateToSuccess() = runTest {

        coEvery {
            weatherRepository.getWeatherForecast(
                latitude = 30.0444,
                longitude = 31.2357,
                language = "en",
                tempUnit = "metric",
                windUnit = "m/s"
            )
        } returns Result.success(dumbWeatherForecastModel)

        viewModel = HomeScreenViewModel(weatherRepository, userSettingsRepo)

        val currentState = viewModel.homeWeatherState.value

        assertTrue(currentState is HomeWeatherStates.Success)
        val successState = currentState as HomeWeatherStates.Success
        assertEquals(dumbWeatherForecastModel.cityName, successState.data.cityName)
    }



    @Test
    fun init_settingsEmittedAndWeatherFetchFails_updatesStateToError() = runTest {
        val errorMessage = "No internet connection"
        coEvery {
            weatherRepository.getWeatherForecast(any(), any(), any(), any(), any())
        } returns Result.failure(Exception(errorMessage))

        viewModel = HomeScreenViewModel(weatherRepository, userSettingsRepo)


        val currentState = viewModel.homeWeatherState.value
        assertTrue(currentState is HomeWeatherStates.Error)

        val errorState = currentState as HomeWeatherStates.Error
        assertEquals(errorMessage, errorState.error)
    }


    @Test
    fun refreshWeather_cachedSettingsExist_fetchesWeatherAgain() = runTest {

        coEvery {
            weatherRepository.getWeatherForecast(any(), any(), any(), any(), any())
        } returns Result.success(WeatherForecastModel("Cairo", dummyWeatherData, emptyList(), emptyList()))


        viewModel = HomeScreenViewModel(weatherRepository, userSettingsRepo)

        viewModel.refreshWeather()

        coVerify(exactly = 2) {
            weatherRepository.getWeatherForecast(any(), any(), any(), any(), any())
        }
    }
}