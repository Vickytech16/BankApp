package com.example.bankapp.ui.components.navigators

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.bankapp.entities.SessionState
import com.example.bankapp.ui.screens.authscreens.SplashScreen
import com.example.bankapp.viewmodels.SessionViewModel
import kotlinx.coroutines.delay

@Composable
fun NavStarter(
    sessionViewModel: SessionViewModel,
    content: @Composable () -> Unit
) {
    val sessionState by sessionViewModel.sessionState.collectAsState()

    var isMinimumTimeElapsed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000)
        isMinimumTimeElapsed = true
    }

    val shouldShowContent = sessionState !is SessionState.Loading && isMinimumTimeElapsed

    AnimatedContent(
        targetState = shouldShowContent,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith
                    fadeOut(animationSpec = tween(500))
        },
        label = "SplashToNavTransition"
    ) { targetReady ->
        if (targetReady) {
            content()
        } else {
            SplashScreen()
        }
    }
}