package com.example.bankapp.ui.components.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R
import com.example.bankapp.entities.FilterState
import com.example.bankapp.entities.types.LedgerDirection
import com.example.bankapp.entities.types.TransactionStatus
import com.example.bankapp.entities.types.TransactionType
import com.example.bankapp.entities.types.UiLedgerDirection
import com.example.bankapp.ui.components.LargeSpacer


@Composable
fun FilterSection(
    pendingState: FilterState,
    onPendingStateChange: (FilterState) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.filter_sheet_padding)),
    ){
        Text(
            text = stringResource(R.string.filter_label),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        LargeSpacer()

        FilterItem(
            title = stringResource(R.string.status_label),
            options = TransactionStatus.entries.toSet(),
            selected = pendingState.selectedStatus,
            onClick = {
                transactionStatus  ->
                val currentSelected = pendingState.selectedStatus.toMutableSet()
                if(transactionStatus in currentSelected)
                    currentSelected.remove(transactionStatus)
                else
                    currentSelected.add(transactionStatus)
               onPendingStateChange(pendingState.copy(selectedStatus = currentSelected))
            },
            labelFor = {
                status->
                when(status) {
                    TransactionStatus.COMPLETED -> stringResource(R.string.completed_label)
                    TransactionStatus.PENDING -> stringResource(R.string.pending_label)
                    TransactionStatus.FAILED -> stringResource(R.string.failed_label)
                }
            }
        )

        LargeSpacer()

        FilterItem(
            title = stringResource(R.string.transaction_type_label),
            options = TransactionType.entries.toSet(),
            selected = pendingState.selectedTypes,
            onClick = {
                transactionType ->
                val currentSelected = pendingState.selectedTypes.toMutableSet()
                if (transactionType in currentSelected)
                    currentSelected.remove(transactionType)
                else
                    currentSelected.add(transactionType)
                onPendingStateChange(pendingState.copy(selectedTypes = currentSelected))
            },
            labelFor = {
                transactionType ->
                when(transactionType){
                    TransactionType.DEPOSIT -> stringResource(R.string.deposit_label)
                    TransactionType.CASH_TRANSFER -> stringResource(R.string.cash_transfer_label)
                    TransactionType.SCHEDULED_TRANSFER -> stringResource(R.string.scheduled_transfer_label)
                }
            },
        )

        LargeSpacer()

        FilterSortRadioButton(
            title = stringResource(R.string.direction_label),
            options = UiLedgerDirection.entries,
            selected = pendingState.selectedDirection,
            onClick = {
                uiLedgerDirection ->
                var currentSelected = pendingState.selectedDirection
                if(uiLedgerDirection != currentSelected)
                    currentSelected = uiLedgerDirection
              onPendingStateChange(pendingState.copy(selectedDirection = currentSelected))
            },
            labelFor = {
                uiLedgerDirection ->
                when(uiLedgerDirection){
                    UiLedgerDirection.CREDIT -> stringResource(R.string.credit_label)
                    UiLedgerDirection.DEBIT -> stringResource(R.string.debit_label)
                    UiLedgerDirection.BOTH -> stringResource(R.string.both_label)
                }
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.filter_chip_spacing))
        ){
            OutlinedButton(
                onClick = {
                    onReset()
                    onDismiss()
                    },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.reset_label))
            }
            Button(
                onClick = {
                    onApply()
                    onDismiss()
                    },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.apply_label))
            }
        }
        LargeSpacer()
    }

}