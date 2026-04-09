package com.example.bankapp.ui.screens.authscreens

import androidx.compose.ui.res.stringResource
import com.example.bankapp.viewmodels.authviewmodels.RecoveryKeyViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.RecoveryKeyViewModelFactory
import com.example.bankapp.ui.components.BackHandlerWithWarning
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.buttons.SubmitButton
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec

import com.example.bankapp.ui.theme.LocalDeviceSpec

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryKeyVerificationScreen(
    recoveryKeyViewModelFactory: RecoveryKeyViewModelFactory,
    navController: NavController,
    onSuccess: ()->Unit,
    backRoute: String,
    popUpRoute: String,
) {
    val viewModel: RecoveryKeyViewModel = viewModel(factory = recoveryKeyViewModelFactory)
    val deviceSpec = LocalDeviceSpec.current
    val scrollState = rememberScrollState()
    val textFieldColumnWidth = deviceSpec.textFieldWidth

    val scrollBehavior = if (deviceSpec is DeviceSpec.MobileLandscape) {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }
    else {
        null
    }


    if(viewModel.isVerified){
        onSuccess()
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandlerWithWarning(
        onConfirm = {
            navController.navigate(backRoute) {
                popUpTo(popUpRoute) {
                    inclusive = false
                }
            }
        },
        onShowDialogConfirm = { showExitDialog = true},
        onDismiss = { showExitDialog = false },
        showDialog = showExitDialog
    )

    Scaffold(
        contentWindowInsets = if (deviceSpec is DeviceSpec.MobileLandscape) {
            WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
        }
        else {
            ScaffoldDefaults.contentWindowInsets
        },
        modifier = Modifier.then(
            if (scrollBehavior != null) {
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            }
            else {
                Modifier
            }
        ),
        topBar = {
            RegularAppBar(
                title = stringResource(R.string.verify_recoverykey),
                navBehaviour = { showExitDialog = true },
                scrollBehavior = scrollBehavior
            )
        },
    ) {
        contentPadding ->
        Column(
            modifier = Modifier.screenModifier(contentPadding, scrollState).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(textFieldColumnWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.enter_recovery_key),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                MediumSpacer()

                Text(
                    text = stringResource(R.string.enter_recovery_key_content),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                XLSpacer()

                UnifiedOutlinedTextField(
                    value = viewModel.recoveryKey,
                    onValueChange = viewModel::onKeyChange,
                    labelText = stringResource(R.string.recovery_key_label),
                    isError = viewModel.recoveryKeyError != null,
                    supportingText = {
                        ErrorTextBuilder(viewModel.recoveryKeyError)
                    },
                    showTickCondition = {false}
                )

                XLSpacer()

                SubmitButton(
                    onClick = { viewModel.onSubmit() },
                    isLoading = viewModel.isLoading,
                    text = stringResource(R.string.verify_button)
                )

                MediumSpacer()

                Text(
                    text = stringResource(R.string.verify_key_disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppSpacing.lg)
                )
                XLSpacer()
            }
        }
    }
}