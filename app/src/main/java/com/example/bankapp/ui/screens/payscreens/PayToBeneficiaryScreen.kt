package com.example.bankapp.ui.screens.payscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.PayToBeneficiaryViewModelFactory
import com.example.bankapp.ui.components.BeneficiaryGridItem
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.components.SearchBarComponent
import com.example.bankapp.ui.components.appbar.SearchAppBar
import com.example.bankapp.ui.theme.LocalDeviceSpec
import com.example.bankapp.utilities.uiAccNo
import com.example.bankapp.viewmodels.PayToBeneficiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayToBeneficiaryScreen(
    payToBeneficiaryViewModelFactory: PayToBeneficiaryViewModelFactory,
    navController: NavController,
    origin: String
) {
    val viewModel: PayToBeneficiaryViewModel = viewModel(factory = payToBeneficiaryViewModelFactory)
    val friends by viewModel.friends.collectAsState()
    val query by viewModel.query.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val deviceSpec = LocalDeviceSpec.current
    val isLoading = viewModel.isLoading
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val filteredFriends = remember(friends, query) {
        friends.filter {
            friend ->
            friend.beneficiaryName.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchAppBar(
                scrollBehavior = scrollBehavior,
                searchContent = {
                    SearchBarComponent(
                        query = query,
                        onQueryChange = viewModel::onQueryChange,
                        onSearch = { keyboardController?.hide() },
                        onCancel = { viewModel.onQueryChange("") },
                        placeholderText = stringResource(R.string.search_friend_hint),
                        leadingContent = {
                            IconButton(onClick = {
                                if (query.isNotEmpty()) {
                                    viewModel.onQueryChange("")
                                } else {
                                    navController.popBackStack()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (filteredFriends.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_beneficiaries_found),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(deviceSpec.beneficiaryGridColumnsSize),
                        modifier = Modifier
                            .fillMaxSize()
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
                        items(filteredFriends) { friend ->
                            BeneficiaryGridItem(
                                beneficiaryName = friend.beneficiaryName,
                                beneficiaryPfp = friend.beneficiaryPfp,
                                deviceSpec = deviceSpec,
                                onPayClick = {
                                    val friendAccNo = friend.beneficiaryPrimaryAccNo.uiAccNo
                                    navController.navigate("$CASH_TRANSFER_ROUTE/$friendAccNo?origin=$origin")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}