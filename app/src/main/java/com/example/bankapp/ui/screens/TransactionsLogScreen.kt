package com.example.bankapp.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.FilterViewModelFactory
import com.example.bankapp.entities.uientities.uitypes.UiLedgerDirection
import com.example.bankapp.entities.types.transaction.ExportType
import com.example.bankapp.ui.components.transactionitems.TransactionLazyList
import com.example.bankapp.ui.components.filters.FilterSortChip
import com.example.bankapp.ui.components.filters.FilterSection
import com.example.bankapp.ui.components.filters.SortSection
import com.example.bankapp.ui.components.SearchBarComponent
import com.example.bankapp.ui.components.appbar.SearchAppBar
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.viewmodels.FilterViewModel
import com.example.bankapp.viewmodels.TransactionsViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactionsViewModel: TransactionsViewModel,
    navController: NavController,
    filterViewModelFactory: FilterViewModelFactory
) {
    val transactions by transactionsViewModel.transactions.collectAsState()
    val query by transactionsViewModel.query.collectAsState()
    val filterViewModel: FilterViewModel = viewModel(factory = filterViewModelFactory)
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val deviceSpec = LocalDeviceSpec.current
    val isLoading by transactionsViewModel.isLoading.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        transactionsViewModel.exportEvent.collect { file -> shareFile(context, file) }
    }

    LaunchedEffect(filterViewModel.filterState) {
        transactionsViewModel.updateFilters(filterViewModel.filterState)
    }

    LaunchedEffect(filterViewModel.sortState) {
        transactionsViewModel.updateSort(filterViewModel.sortState)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchAppBar(
                scrollBehavior = scrollBehavior,
                searchContent = {
                    SearchBarComponent(
                        query = query,
                        onQueryChange = transactionsViewModel::onQueryChange,
                        onSearch = { keyboardController?.hide() },
                        onCancel = { transactionsViewModel.onQueryChange("") },
                        leadingContent = {
                            IconButton(onClick = {
                                if (query.isNotEmpty()) {
                                    transactionsViewModel.onQueryChange("")
                                } else {
                                    navController.popBackStack()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        trailingContent = {
                            Box {
                                IconButton(onClick = { transactionsViewModel.onShowMenuChange(true) }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = null)
                                }
                                DropdownMenu(
                                    expanded = transactionsViewModel.showMenu,
                                    onDismissRequest = { transactionsViewModel.onShowMenuChange(false) }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.Export_as_csv)) },
                                        onClick = {
                                            transactionsViewModel.onShowMenuChange(false)
                                            transactionsViewModel.exportCurrentTransactions(ExportType.CSV)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.Export_as_pdf)) },
                                        onClick = {
                                            transactionsViewModel.onShowMenuChange(false)
                                            transactionsViewModel.exportCurrentTransactions(ExportType.PDF)
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = dimensionResource(R.dimen.filter_sheet_padding))
                    .padding(vertical = AppSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                FilterSortChip(
                    label = stringResource(R.string.filter_label),
                    icon = Icons.Outlined.FilterList,
                    badgeCount = filterViewModel.filterState.selectedStatus.size +
                            filterViewModel.filterState.selectedTypes.size +
                            if (filterViewModel.filterState.selectedDirection != UiLedgerDirection.BOTH) 1 else 0 +
                            (if (filterViewModel.filterState.isDateFilterActive) 1 else 0),
                    showSheet = filterViewModel.filterShowSheet,
                    onShowSheetChange = filterViewModel::onFilterShowSheetChange,
                    fullHeight = true,
                    sheetContent = {
                        FilterSection(
                            pendingState = filterViewModel.pendingState,
                            onPendingStateChange = filterViewModel::onPendingStateChange,
                            onApply = filterViewModel::onFilterApply,
                            onReset = filterViewModel::onFilterReset,
                            onDismiss = { filterViewModel.onFilterShowSheetChange(false) },
                            showPicker = filterViewModel.showDatePicker,
                            onShowPickerChange = filterViewModel::onShowDatePickerChange
                        )
                    }
                )

                FilterSortChip(
                    label = stringResource(R.string.sort_label),
                    icon = Icons.AutoMirrored.Outlined.Sort,
                    badgeCount = 0,
                    showSheet = filterViewModel.sortShowSheet,
                    onShowSheetChange = filterViewModel::onSortShowSheetChange,
                    fullHeight = false,
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

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (transactions.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_transactions_found),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    TransactionLazyList(
                        transactions = transactions,
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
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
    }
}

fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Bank Statement"))
}

fun String.uiAmountDisplay(): String {
    val parts = this.split(".")
    if (parts.size < 2) return this
    return if (parts[1].all { it == '0' }) parts[0] else this
}