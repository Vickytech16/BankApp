package com.example.bankapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bankapp.entities.types.ui.FilterState
import com.example.bankapp.entities.types.ui.SortOptions

class FilterViewModel: ViewModel() {
    var filterState by mutableStateOf(FilterState())
        private set
    var pendingState by mutableStateOf(FilterState())
        private set

    var filterShowSheet by mutableStateOf(false)
        private set

    fun onFilterShowSheetChange(newValue: Boolean){
        if (newValue) {
            pendingState = filterState.copy()
        }
        filterShowSheet = newValue
    }

    fun onPendingStateChange(newState: FilterState) {
        pendingState = newState
    }

    fun onFilterApply() {
        filterState = pendingState
        filterShowSheet = false
    }

    fun onFilterReset() {
        pendingState = FilterState()
        filterState = FilterState()
        filterShowSheet = false
    }

    var sortState by mutableStateOf(SortOptions.NEWEST_FIRST)
        private set

    var pendingSortState by mutableStateOf(SortOptions.NEWEST_FIRST)
        private set

    fun onSortChange(newSort: SortOptions) {
        pendingSortState = newSort
    }

    fun onSortApply() {
        sortState = pendingSortState
        sortShowSheet = false
    }

    fun onSortReset() {
        pendingSortState = SortOptions.NEWEST_FIRST
        sortState = SortOptions.NEWEST_FIRST
        sortShowSheet = false
    }

    var sortShowSheet by mutableStateOf(false)
        private set

    fun onSortShowSheetChange(newValue: Boolean){
        if (newValue) {
            pendingSortState = sortState
        }
        sortShowSheet = newValue
    }
}