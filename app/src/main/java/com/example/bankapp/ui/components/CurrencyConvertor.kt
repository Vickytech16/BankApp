package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.Country
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.screens.payscreens.PaySectionCard
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.viewmodels.CurrencyConvertorViewModel

@Composable
fun DynamicCurrencyConverter(
    viewModel: CurrencyConvertorViewModel,
    deviceSpec: DeviceSpec
) {
    val countryList by viewModel.countries.collectAsState()

    PaySectionCard(
        title = stringResource(R.string.currency_converter_label),
        deviceSpec = deviceSpec
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CurrencyPickerRow(
                label = stringResource(R.string.from),
                selectedCountry = viewModel.topCurrency,
                countries = countryList,
                showSheet = viewModel.showTopSheet,
                onShowSheetChange = { viewModel.showTopSheet = it },
                searchQuery = viewModel.topSearchQuery,
                onSearchQueryChange = { viewModel.topSearchQuery = it },
                onSelected = viewModel::onTopCurrencySelected
            )

            UnifiedOutlinedTextField(
                value = viewModel.converterInput,
                onValueChange = viewModel::onConverterInputChange,
                labelText = stringResource(R.string.amount_field_name),
                modifier = Modifier.fillMaxWidth()
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(onClick = { viewModel.onCurrencySwap() }) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert,
                        contentDescription = stringResource(R.string.swap),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            CurrencyPickerRow(
                label = stringResource(R.string.to),
                selectedCountry = viewModel.bottomCurrency,
                countries = countryList,
                showSheet = viewModel.showBottomSheet,
                onShowSheetChange = { viewModel.showBottomSheet = it },
                searchQuery = viewModel.bottomSearchQuery,
                onSearchQueryChange = { viewModel.bottomSearchQuery = it },
                onSelected = viewModel::onBottomCurrencySelected
            )

            MediumSpacer()

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.field_corner_radius)),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(AppSpacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.result),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = viewModel.convertedValue,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyPickerRow(
    label: String,
    selectedCountry: Country?,
    countries: List<Country>,
    showSheet: Boolean,
    onShowSheetChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelected: (Country) -> Unit
) {
    val displayValue = selectedCountry?.let { "${it.emoji} ${it.countryCode}" } ?: "..."

    DropDownPickerField(
        label = label,
        selectedValue = displayValue,
        placeholder = stringResource(R.string.select_currency),
        items = countries,
        onItemSelected = onSelected,
        itemLabel = { it.name },
        itemSecondaryLabel = { it.countryCode },
        itemLeadingIcon = { Text(it.emoji, fontSize = 20.sp) },
        showSheet = showSheet,
        onShowSheetChange = onShowSheetChange,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange
    )
    XSSpacer()
}