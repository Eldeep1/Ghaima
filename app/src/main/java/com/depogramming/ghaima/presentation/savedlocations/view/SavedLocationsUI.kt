package com.depogramming.ghaima.presentation.savedlocations.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.depogramming.ghaima.R
import com.depogramming.ghaima.data.weather.model.FavouriteWeatherModel
import com.depogramming.ghaima.presentation.savedlocations.viewmodel.SavedLocationsViewModel
import com.depogramming.ghaima.presentation.savedlocations.viewmodel.SavedStates
import com.depogramming.ghaima.presentation.utils.LocationBottomSheetContent
import com.depogramming.ghaima.presentation.utils.location.LocationPermissionHandler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsUI(
    modifier: Modifier = Modifier,
    viewModel: SavedLocationsViewModel,
    onMapClick: () -> Unit
) {
    val favStates by viewModel.favouritesState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showLocationSheet by remember { mutableStateOf(false) }

    LocationPermissionHandler(
        askForPermissionFlow = viewModel.askForPermission,
        openSettingsFlow = viewModel.openLocationSettingsEvent,
        onPermissionResult = { fine, coarse -> viewModel.onLocationPermissionResult(fine, coarse) }
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showLocationSheet = true
                },
                containerColor = Color(0xff8BA6CF),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new saved location"
                )
            }
        }
    ) { innerFabPadding ->

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            CustomAppBar()
            Spacer(Modifier.height(32.dp))

            when (favStates) {
                SavedStates.Error -> ErrorScreen()
                SavedStates.Loading -> LoadingScreen()
                is SavedStates.Success -> {
                    val data = (favStates as SavedStates.Success).data
                    FavouritesList(favourites = data, onDelete = {
                        viewModel.deleteFavourite(it)
                    })
                }

                SavedStates.EmptyList -> EmptyScreen()
            }

        }
    }
    if (showLocationSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showLocationSheet = false
            },
            sheetState = sheetState,
            containerColor = Color(0xff223B63)
        ) {

            LocationBottomSheetContent(
                onCurrentLocationClick = {
                    showLocationSheet = false
                    viewModel.fetchCurrentLocation()
                },
                onMapClick = {
                    showLocationSheet = false
                    onMapClick()
                }
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color.White
        )
    }
}

@Composable
fun ErrorScreen() {

}

@Composable
fun EmptyScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_saved_locations),
            color = Color.White.copy(alpha = .8f),
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun CustomAppBar(modifier: Modifier = Modifier) {
    Spacer(Modifier.height(48.dp))
    Text(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(R.string.saved_locations),
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center
    )
}

@Composable
fun FavouritesList(
    modifier: Modifier = Modifier,
    favourites: List<FavouriteWeatherModel>,
    onDelete: (FavouriteWeatherModel) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(favourites) { favourite ->
            CardItem(favourite = favourite, onDelete = {
                onDelete(favourite)
            }) {}
        }
    }
}

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    favourite: FavouriteWeatherModel,
    onDelete: (FavouriteWeatherModel) -> Unit,
    onClicked: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled
    )
    if (showDeleteDialog) {
        Dialog(
            onDismissRequest = {
                showDeleteDialog = false
                coroutineScope.launch { dismissState.reset() }
            }
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xff8BA6CF),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = stringResource(R.string.delete_location),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(
                            R.string.are_you_sure_you_want_to_delete_from_you_saved_locations,
                            favourite.cityName
                        ),
                        color = Color.White.copy(alpha = .8f),
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                coroutineScope.launch { dismissState.reset() } // Snap back!
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                onDelete(favourite)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(
                                    alpha = .7f
                                )
                            )
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            showDeleteDialog = true
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {}
    ) {

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.2f),
            onClick = onClicked
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = favourite.dateAndTime,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = favourite.cityName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = favourite.description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = favourite.iconResId),
                        contentDescription = favourite.description,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = favourite.temperature,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}