package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.ui.components.LargeSpacer

@Composable
fun <T> FilterItem(
    title: String,
    options: Set<T>,
    selected: Set<T>,
    onClick: (T) -> Unit,
    labelFor: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LargeSpacer()

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing))
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option in selected,
                    onClick = { onClick(option) },
                    label = { Text(labelFor(option)) }
                )
            }
        }
    }
}