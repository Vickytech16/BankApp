package com.example.bankapp.ui.screens.authscreens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.core.ShowDialogOnPermissionDenial
import com.example.bankapp.core.notificationPermissionHandler
import com.example.bankapp.di.viewmodelfactory.NotificationViewModelFactory
import com.example.bankapp.di.viewmodelfactory.OtpViewModelFactory
import com.example.bankapp.entities.Notification
import com.example.bankapp.services.SendNotificationService
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.textfields.GenericOutlinedTextField
import com.example.bankapp.viewmodels.NotificationViewmodel
import com.example.bankapp.viewmodels.OtpViewModel


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OtpScreen(
    navController: NavController,
    notificationViewModelFactory: NotificationViewModelFactory,
    otpViewModelFactory: OtpViewModelFactory,
    onSuccessfulOtpVerification: () -> Unit
) {
    val notificationViewModel: NotificationViewmodel =
        viewModel(factory = notificationViewModelFactory)

    val otpViewModel: OtpViewModel =
        viewModel(factory = otpViewModelFactory)

    val context = LocalContext.current

    BackButtonHandler(navController, LOGIN_ROUTE)

    val title = stringResource(R.string.your_otp)
    val message = stringResource(R.string.otp_field_name)

    val sendOtp = notificationPermissionHandler(
        {
            otpViewModel.generateOtp()
            val otp = otpViewModel.generatedOtp

            SendNotificationService.showNotification(
                context,
                Notification(
                    title = title,
                    message = "$message $otp"
                )
            )
        },
        { notificationViewModel.onPermissionDeniedChange(true) }
    )

    if(notificationViewModel.isPermissionDenied && !notificationViewModel.isPermissionDeniedByDialogBox){
        notificationViewModel.onIsPermissionDeniedByDialogBoxChange(true)
        ShowDialogOnPermissionDenial(context, notificationViewModel::onPermissionDeniedChange) {
            navController.navigate(LOGIN_ROUTE) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(notificationViewModel.isPermissionDenied) {
        if (!notificationViewModel.isPermissionDenied && !otpViewModel.isOtpSent) {
            sendOtp()
        }
    }

    LaunchedEffect(otpViewModel.isOtpValid) {
        if (otpViewModel.isOtpValid == true) {
            onSuccessfulOtpVerification()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            otpViewModel.resetOtpState()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Button(
                onClick = { sendOtp() },
                enabled = otpViewModel.otpExpiresAt == 0
            ) {
                if (otpViewModel.otpExpiresAt == 0)
                    Text(stringResource(R.string.resend_otp))
                else
                    Text("${otpViewModel.otpExpiresAt}")
            }

            GenericOutlinedTextField(
                value = otpViewModel.userEnteredOtp,
                onValueChange = otpViewModel::onUserEnteredOtpChange,
                labelText = stringResource(R.string.otp_field_name),
                isError = otpViewModel.isOtpValid == false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
              leadingIcon = Icons.Outlined.Numbers
            )

            SubmitButton(
                onClick = {otpViewModel.submitOtp()},
                text = stringResource(R.string.submit_button),
                enabled = otpViewModel.userEnteredOtp.length == otpViewModel.otpLength
            )

            ErrorTextBuilder(otpViewModel.submitError)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    title: String,
    text: String,
    onConfirm:() -> Unit,
    onDismiss: () -> Unit
){
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = { Text(title) },
            text = {
                Text(text)
            },
            confirmButton = {
                TextButton(onClick = {
                   onConfirm()
                }) {
                    Text(stringResource(R.string.go_to_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()}) {
                    Text(stringResource(R.string.not_now))
                }
            }
        )
}


//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun OtpScreen(
//   otpViewModel: OtpViewModel
//) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//
//            OutlinedTextField(
//                value = otpViewModel.userEnteredOtp,
//                onValueChange = otpViewModel::onUserEnteredOtpChange,
//                label = { Text("Enter OTP") },
//                isError = otpViewModel.isOtpValid == false,
//                keyboardOptions = KeyboardOptions(
//                    keyboardType = KeyboardType.NumberPassword
//                ),
//                singleLine = true
//            )
//
//            if (otpViewModel.isOtpValid == false) {
//                Text(
//                    text = "Incorrect OTP. Please try again.",
//                    color = MaterialTheme.colorScheme.error,
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//
//            Button(
//                onClick = {
//                    otpViewModel.userEnteredOtp
//                        .toIntOrNull()
//                        ?.let { otpViewModel.submitOtp() }
//                },
//                enabled = otpViewModel.userEnteredOtp.length == otpViewModel.otpLength
//            ) {
//                Text("Submit")
//            }
//        }
//}

//
//@Composable
//fun OtpTextField(
//    otpViewModel: OtpViewModel = hiltViewModel()
//) {
//    val focusRequester = remember { FocusRequester() }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { focusRequester.requestFocus() }
//    ) {
//        BasicTextField(
//            value = otpViewModel.userEnteredOtp,
//            onValueChange = { value ->
//                if (value.length <= otpViewModel.otpLength && value.all { it.isDigit() }) {
//                    otpViewModel.onUserEnteredOtpChange(value)
//                }
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier
//                .matchParentSize()
//                .focusRequester(focusRequester),
//            textStyle = LocalTextStyle.current.copy(color = Color.Transparent),
//            cursorBrush = SolidColor(Color.Transparent),
//            decorationBox = {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    repeat(otpViewModel.otpLength) { index ->
//                        OtpCharBox(
//                            index = index,
//                            text = otpViewModel.userEnteredOtp
//                        )
//                    }
//                }
//            }
//        )
//    }
//}
//
//@Composable
//private fun OtpCharBox(
//    index: Int,
//    text: String
//) {
//    val isFocused = index == text.length
//
//    Box(
//        modifier = Modifier
//            .aspectRatio(1f)
//            .border(
//                1.dp,
//                if (isFocused) MaterialTheme.colorScheme.primary
//                else MaterialTheme.colorScheme.outline,
//                RoundedCornerShape(8.dp)
//            ),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text.getOrNull(index)?.toString() ?: "",
//            fontSize = 20.sp
//        )
//    }
//}



//@Composable
//fun OtpSubmitField(
//    otpViewModel: OtpViewModel = hiltViewModel()
//) {
//    val focusRequester = remember { FocusRequester() }
//
//    Box(
//        modifier = Modifier
//            .wrapContentWidth()
//            .clickable { focusRequester.requestFocus() } // 👈 important
//    ) {
//
//        BasicTextField(
//            value = otpViewModel.userEnteredOtp,
//            onValueChange = otpViewModel::onUserEnteredOtpChange,
//            keyboardOptions = KeyboardOptions(
//                keyboardType = KeyboardType.Number
//            ),
//            modifier = Modifier
//                .matchParentSize() // 👈 IMPORTANT
//                .focusRequester(focusRequester),
//            textStyle = LocalTextStyle.current.copy(color = Color.Transparent), // hide text
//            cursorBrush = SolidColor(Color.Transparent) // hide cursor
//        )
//
//        @Composable
//        fun OtpBoxes(
//            otp: String,
//            otpLength: Int,
//            isError: Boolean
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                repeat(otpLength) { index ->
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)                 // 👈 responsive width
//                            .aspectRatio(1f)            // 👈 keeps square shape
//                            .border(
//                                width = 1.dp,
//                                color = when {
//                                    isError -> MaterialTheme.colorScheme.error
//                                    index == otp.length -> MaterialTheme.colorScheme.primary
//                                    else -> MaterialTheme.colorScheme.outline
//                                },
//                                shape = RoundedCornerShape(8.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = otp.getOrNull(index)?.toString() ?: "",
//                            fontSize = 20.sp
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun OtpTestScreen(
//    notificationViewModelFactory: NotificationViewModelFactory,
//    otpViewModelFactory: OtpViewModelFactory,
//    onSuccessfulOtpVerification: () -> Unit
//) {
//    val notificationViewmodel: NotificationViewmodel = viewModel(factory = notificationViewModelFactory)
//    val otpViewModel: OtpViewModel = viewModel(factory = otpViewModelFactory)
//    val context = LocalContext.current
//
//
//    val sendOtp =  notificationPermissionHandler(
//        {
//            otpViewModel.generateOtp()
//            val otp = otpViewModel.generatedOtp
//            SendNotificationService.showNotification(
//                context,
//                Notification(
//                    title = "Your OTP",
//                    message = "OTP: $otp"
//                )
//            )
//        },
//        {
//            notificationViewmodel.onPermissionDeniedChange(true)
//        }
//    )
//
//
//
//    LaunchedEffect(Unit) {
//        if(!otpViewModel.isOtpSent)
//            sendOtp()
//    }
//
//    if(notificationViewmodel.isPermissionDenied){
//        showDialogOnPermissionDenial(
//            "Enable Notifications.",
//            "We require you to enable notifications in settings so we can send you OTP",
//            {
//                val intent =
//                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
//                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//                    }
//                context.startActivity(intent)
//                notificationViewmodel.onPermissionDeniedChange(false)
//            },
//            {
//                notificationViewmodel.onPermissionDeniedChange(false)
//            }
//        )
//    }
//
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize()
//    ) {
//            paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//
//            Button(
//                onClick = sendOtp,
//                enabled = !otpViewModel.isOtpSent
//            ) {
//                if (otpViewModel.generatedOtp == null)
//                    Text("Send OTP")
//                else
//                    Text("Resend in ${otpViewModel.otpExpiresAt}s")
//            }
//
//            if (otpViewModel.isOtpSent) {
//                OtpScreen(otpViewModel)
//            }
//
//            if (otpViewModel.isOtpValid == false) {
//                Text(
//                    "Enter valid otp.",
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            otpViewModel.resetOtpState()
//        }
//    }
//}
