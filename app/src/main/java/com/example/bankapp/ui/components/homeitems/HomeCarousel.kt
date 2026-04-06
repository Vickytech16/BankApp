package com.example.bankapp.ui.components.homeitems

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bankapp.entities.uientities.uimodels.AccountUiModel
import com.example.bankapp.entities.AccountVelocityStatus
import com.example.bankapp.ui.theme.DeviceSpec
import com.example.bankapp.ui.theme.AppSpacing

@Composable
fun HomeCardCarousel(
    account: AccountUiModel,
    velocityStatus: AccountVelocityStatus?,
    currentPage: Int,
    isBalanceVisible: Boolean,
    isVelocityVisible: Boolean,
    onBalanceToggle: () -> Unit,
    onVelocityToggle: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    deviceSpec: DeviceSpec,
    countryCode: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(deviceSpec.homeScreenCardWidth),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onPrevious,
                enabled = currentPage > 0,
                modifier = Modifier.size(AppSpacing.xl)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                    contentDescription = null,
                    tint = if (currentPage > 0) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }
                    },
                    label = "CardSlide"
                ) { targetPage ->
                    when (targetPage) {
                        0 -> HomeScreenCard(
                            account = account,
                            isBalanceVisible = isBalanceVisible,
                            onIsBalanceVisibleChange = onBalanceToggle,
                            deviceSpec = deviceSpec,
                            countryCode = countryCode
                        )
                        1 -> if (velocityStatus != null) {
                            HomeVelocityCard(
                                velocityStatus = velocityStatus,
                                isVelocityVisible = isVelocityVisible,
                                onToggleVisibility = onVelocityToggle,
                                deviceSpec = deviceSpec,
                                countryCode = countryCode
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onNext,
                enabled = currentPage < 1 && velocityStatus != null,
                modifier = Modifier.size(AppSpacing.xl)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                    contentDescription = null,
                    tint = if (currentPage < 1) MaterialTheme.colorScheme.primary else Color.Transparent
                )
            }
        }
    }
}