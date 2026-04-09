package com.example.bankapp.ui.components.authenticationItems

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bankapp.R
import com.example.bankapp.services.BiometricAuthenticator
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.viewmodels.authviewmodels.RootAuthViewModel

@Composable
fun RootAuthWrapper(
    activity: FragmentActivity,
    authViewModel: RootAuthViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val authenticator = remember { BiometricAuthenticator(activity) }
    val configuration = LocalConfiguration.current

    val lockedOrientation = remember {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.setDeviceSecure(authenticator.canAuthenticate())
    }

    DisposableEffect(uiState.isAuthenticated, uiState.isDeviceSecure) {
        if (uiState.isDeviceSecure && !uiState.isAuthenticated) {
            activity.requestedOrientation = lockedOrientation
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (authViewModel.canPrompt()) {
            authViewModel.markPromptActive(true)
            authenticator.promptBiometricAuth(activity) { result ->
                if (result is BiometricAuthenticator.AuthResult.Success) {
                    authViewModel.setAuthenticated(true)
                } else {
                    authViewModel.markPromptActive(false)
                }
            }
        }
    }

    if (uiState.isAuthenticated) {
        content()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.auth_lock_icon_size))
                )
                LargeSpacer()
                Text(
                    text = stringResource(id = R.string.auth_locked_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                XLSpacer()
                Button(onClick = {
                    authenticator.promptBiometricAuth(activity) { result ->
                        if (result is BiometricAuthenticator.AuthResult.Success) {
                            authViewModel.setAuthenticated(true)
                        }
                    }
                }) {
                    Text(text = stringResource(id = R.string.auth_unlock_button))
                }
            }
        }
    }
}