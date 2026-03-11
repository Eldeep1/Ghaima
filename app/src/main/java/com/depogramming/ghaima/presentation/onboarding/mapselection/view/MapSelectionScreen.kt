package com.depogramming.ghaima.presentation.onboarding.mapselection.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.onboarding.mapselection.viewmodel.MapSelectionViewModel
import com.depogramming.ghaima.presentation.onboarding.views.utils.NextButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition


import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSelectionScreenUI(modifier: Modifier = Modifier, viewModel: MapSelectionViewModel,onBackClick:()->Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {
        CustomAppBar(onBackClick)
        CustomSearchBar(viewModel)
        CustomMapPreview(Modifier.weight(1f),viewModel)
        Spacer(Modifier.height(16.dp))
        val scope = rememberCoroutineScope()

        SelectLocationButton(onButtonClick = {
            scope.launch {
                viewModel.onSubmitButtonClick()
                onBackClick()
            }
        })
        Spacer(Modifier.height(40.dp))
    }

}

@Composable
fun CustomAppBar(onBackClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
    ) {
        //1. back button
        //2. Select On Map
        Image(
            painter = painterResource(R.drawable.backbutton),
            contentDescription = "back button",
            modifier = Modifier.clickable(enabled = true, onClick = {
                onBackClick()
            })
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Map Selection",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(viewModel: MapSelectionViewModel) {
    var isSearchExpanded by rememberSaveable { mutableStateOf(false) }

    val currentText by viewModel.searchQuery.collectAsStateWithLifecycle()
    val resultsList by viewModel.searchResults.collectAsStateWithLifecycle()

    DockedSearchBar(
        inputField = {
            InputField(
                query = currentText,
                onQueryChange = { newText ->
                    viewModel.onSearchQueryChanged(newText)
                    isSearchExpanded = newText.isNotBlank()
                },
                onSearch = {
                    isSearchExpanded = false
                },
                expanded = isSearchExpanded,
                onExpandedChange = { isSearchExpanded = it },
                placeholder = {
                    Text(
                        stringResource(R.string.search_for_a_city),
                        color = Color.White.copy(alpha = .6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White.copy(alpha = .7f)
                    )
                },
                colors = SearchBarDefaults.inputFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White.copy(alpha = .8f)
                )

            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = SearchBarDefaults.colors(
            containerColor = Color.White.copy(alpha = .2f),
            dividerColor = Color.Transparent
        ),

        expanded = isSearchExpanded,
        onExpandedChange = {isSearchExpanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(max = 200.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {

            items(resultsList) { city ->
                val displayText = "${city.name}, ${city.state}, ${city.country}"
                Text(
                    text = displayText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onSearchQueryChanged(displayText)
                            isSearchExpanded = false
                            viewModel.onCitySelected(city)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    color = Color.White
                )
            }

        }
    }
}

@Composable
fun CustomMapPreview(modifier:Modifier=Modifier,viewModel: MapSelectionViewModel) {
    val currentLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val mapLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)

    val markerState = remember { MarkerState(position = mapLatLng) }
    markerState.position = mapLatLng

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapLatLng, 7f)
    }

    LaunchedEffect(key1 = mapLatLng) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                mapLatLng,
                12f
            ),
            durationMs = 1000
        )
    }

    Column(
        modifier = modifier
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapClick = { tappedLatLng ->
                viewModel.updateLocationFromMap(
                    lat = tappedLatLng.latitude,
                    lng = tappedLatLng.longitude
                )
            }
        ) {
            Marker(
                state = markerState,
                title = "Selected Location",
                snippet = "Welcome to Egypt!"
            )
        }
    }
}
@Composable
fun SelectLocationButton(modifier:Modifier=Modifier,onButtonClick:()->Unit) {
    NextButton(modifier=modifier.padding(horizontal = 24.dp)) {
        onButtonClick()
    }
}