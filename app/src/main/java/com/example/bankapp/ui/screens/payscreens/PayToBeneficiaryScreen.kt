package com.example.bankapp.ui.screens.payscreens


import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.PayToBeneficiaryViewModelFactory
import com.example.bankapp.ui.components.BeneficiaryGridItem
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.SearchBarComponent
import com.example.bankapp.ui.theme.DeviceSpecProvider
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.utilities.uiAccNo
import com.example.bankapp.viewmodels.PayToBeneficiaryViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayToBeneficiaryScreen(
    payToBeneficiaryViewModelFactory: PayToBeneficiaryViewModelFactory,
    navController: NavController,
    windowSizeClass: WindowSizeClass
) {
    val viewModel: PayToBeneficiaryViewModel = viewModel(factory = payToBeneficiaryViewModelFactory)
    val friends by viewModel.friends.collectAsState()
    val query by viewModel.query.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val deviceSpec = LocalDeviceSpec.current

    val isLoading = viewModel.isLoading

    val filteredFriends = friends.filter { friend ->
        friend.friendName.contains(query, ignoreCase = true)
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
            SearchBarComponent(
                query,
                viewModel::onQueryChange,
                {
                    keyboardController?.hide()
                    val scope =
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                {
                    viewModel.onQueryChange("")
                    keyboardController?.hide()
                },
                placeholderText = stringResource(R.string.search_friend_hint)
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(deviceSpec.beneficiaryGridColumnsSize),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
                contentPadding = PaddingValues(
                    top = deviceSpec.beneficiaryItemSpacing,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + deviceSpec.beneficiaryItemSpacing,
                    start = deviceSpec.beneficiaryItemSpacing,
                    end = deviceSpec.beneficiaryItemSpacing
                ),
                horizontalArrangement = Arrangement.spacedBy(deviceSpec.beneficiaryItemSpacing),
                verticalArrangement = Arrangement.spacedBy(deviceSpec.beneficiaryItemSpacing)
            ) {
                items(filteredFriends) {
                    friend ->
                    BeneficiaryGridItem(
                        friendName = friend.friendName,
                        friendPfp = friend.friendPfp,
                        deviceSpec = deviceSpec,
                        onPayClick = {
                            val friendAccNo = friend.friendPrimaryAccNo.uiAccNo
                            navController.navigate("$CASH_TRANSFER_ROUTE/$friendAccNo")
                        }
                    )
                }
            }
        }
    }
}