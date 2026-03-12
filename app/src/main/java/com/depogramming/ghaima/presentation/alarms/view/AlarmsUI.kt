package com.depogramming.ghaima.presentation.alarms.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AlarmsUI(modifier: Modifier=Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()

    ) {
        Text("The Alarms")
    }
}