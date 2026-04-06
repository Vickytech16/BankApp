package com.example.bankapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.entities.uientities.uidata.AlertButtonConfig
import com.example.bankapp.entities.uientities.uitypes.AlertButtonStyle

@Composable
fun BackHandlerWithWarning(
    onConfirm: () -> Unit,
    onShowDialogConfirm: () -> Unit,
    onDismiss: () -> Unit,
    showDialog: Boolean,
    title: String = stringResource(R.string.warning_title),
    message: String = stringResource(R.string.back_button_warning_default),
    confirmLabel: String = stringResource(R.string.yes),
    dismissLabel: String = stringResource(R.string.no)
) {
    BackHandler(enabled = true) {
       onShowDialogConfirm()
    }

    if (showDialog) {
        AlertDialogBox(
            onDismissRequest = { onDismiss() },
            title = title,
            content = { Text(message) },
            confirmButton = AlertButtonConfig(
                label = confirmLabel,
                style = AlertButtonStyle.ERROR,
                onClick = onConfirm
            ),
            dismissButton = AlertButtonConfig(
                label = dismissLabel,
                onClick = {
                    onDismiss()
                }
            )
        )
    }
}