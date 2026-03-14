package com.depogramming.ghaima.presentation.savedlocations.viewmodel

import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel

sealed class SavedStates {
    data object EmptyList: SavedStates()
    data object Loading: SavedStates()
    data class Success(val data:List<FavouriteWeatherModel>): SavedStates()
    data object Error: SavedStates()
}