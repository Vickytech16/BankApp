package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R


@Composable
fun LogoutButton(showLogoutAction: Boolean, onShowLogoutActionChange: (Boolean) -> Unit, logoutAction: () -> Unit){
    Button(
        onClick = {
            onShowLogoutActionChange(true)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.profile__screen_logout_button_height)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.profile_screen_card_rounded_corners))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.profile_screen_logout_icon_size))
            )
            Text(
                text = stringResource(R.string.logout_button),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if(showLogoutAction){
        AlertDialogBox(
            onDismissRequest = { onShowLogoutActionChange(false) },
            title = stringResource(R.string.logout_confirmation_title),
            confirmButton = AlertButtonConfig(
                label = stringResource(R.string.logout_button),
                onClick = logoutAction,
                style = ButtonStyle.ERROR
            ),
            dismissButton = AlertButtonConfig(
                label = stringResource(R.string.cancel_label),
                onClick = { }
            ),
            content = {
                Text(stringResource(R.string.logout_confirmation_message))
            }
        )
    }
}