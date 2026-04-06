package com.example.bankapp.ui.components.appbar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bankapp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    searchContent: @Composable () -> Unit,
    bottomContent: @Composable (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.statusBarsPadding(),
        tonalElevation = if ((scrollBehavior?.state?.contentOffset ?: 0f) < 0f) AppSpacing.xs else 0.dp
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = AppSpacing.lg)
                    ) {
                        searchContent()
                    }
                }
            )
            bottomContent?.invoke()
        }
    }
}