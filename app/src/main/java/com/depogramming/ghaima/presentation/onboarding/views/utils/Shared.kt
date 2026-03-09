package com.depogramming.ghaima.presentation.onboarding.views.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NextButton(
    modifier: Modifier=Modifier,
    text:String="Continue",onClick:()->Unit){
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = {
                onClick()
            }),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = Color(0xff1E3C72),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}