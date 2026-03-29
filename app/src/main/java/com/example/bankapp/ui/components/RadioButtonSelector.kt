package com.example.bankapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.ui.theme.textFieldFontSize


@Composable
fun <T> RadioButtonSelector(
    title: String,
    options: List<T>,
    selected: T,
    onSelectionChange: (T) -> Unit,
    labelFor: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Medium
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = fontWeight,
            color = MaterialTheme.colorScheme.onSurface,
           // fontSize = textFieldFontSize
        )

        LargeSpacer()

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing))
        ) {
            options.forEach {
                option ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectionChange(option)
                        }
                        .padding(dimensionResource(R.dimen.radio_padding))
                ) {
                    RadioButton(
                        selected = option == selected,
                        onClick = {
                            onSelectionChange(option)
                        }
                    )
                    Text(
                        text = labelFor(option),
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.radio_text_padding))
                    )
                }
            }
        }
    }
}