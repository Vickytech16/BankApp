package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.bankapp.R

import com.example.bankapp.entities.dtos.BeneficiaryDto
import com.example.bankapp.ui.components.navigators.CASH_TRANSFER_ROUTE
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.utilities.uiAccNo

@Composable
fun FriendLazyList(
    friends: List<BeneficiaryDto>,
    state: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(),
    navController: NavController

) {
    LazyColumn(
        state = state,
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        items(friends) { friend ->
            FriendListItem(
                friendName = friend.friendName,
                friendPfp = friend.friendPfp,
                onPayClick = {
                        val friendAccNo = friend.friendPrimaryAccNo.uiAccNo
                        navController.navigate("$CASH_TRANSFER_ROUTE/$friendAccNo")
                }
            )
        }
    }
}

@Composable
fun FriendListItem(
    friendName: String,
    friendPfp: String? = null,
    onPayClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        if (friendPfp == null)
            UserAvatar(friendName, size = dimensionResource(R.dimen.user_avatar_transaction))
        else
            UserAvatar(
                friendName,
                friendPfp,
                size = dimensionResource(R.dimen.user_avatar_transaction)
            )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            Text(
                text = friendName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        TextButton(onClick = onPayClick) {
            Text(stringResource( R.string.pay_screen_title))
        }
    }
}