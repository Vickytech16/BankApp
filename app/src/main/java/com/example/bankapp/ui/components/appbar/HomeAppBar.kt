package com.example.bankapp.ui.components.appbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.ui.components.AutoResizeText
import com.example.bankapp.ui.theme.DeviceSpec
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeAppBar(
    username: String,
    drawerState: DrawerState,
    scrollBehavior: TopAppBarScrollBehavior,
    deviceSpec: DeviceSpec
) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Column(modifier = Modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(R.string.welcome_back),
                    style = deviceSpec.homeUserGreetingStyle(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AutoResizeText(
                    text = username,
                    style = deviceSpec.homeUserNameStyle(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
//            IconButton(
//                onClick = {
//                    scope.launch {
//                        drawerState.open()
//                    }
//                },
//                modifier = Modifier.padding(horizontal = deviceSpec.homeAppBarHorizontalPadding).size(dimensionResource(deviceSpec.homeAppBarMenuButtonSize))
//                 ) {
//                Icon(
//                    Icons.Outlined.Menu,
//                    contentDescription = stringResource(R.string.open_menu_content_description),
//                    tint = MaterialTheme.colorScheme.onSurface
//                )
//            }
        },
        scrollBehavior = scrollBehavior,
    )
}