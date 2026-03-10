package com.example.bankapp.ui.screens.authscreens

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bankapp.R


@Composable
fun SplashScreen() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_bank)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val logoAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = EaseInOut),
        label = "logo_fade"
    )

    val contentOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 900, delayMillis = 300, easing = EaseOutCubic),
        label = "content_slide"
    )

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = dimensionResource(R.dimen.splash_logo_top_padding)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bank_logo),
                    contentDescription = stringResource(R.string.app_logo_content_description),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.splash_logo_size))
                        .alpha(logoAlpha)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = with(LocalDensity.current) {
                        dimensionResource(R.dimen.splash_content_slide_distance)
                            .toPx()
                            .times(contentOffset)
                            .toDp()
                    })
                    .padding(horizontal = dimensionResource(R.dimen.splash_horizontal_padding)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.splash_name_quote_spacing))
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.splash_quote),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic
                )
            }

            // Bottom: Lottie animation
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.splash_lottie_height))
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimensionResource(R.dimen.splash_lottie_bottom_padding))
            )
        }
    }
}