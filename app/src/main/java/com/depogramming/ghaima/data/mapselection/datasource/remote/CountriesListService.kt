package com.depogramming.ghaima.data.mapselection.datasource.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CountriesListService {
//    ?q=cairo&limit=5"
@GET("geo/1.0/direct")
suspend fun searchCity(@Query("q") query:String,@Query("limit") limit: Int = 5): Response<List<CountriesListNetworkResponse>>
}