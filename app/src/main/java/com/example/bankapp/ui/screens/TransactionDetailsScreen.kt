package com.example.bankapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.di.viewmodelfactory.TransactionDetailsViewModelFactory
import com.example.bankapp.viewmodels.TransactionDetailsViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R
import com.example.bankapp.entities.SessionState
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.transactionitems.CashTransferDetailBody
import com.example.bankapp.ui.components.transactionitems.DepositDetailBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    navController: NavController,
    transactionDetailsViewModelFactory: TransactionDetailsViewModelFactory,
    transactionId: String,
    accNo: Long,
    windowSizeClass: WindowSizeClass,
){
    val viewModel: TransactionDetailsViewModel = viewModel(factory = transactionDetailsViewModelFactory)

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId, accNo)
    }

    val detailItem = viewModel.transaction.collectAsState(null).value

    Scaffold(
        topBar = {
            Appbar(
                title = "",
                navBehaviour = {navController.popBackStack()},
                scrollBehavior = null,
                actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.share_button_content_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
    ) {
        paddingValues ->
        if (detailItem != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (detailItem.transactionType) {

                    TransactionType.CASH_TRANSFER -> {
                        print(detailItem.ledgerDirection)
                        CashTransferDetailBody(
                            transactionItem = detailItem,
                            paddingValues = paddingValues,
                            windowSizeClass = windowSizeClass
                        )
                    }

                    TransactionType.DEPOSIT -> {
                        DepositDetailBody(
                            transactionItem = detailItem,
                            paddingValues = paddingValues,
                            windowSizeClass = windowSizeClass
                        )
                    }

                    TransactionType.SCHEDULED_TRANSFER -> {
                        CashTransferDetailBody(
                            transactionItem = detailItem,
                            paddingValues = paddingValues,
                            windowSizeClass = windowSizeClass
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}