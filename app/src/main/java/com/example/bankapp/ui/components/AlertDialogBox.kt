package com.example.bankapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.bankapp.entities.uientities.uidata.AlertButtonConfig
import com.example.bankapp.entities.uientities.uitypes.AlertButtonStyle

@Composable
fun AlertDialogBox(
    onDismissRequest: () -> Unit,
    confirmButton: AlertButtonConfig,
    dismissButton: AlertButtonConfig? = null,
    content: @Composable () -> Unit,
    title: String? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title?.let {
            { Text(it) }
        },
        text = content,
        confirmButton = {
            when (confirmButton.style) {
                AlertButtonStyle.PRIMARY -> {
                    Button(
                        onClick = {
                            confirmButton.onClick()
                            onDismissRequest()
                        }
                    ) {
                        Text(confirmButton.label)
                    }
                }
                AlertButtonStyle.ERROR -> {
                    Button(
                        onClick = {
                            confirmButton.onClick()
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(confirmButton.label)
                    }
                }
            }
        },
        dismissButton = dismissButton?.let {
            {
                TextButton(
                    onClick = {
                        dismissButton.onClick()
                        onDismissRequest()
                    }
                ) {
                    Text(dismissButton.label)
                }
            }
        }
    )
}

