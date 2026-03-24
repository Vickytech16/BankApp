
package com.example.bankapp.ui.components.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.bankapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appbar(
    title: String,
    navBehaviour: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior?,
    actions: @Composable () -> Unit = {},
    titleStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    showNavIcon: Boolean = true
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = title,
                style = titleStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = if (showNavIcon && navBehaviour != null) {
            {
                IconButton(onClick = navBehaviour) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_icon),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            {}
        },
        actions = {
            actions()
        }
    )
}