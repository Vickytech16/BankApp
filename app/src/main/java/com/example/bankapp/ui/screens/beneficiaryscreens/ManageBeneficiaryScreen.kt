package com.example.bankapp.ui.screens.beneficiaryscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bankapp.R
import com.example.bankapp.di.viewmodelfactory.ManageBeneficiaryViewModelFactory
import com.example.bankapp.entities.uientities.uidata.AlertButtonConfig
import com.example.bankapp.ui.components.AlertDialogBox
import com.example.bankapp.entities.uientities.uitypes.AlertButtonStyle
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.BeneficiaryItem
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.components.SearchBarComponent
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.entities.uientities.uidata.UserNameFieldStrategy
import com.example.bankapp.ui.components.appbar.SearchAppBar
import com.example.bankapp.viewmodels.ManageBeneficiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBeneficiaryScreen(
    manageBeneficiaryViewModelFactory: ManageBeneficiaryViewModelFactory,
    navController: NavController
) {
    val viewModel: ManageBeneficiaryViewModel = viewModel(factory = manageBeneficiaryViewModelFactory)
    val friends by viewModel.friends.collectAsState()
    val query by viewModel.query.collectAsState()
    val isLoading = viewModel.isLoading
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val showDeleteDialog = viewModel.showDeleteDialog
    val showEditDialog = viewModel.showEditDialog
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    val filteredFriends = remember(friends, query) {
        friends.filter { friend ->
            friend.beneficiaryName.contains(query, ignoreCase = true)
        }
    }


    if (showDeleteDialog) {
        AlertDialogBox(
            onDismissRequest = { viewModel.onShowDeleteDialogChange(false) },
            title = stringResource(R.string.delete_beneficiary),
            confirmButton = AlertButtonConfig(
                label = stringResource(R.string.delete),
                onClick = { viewModel.onDelete() },
                style = AlertButtonStyle.ERROR
            ),
            dismissButton = AlertButtonConfig(
                label = stringResource(R.string.cancel_label),
                onClick = { viewModel.onShowDeleteDialogChange(false) }
            ),
            content = {
                Text(stringResource(R.string.delete_beneficiary_confirmation))
            }
        )
    }


    if (showEditDialog) {
        AlertDialogBox(
            onDismissRequest = {
                viewModel.onShowEditDialogChange(false)
                viewModel.resetNicknameState()
            },
            title = stringResource(R.string.edit_beneficiary),
            confirmButton = AlertButtonConfig(
                label = stringResource(R.string.save_button),
                onClick = { viewModel.onNicknameSubmit() }
            ),
            dismissButton = AlertButtonConfig(
                label = stringResource(R.string.cancel_label),
                onClick = {
                    viewModel.onShowEditDialogChange(false)
                    viewModel.resetNicknameState()
                }
            ),
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                    UnifiedOutlinedTextField(
                        value = viewModel.nickname ?: "",
                        onValueChange = viewModel::onNicknameChange,
                        labelText = stringResource(R.string.nickname_field_name),
                        isError = viewModel.nicknameError != null,
                        supportingText = {
                            ErrorTextBuilder(viewModel.nicknameError)
                        },
                        strategy = UserNameFieldStrategy,
                        trailingIcon = {
                            if (viewModel.nickname?.isNotEmpty() == true) {
                                IconButton(onClick = { viewModel.onNicknameChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                    ErrorTextBuilder(viewModel.submitError)
                }
            }
        )
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
                                }
                                else {
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
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else if (filteredFriends.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_beneficiaries_found),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(AppSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                    state = lazyListState
                ) {
                    items(filteredFriends) { friend ->
                        BeneficiaryItem(
                            beneficiaryName = friend.beneficiaryName,
                            beneficiaryPfp = friend.beneficiaryPfp,
                            onEditClick = {
                                viewModel.onSelectFriend(friend)
                                viewModel.onShowEditDialogChange(true)
                            },
                            onDeleteClick = {
                                viewModel.onSelectFriend(friend)
                                viewModel.onShowDeleteDialogChange(true)
                            },
                            onEnlargeImage = viewModel.showEnlargedImage,
                            onEnlargeImageChange = viewModel::onShowEnlargedImageChange
                        )
                    }
                }
            }
        }
    }
}