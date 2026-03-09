package com.depogramming.ghaima.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depogramming.ghaima.R
import com.depogramming.ghaima.presentation.onboarding.views.utils.OnboardingScreens

@Composable
fun SplashScreenUI(modifier: Modifier = Modifier, onNextScreen: (screen: OnboardingScreens) -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val progress by animateLottieCompositionAsState(composition, iterations = 1)
    var startTextAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(progress) {
        if (progress == 1f) {
            onNextScreen(OnboardingScreens.WelcomeScreen)
        }
    }

    val animateStage = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animateStage.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = EaseOutCubic)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.0f to MaterialTheme.colorScheme.primary,
                    (animateStage.value * 0.5f) to MaterialTheme.colorScheme.secondary,
                    animateStage.value to MaterialTheme.colorScheme.tertiary
                ))
    ) {
        if (composition == null) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null,
                modifier = Modifier.size(290.dp)
            )
        } else {
            LottieAnimation(
                modifier = Modifier.size(290.dp),
                composition = composition,
                progress = { progress },
            )
            startTextAnimation=true
        }
    }
    HomeTitles(startTextAnimation)
}

@Composable
fun HomeTitles(showLabels: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showLabels,
            enter = fadeIn(animationSpec = tween(1000,delayMillis = 200)) + expandVertically()
        ) {
            Text(
                text = "GHAIMA",
                fontSize = 42.sp,
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        }

        AnimatedVisibility(
            visible = showLabels,
            enter = fadeIn(animationSpec = tween(1000, delayMillis = 800)) +
                    slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Text(
                text = "Hear The Clouds",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = showLabels,
            enter = fadeIn(animationSpec = tween(1000, delayMillis = 1500))
        ) {
            Text(
                text = "By Depogramming",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}