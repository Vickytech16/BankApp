package com.example.bankapp.ui.components.appbar

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bankapp.R
import com.example.bankapp.ui.components.DynamicStepMeter
import com.example.bankapp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowAppBar(
    title: String,
    currentStep: Int,
    totalSteps: Int,
    scrollBehavior: TopAppBarScrollBehavior?,
    onBack: () -> Unit,
    expandedContent: @Composable (() -> Unit)? = null,
    showStepMeter: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_label)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (expandedContent != null) {
            AnimatedVisibility(
                visible = true,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm)) {
                    expandedContent()
                }
            }
        }

        if(showStepMeter) {

            DynamicStepMeter(
                totalSteps = totalSteps,
                currentStep = currentStep,
                modifier = Modifier.padding(bottom = AppSpacing.sm)
            )
        }
    }
}