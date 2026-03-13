package com.depogramming.ghaima.data.network

import com.depogramming.ghaima.BuildConfig
import com.depogramming.ghaima.data.mapselection.datasource.remote.CountriesListService
import com.depogramming.ghaima.data.weather.datasource.remote.WeatherForeCastService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {
    private val weatherHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url

                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("appid", BuildConfig.OPEN_WEATHER_API_KEY)
                    .build()


                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()

                chain.proceed(newRequest)
            }
            .build()
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(weatherHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val countriesListService: CountriesListService by lazy {
        retrofit.create(CountriesListService::class.java)
    }
    val weatherForeCastService: WeatherForeCastService by lazy {
        retrofit.create(WeatherForeCastService::class.java)
    }

}

