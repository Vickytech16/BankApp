package com.example.bankapp.ui.screens.authscreens

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.ui.components.BackHandlerWithWarning
import com.example.bankapp.ui.components.LargeSpacer
import com.example.bankapp.ui.components.MediumSpacer
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.components.navigators.AUTH_ROUTE
import com.example.bankapp.ui.components.navigators.LOGIN_ROUTE
import com.example.bankapp.ui.components.navigators.REGISTER_SUCCESS_ROUTE
import com.example.bankapp.ui.components.screenModifier
import com.example.bankapp.ui.theme.AppSpacing
import kotlinx.coroutines.launch

@Composable
fun RecoveryKeyDisplayScreen(
    recoveryKey: String,
    navController: NavController
) {
    val clipboard = LocalClipboard.current
    var hasCopied by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    BackHandlerWithWarning(
        onConfirm = {
            navController.navigate(LOGIN_ROUTE) {
                popUpTo(AUTH_ROUTE) {
                    inclusive = false
                }
            }
        },
        onShowDialogConfirm = { showExitDialog = true },
        onDismiss = { showExitDialog = false },
        showDialog = showExitDialog
    )

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier.screenModifier(contentPadding, scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.your_recovery_key),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            LargeSpacer()

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = recoveryKey,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.recovery_field_vertical_padding)),
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LargeSpacer()

            OutlinedButton(
                onClick = {
                    val clipData = ClipData.newPlainText("Recovery Key", recoveryKey)
                    val clipEntry = ClipEntry(clipData)
                    scope.launch {
                        clipboard.setClipEntry(clipEntry)
                    }
                    hasCopied = true
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.copy_content_description)
                )
                MediumSpacer()
                Text(
                    text = if (hasCopied)
                        stringResource(R.string.copied)
                    else
                        stringResource(R.string.copy_to_clipboard)
                )
            }

            XLSpacer()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                val annotatedWarningString = buildAnnotatedString {
                    appendInlineContent(id = "warningIcon", alternateText = "[!]")
                    append("  ")
                    append(stringResource(R.string.copy_recovery_text_warning))
                }

                val inlineContent = mapOf(
                    "warningIcon" to androidx.compose.foundation.text.InlineTextContent(
                        Placeholder(
                            width = 24.sp,
                            height = 24.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )

                Text(
                    text = annotatedWarningString,
                    inlineContent = inlineContent,
                    modifier = Modifier.padding(AppSpacing.lg),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    lineHeight = 22.sp
                )
            }

            XLSpacer()
            XLSpacer()

            Button(
                onClick = {
                    navController.navigate(REGISTER_SUCCESS_ROUTE) {
                        popUpTo(AUTH_ROUTE) {
                            inclusive = true
                        }
                    }
                },
                enabled = hasCopied,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.saved_key_button))
            }

            XLSpacer()
            XLSpacer()
        }
    }
}
