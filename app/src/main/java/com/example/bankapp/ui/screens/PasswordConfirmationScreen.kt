package com.example.bankapp.ui.screens

import androidx.activity.compose.BackHandler


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.PasswordConfirmationViewModelFactory
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.navigators.TRANSACTION_RESULT_ROUTE
import com.example.bankapp.ui.components.textfields.PasswordVerificationOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.usecases.TransactionSessionHolder
import com.example.bankapp.viewmodels.PasswordConfirmationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordConfirmationScreen(
    navController: NavController,
    passwordConfirmationViewModelFactory: PasswordConfirmationViewModelFactory,
    onDismissRoute: String,
    onPasswordVerificationSuccess: () -> Unit
) {
    val viewModel: PasswordConfirmationViewModel =
        viewModel(factory = passwordConfirmationViewModelFactory)

    BackHandler(enabled = true) {
        if (viewModel.showPasswordDialog) {
            viewModel.onPasswordDialogDismiss()
        } else {
            navController.navigate(onDismissRoute) {
                popUpTo(onDismissRoute) {
                    inclusive = false
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetScreenState()
        }
    }

    DisposableEffect(Unit) {
        viewModel.onShowPasswordDialogChange(true)
        onDispose {}
    }

    LaunchedEffect(viewModel.isPasswordVerified) {
        if(viewModel.isPasswordVerified) {
            onPasswordVerificationSuccess()
            navController.navigate(TRANSACTION_RESULT_ROUTE) {
                popUpTo(onDismissRoute) {
                    inclusive = false
                }
            }
        }
    }

    if (viewModel.showPasswordDialog) {
        PasswordVerificationDialog(
            onDismiss = {
                viewModel.onShowPasswordDialogChange(false)
                viewModel.onShowCancelDialogChange(true)
            },
            onSubmit = { password ->
                viewModel.onSubmit(password)
            },
            isLoading = viewModel.isLoading,
            password = viewModel.password,
            onPasswordChange = viewModel::onPasswordChange,
            passwordVisible = viewModel.passwordVisible,
            passwordError = viewModel.passwordError,
            onPasswordVisibleChange = viewModel::onPasswordVisibleChange,
            submitError = viewModel.submitError
        )
    }

    if (viewModel.showCancelDialog) {
        ConfirmCancellationDialog(
            onDismiss = {
                viewModel.onShowCancelDialogChange(false)
                viewModel.onShowPasswordDialogChange(true)
            },
            onConfirm = {
                viewModel.onShowCancelDialogChange(false)
                viewModel.resetSession()
                navController.navigate(onDismissRoute) {
                    popUpTo(onDismissRoute) {
                        inclusive = false
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(R.string.verify_pass),
                navBehaviour = {
                    if (viewModel.showPasswordDialog) {
                        viewModel.onShowPasswordDialogChange(false)
                        viewModel.onShowCancelDialogChange(true)
                    } else {
                        navController.navigate(onDismissRoute) {
                            popUpTo(onDismissRoute) {
                                inclusive = false
                            }
                        }
                    }
                },
                scrollBehavior = null
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!viewModel.showPasswordDialog && !viewModel.showCancelDialog) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ConfirmCancellationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.cancel_transaction)) },
        text = { Text(stringResource(R.string.dismissing_Cancel_transaction_message)) },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.yes_cancel_confirmation))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.continue_label))
            }
        }
    )
}

@Composable
fun PasswordVerificationDialog(
    onDismiss:  () -> Unit,
    onSubmit: (password: String) -> Unit,
    isLoading: Boolean = false,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordError: FormError?,
    onPasswordVisibleChange: () -> Unit,
    submitError: FormError? = null

) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.verify_pass)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                PasswordVerificationOutlinedTextField(
                    password = password,
                    onPasswordChange = onPasswordChange,
                    passwordVisible = passwordVisible,
                    passwordError = passwordError,
                    onPasswordVisibleChange = onPasswordVisibleChange
                )

                if (submitError != null) {
                    ErrorTextBuilder(submitError)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(password) },
                enabled = password.isNotEmpty() && !isLoading
            ) {
                Text(stringResource(R.string.submit_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}