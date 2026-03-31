package com.example.bankapp.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.FilterViewModelFactory
import com.example.bankapp.entities.types.ui.UiLedgerDirection
import com.example.bankapp.services.ExportType
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.transactionitems.TransactionLazyList
import com.example.bankapp.ui.components.filters.FilterSortChip
import com.example.bankapp.ui.components.filters.FilterSection
import com.example.bankapp.ui.components.filters.SortSection
import com.example.bankapp.ui.components.SearchBarComponent
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.FilterViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactionsViewModel: TransactionsViewModel,
    navController: NavController,
    filterViewModelFactory: FilterViewModelFactory,
    windowSizeClass: WindowSizeClass
) {
    val transactions by transactionsViewModel.transactions.collectAsState()
    val query by transactionsViewModel.query.collectAsState()
    val filterViewModel: FilterViewModel = viewModel(factory = filterViewModelFactory)
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        transactionsViewModel.exportEvent.collect { file ->
            shareFile(context, file)
        }
    }

    //val scrollState = rememberScrollState()

    val isLoading = transactionsViewModel.isLoading.collectAsState().value

   // val deviceSpec = DeviceSpecProvider.getCurrentDeviceSpec(windowSizeClass)

    val deviceSpec = LocalDeviceSpec.current


    LaunchedEffect(filterViewModel.filterState) {
        transactionsViewModel.updateFilters(filterViewModel.filterState)
    }

    LaunchedEffect(filterViewModel.sortState) {
        transactionsViewModel.updateSort(filterViewModel.sortState)
    }

    Scaffold(
        topBar = {
        Appbar(
            stringResource(R.string.transactions),
            navBehaviour = { navController.popBackStack() },
            scrollBehavior = null,
            actions = {
                Box {
                    IconButton(onClick = { transactionsViewModel.onShwoMenuChange(true)  }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Export Options")
                    }
                    DropdownMenu(
                        expanded = transactionsViewModel.showMenu,
                        onDismissRequest = { transactionsViewModel.onShwoMenuChange(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.Export_as_csv)) },
                            onClick = {
                                transactionsViewModel.onShwoMenuChange(false)
                                transactionsViewModel.exportCurrentTransactions(ExportType.CSV)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.Export_as_pdf)) },
                            onClick = {
                                transactionsViewModel.onShwoMenuChange(false)
                                transactionsViewModel.exportCurrentTransactions(ExportType.PDF)
                            }
                        )
                    }
                }
            }
        )
    },
        contentWindowInsets = WindowInsets.systemBars
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            SearchBarComponent(
                query,
                transactionsViewModel::onQueryChange,
                {
                    keyboardController?.hide()
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                {
                    transactionsViewModel.onQueryChange("")
                    keyboardController?.hide()
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.filter_sheet_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing))
            ) {

                FilterSortChip(
                    label = stringResource(R.string.filter_label),
                    icon = Icons.Outlined.FilterList,
                    badgeCount = filterViewModel.filterState.selectedStatus.size +
                            filterViewModel.filterState.selectedTypes.size +
                            if (filterViewModel.filterState.selectedDirection != UiLedgerDirection.BOTH) 1 else 0,
                    showSheet = filterViewModel.filterShowSheet,
                    onShowSheetChange = filterViewModel::onFilterShowSheetChange,
                    sheetContent = {
                        FilterSection(
                            pendingState = filterViewModel.pendingState,
                            onPendingStateChange = filterViewModel::onPendingStateChange,
                            onApply = filterViewModel::onFilterApply,
                            onReset = filterViewModel::onFilterReset,
                            onDismiss = {
                                filterViewModel.onFilterShowSheetChange(false)
                            }
                        )
                    }
                )


                FilterSortChip(
                    label = stringResource(R.string.sort_label),
                    icon = Icons.AutoMirrored.Outlined.Sort,
                    badgeCount = 0,
                    showSheet = filterViewModel.sortShowSheet,
                    onShowSheetChange = filterViewModel::onSortShowSheetChange,
                    sheetContent = {
                        SortSection(
                            selectedSort = filterViewModel.pendingSortState,
                            onSortChange = filterViewModel::onSortChange,
                            onReset = { filterViewModel.onSortReset() },
                            onApply = { filterViewModel.onSortApply() }
                        )
                    }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (!isLoading && transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_transactions_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TransactionLazyList(
                transactions = transactions,
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                navController = navController,
                deviceSpec = deviceSpec,
                countryCode = transactionsViewModel.countryCode
            )
        }
    }
}

fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Bank Statement"))
}


fun String.uiAmountDisplay(): String {
    val parts = this.split(".")

    if (parts.size < 2) {
        return this
    }

    val wholePart = parts[0]
    val decimalPart = parts[1]

    return if (decimalPart.all { it == '0' }) {
        wholePart
    } else {
        this
    }
}