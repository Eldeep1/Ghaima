package com.depogramming.ghaima.presentation.onboarding.views.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider

import androidx.compose.ui.res.painterResource

import com.depogramming.ghaima.R
@Composable
fun NextButton(
    modifier: Modifier=Modifier,
    text:String= stringResource(R.string.continue_btn), onClick:()->Unit){
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

@Composable
fun ExpandingPageIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage


            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = tween(durationMillis = 300),
                label = "indicator_width"
            )


            val color by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                animationSpec = tween(durationMillis = 300),
                label = "indicator_color"
            )


            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun SettingsSelectionCard(
    titleResId: Int,
    optionsResIds: List<Int>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .2f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(id = titleResId),
                color = Color.White.copy(alpha = .6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(16.dp))

            optionsResIds.forEachIndexed { index, optionTitleResId ->
                val isSelected = index == selectedIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp).clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onOptionSelected(index)
                        }.padding(horizontal = 8.dp) ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = optionTitleResId),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Image(
                        painter = painterResource(
                            if (isSelected) R.drawable.selected_row else R.drawable.unselected_row
                        ),
                        contentDescription = if (isSelected) "Selected" else "Unselected"
                    )
                }

                if (index < optionsResIds.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}