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
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.navigators.TRANSACTIONS_LOG_ROUTE
import com.example.bankapp.ui.components.transactionitems.TransactionDetailBody
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    navController: NavController,
    transactionDetailsViewModelFactory: TransactionDetailsViewModelFactory,
    transactionId: String,
    windowSizeClass: WindowSizeClass,
    backRoute: String,
    sessionState: SessionState.Authenticated.AccountRegistered
){
    val viewModel: TransactionDetailsViewModel = viewModel(factory = transactionDetailsViewModelFactory)

    val account = sessionState.account.collectAsState()
    val user = sessionState.user.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId, account.value.accNo)
    }

    val detailItem = viewModel.transaction.collectAsState(null).value

    BackButtonHandler(navController, backRoute,)

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
        },) {
        paddingValues ->
        if (detailItem != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                TransactionDetailBody(
                    transactionItem = detailItem,
                    paddingValues = paddingValues,
                    windowSizeClass = windowSizeClass,
                    countryCode = user.value.countryCode
                )
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