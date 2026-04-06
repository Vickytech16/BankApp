package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.theme.AppSpacing

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.example.bankapp.ui.theme.textFieldFontSize
import com.example.bankapp.utilities.ACCOUNT_NUMBER_SIZE

@Composable
fun AccountNumberOutlinedTextField(
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    accountNumberError: FormError?
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(formatAccountNumber(accountNumber)))
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text(
            text = stringResource(R.string.account_number_label),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                newValue ->
                val digitsOnly = newValue.text.replace(" ", "")
                if (digitsOnly.length <= ACCOUNT_NUMBER_SIZE) {
                    onAccountNumberChange(digitsOnly)
                    val formatted = formatAccountNumber(digitsOnly)
                    val cursorPosition = minOf(newValue.selection.start + 1, formatted.length)
                    textFieldValue = TextFieldValue(formatted, TextRange(cursorPosition))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.amount_textfield_height)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.amount_input_corner_radius)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(
                fontSize = textFieldFontSize,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            ),
            isError = accountNumberError != null,
            singleLine = true
        )

        if (accountNumberError != null) {
            ErrorTextBuilder(accountNumberError)
        }
    }
}

private fun formatAccountNumber(accountNumber: String): String {
    return accountNumber
        .take(12)
        .chunked(4)
        .joinToString(" ")
}