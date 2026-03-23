package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bankapp.ui.theme.AppSpacing


@Composable
fun XSSpacer() {
    Spacer(Modifier.height(AppSpacing.xs))
}

@Composable
fun XLSpacer() {
    Spacer(Modifier.height(AppSpacing.xl))
}

@Composable
fun MediumSpacer() {
    Spacer(Modifier.height(AppSpacing.md))
}

@Composable
fun LargeSpacer() {
    Spacer(Modifier.height(AppSpacing.lg))
}

@Composable
fun SmallSpacer() {
    Spacer(Modifier.height(AppSpacing.sm))
}

@Composable
fun MediumHorizontalSpacer() {
    Spacer(Modifier.width(AppSpacing.md))
}