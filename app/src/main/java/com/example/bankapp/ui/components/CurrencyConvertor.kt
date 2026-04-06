package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.Country
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.entities.uientities.uidata.AmountFieldStrategy
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.screens.payscreens.PaySectionCard
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.viewmodels.CurrencyConvertorViewModel

@Composable
fun CurrencyConverter(
    viewModel: CurrencyConvertorViewModel,
    deviceSpec: DeviceSpec
) {
    val countryList by viewModel.countries.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
    ) {
        Text(
            text = stringResource(R.string.currency_converter_label),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppSpacing.lg)
        )

        CurrencyPickerRow(
            label = stringResource(R.string.from),
            selectedCountry = viewModel.topCurrency,
            countries = countryList,
            showSheet = viewModel.showTopSheet,
            onShowSheetChange = { viewModel.showTopSheet = it },
            searchQuery = viewModel.topSearchQuery,
            onSearchQueryChange = { viewModel.ontOpSearchQueryChange(it) },
            onSelected = viewModel::onTopCurrencySelected
        )

        UnifiedOutlinedTextField(
            value = viewModel.converterInput,
            onValueChange = viewModel::onConverterInputChange,
            labelText = stringResource(R.string.amount_field_name),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (viewModel.converterInput.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onConverterInputChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            strategy = AmountFieldStrategy(transactionType = TransactionType.DEPOSIT),
            showTickCondition = {false}
        )

        MediumSpacer()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.xs),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { viewModel.onCurrencySwap() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                )
            ) {
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
            onSearchQueryChange = { viewModel.onBottomSearchQueryChange(it) },
            onSelected = viewModel::onBottomCurrencySelected
        )

        MediumSpacer()

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.field_corner_radius)),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(AppSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.result),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = viewModel.convertedValue,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                viewModel.bottomCurrency?.let {
                    Text(
                        text = CurrencyUtils.getCurrencySymbol(it.countryCode),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        if(viewModel.lastUpdated != null) {
            Text(
                text = "${stringResource(R.string.last_updated_label)} ${viewModel.lastUpdated}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.sm, end = AppSpacing.xs)
            )
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
    val displayValue = selectedCountry?.let { "${it.emoji}  ${CurrencyUtils.getCurrencyCode(it.countryCode)}" } ?: "..."

    DropDownPickerField(
        label = label,
        selectedValue = displayValue,
        placeholder = stringResource(R.string.select_currency),
        items = countries,
        onItemSelected = onSelected,
        itemLabel = { it.name },
        itemSecondaryLabel = { CurrencyUtils.getCurrencyCode(it.countryCode) },
        itemLeadingIcon = { Text(it.emoji, fontSize = 20.sp) },
        showSheet = showSheet,
        onShowSheetChange = onShowSheetChange,
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        leadingIcon = {Icon(imageVector = Icons.Outlined.Public, contentDescription = null)},
        searchPlaceholder = stringResource(R.string.currency_field_name),
        tickCondition = false
    )
    XSSpacer()
}