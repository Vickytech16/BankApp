package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.bankapp.entities.errors.FormError
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.theme.textFieldLabelFontSize

@Composable
fun AmountOutlinedTextField(
    amount: String,
    onAmountChange: (String) -> Unit,
    fieldName: String,
    amountError: FormError?
)
{
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        label = {
            Text(fieldName,
                fontSize = textFieldLabelFontSize
                )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        supportingText = {
            ErrorTextBuilder(amountError)
        },
        leadingIcon = {
            Icon(Icons.Outlined.CurrencyRupee, contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth()
    )
}