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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.TransactionDetailsViewModelFactory
import com.example.bankapp.entities.SessionState
import com.example.bankapp.services.TransactionReceiptPainter
import com.example.bankapp.ui.components.BackButtonHandler
import com.example.bankapp.ui.components.SharePreviewDialog
import com.example.bankapp.ui.components.appbar.RegularAppBar
import com.example.bankapp.ui.components.transactionitems.TransactionDetailBody
import com.example.bankapp.viewmodels.TransactionDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    navController: NavController,
    transactionDetailsViewModelFactory: TransactionDetailsViewModelFactory,
    transactionId: String,
    backRoute: String,
    sessionState: SessionState.Authenticated.AccountRegistered
) {
    val viewModel: TransactionDetailsViewModel = viewModel(factory = transactionDetailsViewModelFactory)
    val account = sessionState.account.collectAsState()
    val user = sessionState.user.collectAsState()
    val context = LocalContext.current
    val receiptPainter = remember { TransactionReceiptPainter(context) }

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId, account.value.accNo)
    }

    val detailItem = viewModel.transaction.collectAsState(null).value
    BackButtonHandler(navController, backRoute)

    Scaffold(
        topBar = {
            RegularAppBar(
                title = "",
                navBehaviour = { navController.popBackStack() },
                scrollBehavior = null,
                actions = {
                    IconButton(
                        onClick = {
                            detailItem?.let { item ->
                                viewModel.generateReceipt(
                                    painter = receiptPainter,
                                    item = item,
                                    countryCode = user.value.countryCode,
                                    timezone = user.value.timeZone
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.share_button_content_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (detailItem != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.showSharePreview && viewModel.previewBitmap != null) {
                    SharePreviewDialog(
                        bitmap = viewModel.previewBitmap!!,
                        onDismiss = { viewModel.dismissPreview() },
                        onConfirm = { viewModel.confirmShare() }
                    )
                }

                TransactionDetailBody(
                    transaction = detailItem,
                    paddingValues = paddingValues,
                    countryCode = user.value.countryCode,
                    onEnlargeImage = viewModel.showEnlargeImage,
                    onEnlargeImageChange = {
                        viewModel.showEnlargeImage
                        !viewModel.showEnlargeImage
                    },
                )
            }
        }
        else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}