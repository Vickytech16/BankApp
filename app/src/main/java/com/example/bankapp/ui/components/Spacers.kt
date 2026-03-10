package com.example.bankapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bankapp.ui.theme.AppSpacing


@Composable
fun XSSpacer()
{
    Spacer(Modifier.height(AppSpacing.xs))
}

@Composable
fun XLSpacer()
{
    Spacer(Modifier.height(AppSpacing.xl))
}

@Composable
fun MediumSpacer()
{
    Spacer(Modifier.height(AppSpacing.md))
}

@Composable
fun SmallSpacer(){
    Spacer(Modifier.height(AppSpacing.sm))
}