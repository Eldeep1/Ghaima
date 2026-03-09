package com.depogramming.ghaima.presentation.onboarding.views.languageScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depogramming.ghaima.data.onBoarding.LanguageModel
import com.depogramming.ghaima.presentation.onboarding.OnboardingScreens
import com.depogramming.ghaima.presentation.onboarding.viewmodel.OnboardingViewModel
import androidx.compose.runtime.collectAsState


@Composable
fun LanguageScreenUI(
    modifier: Modifier = Modifier,
    onClick: (OnboardingScreens) -> Unit,
    viewModel: OnboardingViewModel
) {

    val languages = viewModel.language.collectAsState().value
    if (languages.isEmpty()) {
        // Show a loading spinner while the coroutine fetches the data
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .padding(horizontal = 24.dp)
            .fillMaxSize(),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Spacer(Modifier.height(96.dp))
            Text(
                text = "Choose Your Language",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Select Your Preferred Language for the best weather experience",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = .7f),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(Modifier.height(42.dp))

            if (!languages.isEmpty()) LanguageListScreen(languages,viewModel)
        }
        if (!languages.isEmpty()) {
            ExpandingPageIndicator(
                3,
                0
            )
            Spacer(Modifier.height(32.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable(onClick = {
                        onClick(OnboardingScreens.LocationScreen)
                    }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Continue",
                    color = Color(0xff1E3C72),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(48.dp))

        }


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
fun LanguageListScreen(
    languages: List<LanguageModel>,
    viewModel: OnboardingViewModel
) {

    var selectedLanguage by remember { mutableStateOf<LanguageModel>(viewModel.selectedLanguage) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {


        items(languages) { language ->

            LanguageSelectionButton(
                language = language.language,
                country = language.country,
                flagResId = language.image,
                isSelected = (language == selectedLanguage),
                onClick = {
                    viewModel.selectLanguage(language)
                    selectedLanguage=language
                }
            )

        }
    }
}

@Composable
fun LanguageSelectionButton(
    language: String,
    country: String,
    flagResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {


    val solidWhite = Color.White
    val glassBackground = Color.White.copy(alpha = 0.15f)
    val glassBorder = Color.White.copy(alpha = 0.4f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .then(
                if (!isSelected) {
                    Modifier.border(1.dp, glassBorder, RoundedCornerShape(24.dp))
                } else {
                    Modifier
                }
            )
            .background(if (isSelected) solidWhite else glassBackground)
            .clickable { onClick() }
            .padding(16.dp)
    ) {


        Image(
            painter = painterResource(id = flagResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))


        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = language,
                color = if (isSelected) Color(0xff1E3C72) else Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = country.uppercase(),
                color = if (isSelected) Color(0xff1E3C72).copy(alpha = .6f) else Color.White.copy(
                    alpha = .6f
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }


        if (isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xff1E3C72))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}