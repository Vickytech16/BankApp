package com.example.bankapp.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.PayToBeneficiaryViewModelFactory
import com.example.bankapp.ui.components.FriendLazyList
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.transactionitems.TransactionSearchBar
import com.example.bankapp.viewmodels.PayToBeneficiaryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayToBeneficiaryScreen(
    payToBeneficiaryViewModelFactory: PayToBeneficiaryViewModelFactory,
    navController: NavController,
) {
    val viewModel: PayToBeneficiaryViewModel = viewModel(factory = payToBeneficiaryViewModelFactory)
    val friends by viewModel.friends.collectAsState()
    val query by viewModel.query.collectAsState()
    val lazyListState = rememberLazyListState()

    val isLoading = viewModel.isLoading

    val filteredFriends = friends.filter { friend ->
        friend.friendUserId.toString().contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Appbar(
                stringResource(R.string.beneficiaries),
                navBehaviour = { navController.popBackStack() },
                scrollBehavior = null
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TransactionSearchBar(
                query,
                viewModel::onQueryChange,
                { },
                {
                    viewModel.onQueryChange("")
                }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            if (!isLoading && filteredFriends.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_beneficiaries_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FriendLazyList(
                friends = filteredFriends,
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                navController
            )
        }
    }
}