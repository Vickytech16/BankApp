package com.example.bankapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.bankapp.R
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec


@Composable
fun BeneficiaryItem(
    beneficiaryName: String,
    beneficiaryPfp: String? = null,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            UserAvatar(
                name = beneficiaryName,
                pfpUrl = beneficiaryPfp,
                size = dimensionResource(R.dimen.user_avatar_transaction)
            )
            Text(
                text = beneficiaryName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(dimensionResource(R.dimen.beneficiary_icon_button_area))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_friend),
                    modifier = Modifier.size(dimensionResource(R.dimen.beneficiary_list_icon_size)),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(dimensionResource(R.dimen.beneficiary_icon_button_area))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_friend),
                    modifier = Modifier.size(dimensionResource(R.dimen.beneficiary_list_icon_size)),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}



@Composable
fun BeneficiaryGridItem(
    beneficiaryName: String,
    beneficiaryPfp: String? = null,
    deviceSpec: DeviceSpec,
    onPayClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPayClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(deviceSpec.beneficiaryItemSpacing)
    ) {
        UserAvatar(
            name = beneficiaryName,
            pfpUrl = beneficiaryPfp,
            size = dimensionResource(deviceSpec.beneficiaryAvatarSize)
        )
        Text(
            text = beneficiaryName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}