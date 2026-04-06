package com.example.bankapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bankapp.R
import kotlinx.coroutines.delay


@Composable
fun SuccessConfirmation(
    title: String,
    subtitle: String? = null,
    onDone: () -> Unit
) {
    var animationFinished by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = true) {}

    LaunchedEffect(animationFinished) {
        if (animationFinished) {
            delay(1000)
            onDone()
        }
    }

    val scrollState = rememberScrollState()

    Scaffold {
        contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            LottieSuccessTick(
                modifier = Modifier.fillMaxWidth(0.7f),
                onAnimationFinished = {
                    animationFinished = true
                }
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.success_spacer_height)))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )

            subtitle?.let {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.success_subtitle_spacing)))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



@Composable
private fun LottieSuccessTick(
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