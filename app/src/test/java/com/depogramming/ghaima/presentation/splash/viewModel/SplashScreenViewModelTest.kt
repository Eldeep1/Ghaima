package com.depogramming.ghaima.presentation.splash.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.model.UserSettingsModel
import com.depogramming.ghaima.data.weather.model.LocationModel
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
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

class SplashScreenViewModelTest {
    private lateinit var viewModel: SplashScreenViewModel
    private lateinit var userSettingsRepo:UserSettingsRepo

    @OptIn(ExperimentalCoroutinesApi::class)
    val testDispatcher= UnconfinedTestDispatcher()


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun clearing(){
        Dispatchers.resetMain()
    }

    @Before
    fun setup(){
        //given
        userSettingsRepo = mockk()
        every {userSettingsRepo.getUserData() } returns flowOf(null)
        viewModel = SplashScreenViewModel(userSettingsRepo)
        Dispatchers.setMain(testDispatcher)
    }
    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    @Test
    fun getSplashState_userSettingsAreNull_returnOnBoardingScreen()= runTest {
        every { userSettingsRepo.getUserData() } returns flowOf(null)
        viewModel=SplashScreenViewModel(userSettingsRepo)

        assertEquals(SplashState.GoToOnboarding, viewModel.splashState.value)
    }

    @Test
    fun getSplashState_userSettingsAreFullInit_returnHomeScreen()= runTest {
        every { userSettingsRepo.getUserData() } returns flowOf(
            UserSettingsModel(
                "en",
                "metric",
                LocationModel(30.0444, 31.2357, "Cairo, Egypt"),
                "m/s"
            )
        )
        viewModel=SplashScreenViewModel(userSettingsRepo)

        assertEquals(SplashState.GoToHome, viewModel.splashState.value)
    }


    @Test
    fun getSplashState_userSettingsAreSemiInit_returnOnBoardingScreen()= runTest {
        every { userSettingsRepo.getUserData() } returns flowOf(
            UserSettingsModel(
                "en",
                null,
                LocationModel(30.0444, 31.2357, "Cairo, Egypt"),
                null
            )
        )
        viewModel=SplashScreenViewModel(userSettingsRepo)

        assertEquals(SplashState.GoToOnboarding, viewModel.splashState.value)
    }

}