package com.depogramming.ghaima.di

import androidx.room.Room
import androidx.work.WorkerParameters
import com.depogramming.ghaima.BuildConfig
import com.depogramming.ghaima.data.alerts.datasource.local.AlertsLocalDataSource
import com.depogramming.ghaima.data.alerts.repository.AlertsRepo
import com.depogramming.ghaima.data.alerts.repository.AlertsRepositoryImpl
import com.depogramming.ghaima.data.db.AppDatabase
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import com.depogramming.ghaima.data.usersettings.UserSettingsRepoImp
import com.depogramming.ghaima.data.usersettings.datasource.local.UserSettingsLocalDataSource
import com.depogramming.ghaima.data.weather.WeatherRepository
import com.depogramming.ghaima.data.weather.WeatherRepositoryImpl
import com.depogramming.ghaima.data.weather.datasource.WeatherRemoteDataSource
import com.depogramming.ghaima.data.weather.datasource.local.WeatherLocalDataSource
import com.depogramming.ghaima.data.weather.datasource.local.WeatherLocalDataSourceImp
import com.depogramming.ghaima.data.weather.datasource.remote.WeatherRemoteDataSourceImpl
import com.depogramming.ghaima.data.weather.datasource.remote.WeatherService
import com.depogramming.ghaima.presentation.alarms.viewmodel.AlarmsViewModel
import com.depogramming.ghaima.presentation.home.viewmodel.HomeScreenViewModel
import com.depogramming.ghaima.presentation.mapselection.viewmodel.CurrentLocationMapSelection
import com.depogramming.ghaima.presentation.mapselection.viewmodel.FavouriteLocationMapSelection
import com.depogramming.ghaima.presentation.mapselection.viewmodel.MapSelectionViewModel
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel
import com.depogramming.ghaima.presentation.savedlocations.viewmodel.SavedLocationsViewModel
import com.depogramming.ghaima.presentation.settings.viewmodel.SettingsViewModel
import com.depogramming.ghaima.presentation.splash.viewModel.SplashScreenViewModel
import com.depogramming.ghaima.presentation.utils.location.LocationHelper
import com.depogramming.ghaima.worker.WeatherAlertWorker
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.core.module.dsl.viewModel

val dataModule = module {
    single<OkHttpClient> {
        OkHttpClient.Builder().addInterceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url

                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("appid", BuildConfig.OPEN_WEATHER_API_KEY).build()


                val newRequest = originalRequest.newBuilder().url(newUrl).build()

                chain.proceed(newRequest)
            }.build()
    }
    single<Retrofit> {
        Retrofit.Builder().baseUrl("https://api.openweathermap.org/").client(get())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(), AppDatabase::class.java, "movies_db"
        ).build()
    }
    single {
        val retrofit: Retrofit = get()
        retrofit.create(WeatherService::class.java)
    }
    single {
        val db = get<AppDatabase>()
        db.favouritesDao()
    }
    single {
        val db = get<AppDatabase>()
        db.alertsDao()
    }
    single {
        val db = get<AppDatabase>()
        db.weatherForecastDao()
    }
    single {
        val db = get<AppDatabase>()
        db.userSettingsDao()
    }

    factory<AlertsLocalDataSource> {
        AlertsLocalDataSource(get())
    }
    factory<UserSettingsLocalDataSource> {
        UserSettingsLocalDataSource(get())
    }
    factory<WeatherLocalDataSource> {
        WeatherLocalDataSourceImp(get(), get())
    }
    factory<WeatherRemoteDataSource> {
        WeatherRemoteDataSourceImpl(get())
    }
    factory<LocationHelper> {
        LocationHelper(androidApplication())
    }
    factory<AlertsRepo> {
        AlertsRepositoryImpl(get())
    }
    factory<UserSettingsRepo> {
        UserSettingsRepoImp(get())
    }

    factory<WeatherRepository> {
        WeatherRepositoryImpl(get(), get())
    }
    viewModel<OnboardingViewModel> {
        OnboardingViewModel(get(), get())
    }
    viewModel<AlarmsViewModel> {
        AlarmsViewModel(get())
    }
    viewModel<HomeScreenViewModel> {
        HomeScreenViewModel(get(), get())
    }
    viewModel<CurrentLocationMapSelection> {
        CurrentLocationMapSelection(get(), get(), get())
    }
    viewModel<FavouriteLocationMapSelection> {
        FavouriteLocationMapSelection(get(), get(), get())
    }
    viewModel<SavedLocationsViewModel> {
        SavedLocationsViewModel(get(), get(), get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(get(), get())
    }
    viewModel<SplashScreenViewModel> {
        SplashScreenViewModel(get())
    }

    worker { (workerParams: WorkerParameters) ->
        WeatherAlertWorker(
            context = get(),
            workerParams = workerParams,
            alertsRepository = get(),
            weatherRepo = get()
        )
    }
}


