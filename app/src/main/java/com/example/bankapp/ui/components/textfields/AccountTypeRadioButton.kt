package com.example.bankapp.ui.components.textfields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.bankapp.entities.types.AccountType

@Composable
fun AccountTypeRadioButton(
    labeltext: String,
    accountType: AccountType,
    onOptionSelectedChange: (AccountType) -> Unit
)
{
    Column {
        Text(labeltext)
        AccountType.entries.forEach {
            it ->
            Row {
                RadioButton(
                    selected = accountType == it,
                    onClick = { onOptionSelectedChange(it) }
                )
                Text(text = it.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }
    }


}