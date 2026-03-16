package com.depogramming.ghaima.presentation.alarms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.alarms.view.composables.AlertFormContent
import com.depogramming.ghaima.presentation.alarms.view.composables.AlertItemCard
import com.depogramming.ghaima.presentation.alarms.viewmodel.AlarmsUiEvent
import com.depogramming.ghaima.presentation.alarms.viewmodel.AlarmsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreenUI(
    viewModel: AlarmsViewModel,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val savedAlerts by viewModel.savedAlerts.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val context= LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }



    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AlarmsUiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short,

                    )
                }
                is AlarmsUiEvent.DismissBottomSheet -> {

                    showBottomSheet = false
                }
            }
        }
    }
    Scaffold(

        modifier=modifier,

        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true }, containerColor = Color(0xff8BA6CF),
                contentColor = Color.White,
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        println(paddingValues)
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp,bottom=32.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.alerts),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                if (savedAlerts.isEmpty()) {
                    // Show a nice empty state if there are no alerts yet
                    Text(
                        text = stringResource(R.string.no_alerts_configured_tap_to_create_one),
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = savedAlerts,
                            key = { it.id }
                        ) { alert ->
                            AlertItemCard(
                                alert = alert,
                                onToggle = { isChecked ->
                                    viewModel.toggleAlertActiveState(context, alert, isChecked)
                                },
                                onDelete = {
                                    viewModel.deleteAlert(context, alert)
                                }
                            )
                        }
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState, modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(0.dp)
                ) { data ->
                    // This is where you customize the actual physical Snackbar bubble!
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xff4A6FA5),
                        contentColor = Color.White,
                        actionColor = Color(0xff8BA6CF),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xff223B63),
                contentColor = Color.White
            ) {

                AlertFormContent(
                    state = formState,
                    onModeChanged = viewModel::updateMode,
                    onDescChanged = viewModel::updateDescription,
                    onTempChanged = viewModel::updateTempRange,
                    onSave = {
                        viewModel.saveAlert(
                            context= context,
                        )
                        showBottomSheet = false
                    },
                    onCancel = { showBottomSheet = false },
                    onStartTimeChanged = { hour, minute ->
                        viewModel.updateStartTime(hour, minute)
                    },
                    onEndTimeChanged = { hour, minute ->
                        viewModel.updateEndTime(hour, minute)
                    }
                )
            }
        }
    }
}