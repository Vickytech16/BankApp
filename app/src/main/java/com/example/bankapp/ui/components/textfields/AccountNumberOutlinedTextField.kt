package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.theme.textFieldLabelFontSize
import com.example.bankapp.utilities.ACCOUNTNUMBERFIELDNAME

@Composable
fun AccountNumberOutlinedTextField(
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    accountNumberError: FormError? ){
    OutlinedTextField(
        value = accountNumber,
        onValueChange = onAccountNumberChange,
        label = {
            Text(
                ACCOUNTNUMBERFIELDNAME,
                fontSize = textFieldLabelFontSize)
        },
        singleLine = true,
        isError = accountNumberError != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        supportingText = {
            ErrorTextBuilder(accountNumberError)
        },
        leadingIcon = {
            Icon(Icons.Outlined.AccountBox, contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth()
    )
}