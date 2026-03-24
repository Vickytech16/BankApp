package com.example.bankapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.entities.types.ui.FilterState
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.types.ui.SortOptions
import com.example.bankapp.entities.types.ui.UiLedgerDirection
import com.example.bankapp.repositories.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionsViewModel(
    val sessionState: SessionState.Authenticated.AccountRegistered,
    val transactionRepository: TransactionRepository
): ViewModel() {


    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())

    private val _sortState = MutableStateFlow(SortOptions.NEWEST_FIRST)

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = combine(
        _query,
        _filterState,
        _sortState
    ) {
        searchQuery, filters, sort ->
        transactionRepository.getFilteredTransactions(
            accNo = sessionState.account.accNo,
            searchQuery = searchQuery.trim(),
            types = filters.selectedTypes.map { it.name },
            typeFilter = if (filters.selectedTypes.isEmpty()) 0 else 1,
            directions =
                filters.selectedDirection.getDirection(),
            directionFilter = if (filters.selectedDirection == UiLedgerDirection.BOTH) 0 else 1,
            statuses = filters.selectedStatus.map { it.name },
            statusFilter = if (filters.selectedStatus.isEmpty()) 0 else 1,
            sortOrder = sort.getSort()
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
}