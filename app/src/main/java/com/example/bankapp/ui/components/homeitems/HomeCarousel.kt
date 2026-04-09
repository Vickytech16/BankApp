package com.example.bankapp.ui.components.homeitems

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.bankapp.entities.AccountVelocityStatus
import com.example.bankapp.entities.uientities.uimodels.AccountUiModel
import com.example.bankapp.ui.theme.AppSpacing
import com.example.bankapp.ui.theme.DeviceSpec

@Composable
fun HomeCardCarousel(
    account: AccountUiModel,
    velocityStatus: AccountVelocityStatus?,
    isBalanceVisible: Boolean,
    isVelocityVisible: Boolean,
    onBalanceToggle: () -> Unit,
    onVelocityToggle: () -> Unit,
    deviceSpec: DeviceSpec,
    countryCode: String
) {
    val pagerState = rememberPagerState(pageCount = { if (velocityStatus != null) 2 else 1 })
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val cardWidthDp = screenWidth * deviceSpec.homeScreenCardWidth
        val horizontalPadding = (screenWidth - cardWidthDp) / 2

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSpacing = 16.dp,
            userScrollEnabled = velocityStatus != null
        ) { page ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (page) {
                    0 -> HomeScreenCard(
                        account = account,
                        isBalanceVisible = isBalanceVisible,
                        onIsBalanceVisibleChange = onBalanceToggle,
                        deviceSpec = deviceSpec,
                        countryCode = countryCode
                    )
                    1 -> velocityStatus?.let {
                        HomeVelocityCard(
                            velocityStatus = it,
                            isVelocityVisible = isVelocityVisible,
                            onToggleVisibility = onVelocityToggle,
                            deviceSpec = deviceSpec,
                            countryCode = countryCode
                        )
                    }
                }
            }
        }

        if (velocityStatus != null) {
            Spacer(modifier = Modifier.height(AppSpacing.md))
            Row(
                Modifier.height(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val color by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        label = "dotColor"
                    )
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        label = "dotWidth"
                    )

                    Box(
                        modifier = Modifier
                            .size(width = width, height = 8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}