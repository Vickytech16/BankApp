package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bankapp.R

@Composable
fun LottieSuccessTick(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit = {}
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_tick)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1f
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            onAnimationFinished()
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    )
}