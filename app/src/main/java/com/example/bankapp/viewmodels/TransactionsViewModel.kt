package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.core.datecompatability.BankDateFactory
import com.example.bankapp.entities.uientities.uidata.FilterState
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.dtos.ExportMetadata
import com.example.bankapp.entities.dtos.TransactionExportDto
import com.example.bankapp.entities.uientities.uitypes.SortOptions
import com.example.bankapp.entities.uientities.uitypes.UiLedgerDirection
import com.example.bankapp.repositories.TransactionRepository
import com.example.bankapp.services.TransactionExportService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.entities.types.transaction.ExportType
import com.example.bankapp.ui.screens.uiAmountDisplay
import com.example.bankapp.utilities.CurrencyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.File

class TransactionsViewModel(
    val sessionState: SessionState.Authenticated.AccountRegistered,
    val transactionRepository: TransactionRepository,
    private val exportService: TransactionExportService
): ViewModel() {


    private val user = sessionState.user
    private val account = sessionState.account

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())

    val countryCode = user.value.countryCode

    private val _sortState = MutableStateFlow(SortOptions.NEWEST_FIRST)

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = combine(
        _query,
        _filterState,
        _sortState
    ) {
        searchQuery, filters, sort ->

        val startTime = filters.startDate?.let {
            BankDateFactory.fromMillis(it).startOfDayMillis
        } ?: 0L
        val endTime = filters.endDate?.let {
            BankDateFactory.fromMillis(it).endOfDayMillis
        } ?: Long.MAX_VALUE

        transactionRepository.getFilteredTransactions(
            accNo = account.value.accNo,
            searchQuery = searchQuery.trim(),
            types = filters.selectedTypes.map { it.name },
            typeFilter = if (filters.selectedTypes.isEmpty()) 0 else 1,
            directions =
                filters.selectedDirection.getDirection(),
            directionFilter = if (filters.selectedDirection == UiLedgerDirection.BOTH) 0 else 1,
            statuses = filters.selectedStatus.map { it.name },
            statusFilter = if (filters.selectedStatus.isEmpty()) 0 else 1,
            sortOrder = sort.getSort(),
            startDate = startTime,
            endDate = endTime,
            dateFilter = if (filters.startDate != null) 1 else 0
        )
    }
        .flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            transactions.collect {
                _isLoading.value = false
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun updateFilters(newFilterState: FilterState) {
        _filterState.value = newFilterState
    }

    fun updateSort(newSort: SortOptions) {
        _sortState.value = newSort
    }

    private fun UiLedgerDirection.getDirection() : List<String>{
        return when(this){
            UiLedgerDirection.CREDIT -> listOf("CREDIT")
            UiLedgerDirection.DEBIT -> listOf("DEBIT")
            UiLedgerDirection.BOTH -> listOf("CREDIT", "DEBIT")
        }
    }

    private fun SortOptions.getSort() : String{
        return when(this){
            SortOptions.NEWEST_FIRST -> "NEWEST"
            SortOptions.OLDEST_FIRST -> "OLDEST"
        }
    }

    var showMenu by mutableStateOf(false)
        private set

    fun onShowMenuChange(newValue: Boolean){
        showMenu = newValue
    }



    private val _exportEvent = MutableSharedFlow<File>()
    val exportEvent = _exportEvent.asSharedFlow()

    fun exportCurrentTransactions(exportType: ExportType) {

        viewModelScope.launch {
            val currentList = transactions.value
            if (currentList.isEmpty()) return@launch

            withContext(Dispatchers.IO) {
                val exportData = currentList.map { exportData ->
                    val description = if (exportData.transactionType == TransactionType.DEPOSIT) {
                        "${exportData.myUserName} (Deposit)"
                    } else {
                        exportData.counterpartyName ?: "Unknown"
                    }

                    TransactionExportDto(
                        date = exportData.transactionDate.toMonthDayDisplay(),
                        description = description,
                        type = exportData.transactionType.name,
                        direction = exportData.ledgerDirection?.name ?: "N/A",
                        amount = "${exportData.amount.uiAmountDisplay()} ${CurrencyUtils.getCurrencySymbol(countryCode)}",
                        balanceAfter = exportData.balanceAfter,
                        status = exportData.transactionStatus?.name ?: "UNKNOWN",
                        reference = exportData.referenceNumber
                    )
                }

                val metadata = ExportMetadata(
                    userName = sessionState.user.value.userName,
                    accountNo = "XXXX${sessionState.account.value.accNo.toString().takeLast(4)}"
                )

              val exportedFile =  when(exportType) {
                    ExportType.CSV -> {
                    exportService.createCsvFile(
                        metadata,
                        exportData,
                        "Bank_Statement_${BankDateFactory.now().fileNameDate()}"
                    )
                }
                    ExportType.PDF -> {
                        exportService.createPdfFile(
                            metadata,
                            exportData,
                            "Bank_Statement_${BankDateFactory.now().fileNameDate()}"
                        )
                    }
                }
                _exportEvent.emit(exportedFile)
            }

        }
    }
}

