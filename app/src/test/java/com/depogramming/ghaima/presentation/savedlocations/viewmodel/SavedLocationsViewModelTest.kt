package com.depogramming.ghaima.presentation.savedlocations.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.WeatherRepository
import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel
import com.depogramming.ghaima.data.weather.model.LocationModel
import com.depogramming.ghaima.presentation.utils.location.LocationHelper
import com.depogramming.ghaima.presentation.utils.location.LocationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SavedLocationsViewModelTest {
    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var userSettingsRepo: UserSettingsRepo
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var locationHelper: LocationHelper
    private lateinit var viewModel: SavedLocationsViewModel

    private lateinit var dummySettings: UserSettingsModel
    private lateinit var dummyLocation: LocationModel
    private lateinit var dummyFavourite: FavouriteWeatherModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        dummyLocation = LocationModel(30.0444, 31.2357, "Cairo")
        dummySettings = UserSettingsModel("en", "metric", dummyLocation, "m/s")
        dummyFavourite = mockk<FavouriteWeatherModel>(relaxed = true)

        userSettingsRepo = mockk()
        weatherRepository = mockk()
        locationHelper = mockk()

        every { userSettingsRepo.getUserData() } returns flowOf(dummySettings)

        coEvery { weatherRepository.addToFavourites(any(), any(), any(), any()) } returns Result.success(Unit)
        coEvery { weatherRepository.deleteFavourite(any()) } returns Result.success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun init_favouritesFlowIsEmpty_updatesStateToEmptyList() = runTest {

        every { weatherRepository.getFavourites(any(), any()) } returns flowOf(emptyList())

        viewModel = SavedLocationsViewModel(weatherRepository, userSettingsRepo, locationHelper)

        val currentState = viewModel.favouritesState.value
        assertTrue(currentState is SavedStates.EmptyList)
    }

    @Test
    fun init_favouritesFlowHasData_updatesStateToSuccessWithData() = runTest {
        val dummyList = listOf(dummyFavourite)
        every { weatherRepository.getFavourites(any(), any()) } returns flowOf(dummyList)

        viewModel = SavedLocationsViewModel(weatherRepository, userSettingsRepo, locationHelper)

        val currentState = viewModel.favouritesState.value
        assertTrue(currentState is SavedStates.Success)

        val successState = currentState as SavedStates.Success
        assertEquals(dummyList, successState.data)
    }


    @Test
    fun fetchCurrentLocation_whenPermissionIsMissing_emitsAskForPermissionEvent() = runTest {
        every { weatherRepository.getFavourites(any(), any()) } returns flowOf(emptyList())
        coEvery { locationHelper.getCurrentLocation() } returns LocationResult.MissingPermission

        viewModel = SavedLocationsViewModel(weatherRepository, userSettingsRepo, locationHelper)

        val emissions = mutableListOf<Unit>()
        backgroundScope.launch {
            viewModel.askForPermission.toList(emissions)
        }
        runCurrent()

        viewModel.fetchCurrentLocation()
        runCurrent()

        assertEquals(1, emissions.size)
    }

    @Test
    fun fetchCurrentLocation_whenGpsIsDisabled_emitsOpenLocationSettingsEvent() = runTest {
        every { weatherRepository.getFavourites(any(), any()) } returns flowOf(emptyList())
        coEvery { locationHelper.getCurrentLocation() } returns LocationResult.GpsDisabled

        viewModel = SavedLocationsViewModel(weatherRepository, userSettingsRepo, locationHelper)

        val emissions = mutableListOf<Unit>()
        backgroundScope.launch {
            viewModel.openLocationSettingsEvent.toList(emissions)
        }
        runCurrent()
        viewModel.fetchCurrentLocation()
        runCurrent()

        assertEquals(1, emissions.size)
    }

    @Test
    fun deleteFavourite_callsRepositoryDeleteWithCorrectItem() = runTest {
        every { weatherRepository.getFavourites(any(), any()) } returns flowOf(emptyList())
        viewModel = SavedLocationsViewModel(weatherRepository, userSettingsRepo, locationHelper)

        viewModel.deleteFavourite(dummyFavourite)

        coVerify(exactly = 1) {
            weatherRepository.deleteFavourite(dummyFavourite)
        }
    }
}