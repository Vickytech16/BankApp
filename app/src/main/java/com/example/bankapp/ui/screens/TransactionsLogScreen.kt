package com.example.bankapp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.TransactionsViewModelFactory
import com.example.bankapp.entities.types.LedgerDirection
import com.example.bankapp.ui.components.Appbar
import com.example.bankapp.ui.components.TransactionLazyList
import com.example.bankapp.ui.components.UserAvatar
import com.example.bankapp.ui.components.XLSpacer
import com.example.bankapp.ui.theme.AppPadding
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.screenPadding
import com.example.bankapp.utilities.RUPEESYMBOL
import com.example.bankapp.viewmodels.TransactionsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactionsViewModel: TransactionsViewModel, navController: NavController
) {


    val transactions by transactionsViewModel.transactions.collectAsState()

    //val scrollState = rememberScrollState()

    Scaffold(topBar = {
        Appbar(
            stringResource(R.string.transactions),
            navBehaviour = { navController.popBackStack() },
            scrollBehavior = null
        )
    }) {
        paddingValues ->
        TransactionLazyList(
            transactions = transactions,
            modifier = AppPadding.padding(screenPadding)
                .fillMaxWidth()
                .padding(paddingValues)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionListItem(
    counterPartyName: String,
    transactionDirection: String,
    amount: String,
    transactionDate: String,
    transactionURL: String? = null
){
    val formattedDate = transactionDate.toMonthAndDayOnlyDate()
    val isCredit = transactionDirection.equals("CREDIT", true)

    val amountColor =
        if(isCredit)
            Color.Green
        else
            MaterialTheme.colorScheme.error


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        if(transactionURL==null)
            UserAvatar(counterPartyName, size = dimensionResource(R.dimen.user_avatar_transaction))
        else
            UserAvatar(counterPartyName, transactionURL,size = dimensionResource(R.dimen.user_avatar_transaction))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Text(
                text = counterPartyName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$RUPEESYMBOL$amount",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun String.toMonthAndDayOnlyDate(): String{
    return try {
        val truncated = this.substringBeforeLast(".")
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val parsed = LocalDateTime.parse(truncated, formatter)
        parsed.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault()))
    } catch (e: Exception) {
        this
    }
}

fun String.uiAmountDisplay(): String{
    val parts = this.toString().split(".")
    val wholePart = parts[0]
    val decimalPart = parts[1]

    if(decimalPart.all{it=='0'})
        return wholePart
    else
        return this
}