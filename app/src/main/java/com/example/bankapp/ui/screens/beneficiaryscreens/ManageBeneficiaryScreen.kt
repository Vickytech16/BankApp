package com.example.bankapp.ui.screens.beneficiaryscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.bankapp.di.viewmodelfactory.ManageBeneficiaryViewModelFactory
import com.example.bankapp.ui.components.AlertButtonConfig
import com.example.bankapp.ui.components.AlertDialogBox
import com.example.bankapp.ui.components.ButtonStyle
import com.example.bankapp.ui.components.ErrorTextBuilder
import com.example.bankapp.ui.components.FriendListItem
import com.example.bankapp.ui.components.appbar.Appbar
import com.example.bankapp.ui.components.textfields.UnifiedOutlinedTextField
import com.example.bankapp.ui.components.SearchBarComponent
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.utilities.UserNameFieldStrategy
import com.example.bankapp.viewmodels.ManageBeneficiaryViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBeneficiaryScreen(
    manageBeneficiaryViewModelFactory: ManageBeneficiaryViewModelFactory,
    navController: NavController,
    windowSizeClass: WindowSizeClass
) {
    val viewModel: ManageBeneficiaryViewModel = viewModel(factory = manageBeneficiaryViewModelFactory)
    val friends by viewModel.friends.collectAsState()
    val query by viewModel.query.collectAsState()
    val isLoading = viewModel.isLoading
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val showDeleteDialog = viewModel.showDeleteDialog
    val showEditDialog = viewModel.showEditDialog

    val filteredFriends = friends.filter { friend ->
        friend.friendName.contains(query, ignoreCase = true)
    }


    if (showDeleteDialog) {
        AlertDialogBox(
            onDismissRequest = { viewModel.onShowDeleteDialogChange(false) },
            title = stringResource(R.string.delete_beneficiary),
            confirmButton = AlertButtonConfig(
                label = stringResource(R.string.delete),
                onClick = { viewModel.onDelete() },
                style = ButtonStyle.ERROR
            ),
            dismissButton = AlertButtonConfig(
                label = stringResource(R.string.cancel_label),
                onClick = {
                    viewModel.onShowDeleteDialogChange(false)
                }
            ),
            content = {
                Text(stringResource(R.string.delete_beneficiary_confirmation))
            }
        )
    }

    if (showEditDialog) {
        AlertDialogBox(
            onDismissRequest = { viewModel.onShowEditDialogChange(false) },
            title = stringResource(R.string.edit_beneficiary),
            confirmButton = AlertButtonConfig(
                label = stringResource(R.string.save_button),
                onClick = {
                    viewModel.onNicknameSubmit()
                }
            ),
            dismissButton = AlertButtonConfig(
                label = stringResource(R.string.cancel_label),
                onClick = {
                    viewModel.onShowEditDialogChange(false)
                    viewModel.resetNicknameState()
                }
            ),
            content = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    UnifiedOutlinedTextField(
                        value = viewModel.nickname,
                        onValueChange = viewModel::onNicknameChange,
                        labelText = stringResource(R.string.nickname_field_name),
                        isError = viewModel.nicknameError != null,
                        supportingText = {
                            ErrorTextBuilder(viewModel.nicknameError)
                        },
                        strategy = UserNameFieldStrategy
                    )
                    ErrorTextBuilder(viewModel.submitError)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Appbar(
                stringResource(R.string.manage_beneficiaries),
                navBehaviour = {
                    navController.popBackStack()
                },
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(R.dimen.screen_padding),
                    vertical = dimensionResource(R.dimen.screen_padding)
                ),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding)),
                state = lazyListState
            ) {
                items(filteredFriends) { friend ->
                    FriendListItem(
                        friendName = friend.friendName,
                        friendPfp = friend.friendPfp,
                        onEditClick = {
                            viewModel.onSelectFriend(friend)
                            viewModel.onShowEditDialogChange(true)
                        },
                        onDeleteClick = {
                            viewModel.onSelectFriend(friend)
                            viewModel.onShowDeleteDialogChange(true)
                        }
                    )
                }
            }
        }
    }
}