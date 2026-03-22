package com.example.bankapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.bankapp.R
import com.example.bankapp.ui.components.LottieSuccessTick
import com.example.bankapp.ui.theme.AppPadding
import kotlinx.coroutines.delay


@Composable
fun SuccessConfirmation(
    title: String,
    subtitle: String? = null,
    onDone: () -> Unit
) {
    var animationFinished by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(animationFinished) {
        if (animationFinished) {
            delay(1000)
            onDone()
        }
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
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