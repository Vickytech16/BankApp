package com.example.bankapp.ui.screens.authscreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.core.notificationPermissionHandler
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.entities.Notification
import com.example.bankapp.services.SendNotificationService
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.OtpInputField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.viewmodels.NotificationViewmodel
import com.example.bankapp.viewmodels.OtpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OtpScreen(
    navController: NavController,
    notificationViewModelFactory: NotificationViewModelFactory,
    otpViewModelFactory: OtpViewModelFactory,
    backRoute: String,
    onOtpSuccess: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    val notificationViewModel: NotificationViewmodel =
        viewModel(factory = notificationViewModelFactory)

    val otpViewModel: OtpViewModel =
        viewModel(factory = otpViewModelFactory)

    val context = LocalContext.current

    val title = stringResource(R.string.your_otp)
    val message = stringResource(R.string.otp_field_name)

    BackButtonHandler(navController, backRoute, onDismiss)

    val sendOtp = notificationPermissionHandler(
        {
            sendOtp(
                notificationViewModel = notificationViewModel,
                otpViewModel = otpViewModel,
                context = context,
                title = title,
                message = message
            )
        },
        {
            notificationViewModel.onPermissionDenied()
        }
    )

    LaunchedEffect(Unit) {
        sendOtp()
    }

    LaunchedEffect(notificationViewModel.hasPermissionBeenRequested) {
        if (notificationViewModel.hasPermissionBeenRequested && !notificationViewModel.isPermissionGranted) {
            val isNowGranted = checkPermission(context)
            if (isNowGranted) {
                sendOtp(
                    notificationViewModel = notificationViewModel,
                    otpViewModel = otpViewModel,
                    context = context,
                    title = title,
                    message = message
                )
            }
        }
    }

    LaunchedEffect(otpViewModel.isOtpValid) {
        if (otpViewModel.isOtpValid == true) {
            onOtpSuccess()
        }
    }


    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(Unit) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (notificationViewModel.hasPermissionBeenRequested && !notificationViewModel.isPermissionGranted) {
                    val isNowGranted = checkPermission(context)
                    if (isNowGranted) {
                        sendOtp(
                            notificationViewModel = notificationViewModel,
                            otpViewModel = otpViewModel,
                            context = context,
                            title = title,
                            message = message
                        )
                    }
                }
            }
        }

        lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            otpViewModel.resetOtpState()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Appbar(
                title = "",
                navBehaviour = {
                    navController.navigate(backRoute) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                scrollBehavior = null
            )
        }
    ) { paddingValues ->
        if (!notificationViewModel.isPermissionGranted && notificationViewModel.hasPermissionBeenRequested) {
            NotificationPermissionScreen(
                modifier = Modifier.padding(paddingValues),
            )
        } else if (notificationViewModel.isPermissionGranted) {
            OtpInputScreen(
                otpViewModel,
                paddingValues, {
                    sendOtp(
                        notificationViewModel = notificationViewModel,
                        otpViewModel = otpViewModel,
                        context = context,
                        title = title,
                        message = message
                    )
                }
            )
        }
    }
}

@Composable
fun OtpInputScreen(
    otpViewModel: OtpViewModel,
    paddingValues: PaddingValues,
    resend: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            text = stringResource(R.string.otp_sent_message),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = stringResource(R.string.otp_instruction_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        XLSpacer()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
            contentAlignment = Alignment.Center
        ) {
            OtpInputField(
                otpInputs = otpViewModel.otpInputs,
                isError = otpViewModel.isOtpValid == false,
                onOtpChange = otpViewModel::onOtpInputChange
            )
        }

        if (otpViewModel.isOtpValid == false) {
            ErrorTextBuilder(otpViewModel.submitError)
        }

        SubmitButton(
            onClick = {
                otpViewModel.submitOtp()
            },
            text = stringResource(R.string.verify_button),
            enabled = otpViewModel.otpInputs.all {
                it.isNotEmpty()
            },
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding))
        )

        LargeSpacer()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.didnt_receive_code),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (otpViewModel.otpExpiresAt > 0) {
                Text(
                    text = stringResource(R.string.resend_in_formatted, otpViewModel.otpExpiresAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = AppSpacing.sm)
                )
            } else {
                TextButton(
                    onClick = {
                        resend()
                    },
                    modifier = Modifier.padding(start = AppSpacing.sm)
                ) {
                    Text(stringResource(R.string.resend_otp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun NotificationPermissionScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        ) {
            Text(stringResource(R.string.enable_notifications_title))
        }
    }
}

private fun checkPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun sendOtp(
    notificationViewModel: NotificationViewmodel,
    otpViewModel: OtpViewModel,
    context: Context,
    title: String,
    message: String
) {
    notificationViewModel.onPermissionGranted()
    otpViewModel.generateOtp()
    val otp = otpViewModel.generatedOtp

    SendNotificationService.showNotification(
        context,
        Notification(
            title = title,
            message = "$message $otp"
        )
    )
}