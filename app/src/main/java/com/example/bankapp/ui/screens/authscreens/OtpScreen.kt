package com.example.bankapp.ui.screens.authscreens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.entities.Notification
import com.example.bankapp.services.SendNotificationService
import com.example.bankapp.ui.components.*
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.textfields.OtpInputField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.viewmodels.NotificationViewmodel
import com.example.bankapp.viewmodels.authviewmodels.OtpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    navController: NavController,
    notificationViewModelFactory: NotificationViewModelFactory,
    otpViewModelFactory: OtpViewModelFactory,
    backRoute: String,
    onOtpSuccess: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    val otpViewModel: OtpViewModel = viewModel(factory = otpViewModelFactory)
    val notificationViewModel: NotificationViewmodel = viewModel(factory = notificationViewModelFactory)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val title = stringResource(R.string.your_otp)
    val message = stringResource(R.string.otp_field_name)

    BackButtonHandler(navController, backRoute)

    var permissionGranted by remember { mutableStateOf(checkPermission(context)) }

    val triggerPermissionRequest = notificationPermissionHandler(
        onPermissionGranted = {
            permissionGranted = true
            notificationViewModel.onPermissionResult()
        },
        onPermissionDenied = {
            permissionGranted = false
            notificationViewModel.onPermissionResult()
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionGranted = checkPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            otpViewModel.resetOtpState()
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            triggerPermissionRequest()
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            otpViewModel.startOtpProcess {
                sendOtp(otpViewModel, context, title, message)
            }
        }
    }

    LaunchedEffect(otpViewModel.isOtpValid) {
        if (otpViewModel.isOtpValid == true) {
            onOtpSuccess()
        }
    }

    Scaffold(
        topBar = {
            Appbar(title = "", navBehaviour = { navController.popBackStack() }, scrollBehavior = null)
        }
    ) { paddingValues ->
        if (!permissionGranted) {
            NotificationPermissionScreen(
                modifier = Modifier.padding(paddingValues),
                onEnableClick = {
                    triggerPermissionRequest()
                    if (!checkPermission(context)) {
                        openAppSettings(context)
                    }
                }
            )
        } else {
            OtpInputScreen(
                otpViewModel = otpViewModel,
                paddingValues = paddingValues,
                resend = { sendOtp(otpViewModel, context, title, message) }
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
            .screenModifier(paddingValues, rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        XLSpacer()
        Text(
            text = stringResource(R.string.otp_sent_message),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.otp_instruction_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        XLSpacer()
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.screen_padding)),
            contentAlignment = Alignment.Center
        ) {
            OtpInputField(
                otpInputs = otpViewModel.otpInputs,
                isError = otpViewModel.isOtpValid == false,
                onOtpChange = otpViewModel::onOtpInputChange
            )
        }

            ErrorTextBuilder(otpViewModel.submitError)

        SubmitButton(
            onClick = { otpViewModel.submitOtp() },
            text = stringResource(R.string.verify_button),
            enabled = otpViewModel.otpInputs.all { it.isNotEmpty() } && otpViewModel.isOtpSent,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding))
        )
        LargeSpacer()
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.screen_padding)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.didnt_receive_code), style = MaterialTheme.typography.bodyMedium)
            if (otpViewModel.otpExpiresAt > 0) {
                Text(
                    text = stringResource(R.string.resend_in_formatted, otpViewModel.otpExpiresAt),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = AppSpacing.sm)
                )
            } else {
                TextButton(onClick = resend) {
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
    onEnableClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize().padding(dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.enable_notifications_title), style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        MediumSpacer()
        Text(text = stringResource(R.string.enable_notifications_text), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        XLSpacer()
        Button(onClick = onEnableClick, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(stringResource(R.string.enable_button_text))
        }
        TextButton(onClick = { openAppSettings(context) }) {
            Text(stringResource(R.string.open_settings_manually))
        }
    }
}

private fun checkPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else true
}

private fun openAppSettings(context: Context) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }
}

private fun sendOtp(otpViewModel: OtpViewModel, context: Context, title: String, message: String) {
    otpViewModel.generateOtp()
    SendNotificationService.showOtpNotification(
        context,
        Notification(title = title, message = "$message ${otpViewModel.generatedOtp}")
    )
}

@Composable
private fun notificationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
): () -> Unit {

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    return {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onPermissionGranted()
        } else {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (isGranted) {
                onPermissionGranted()
            } else {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}