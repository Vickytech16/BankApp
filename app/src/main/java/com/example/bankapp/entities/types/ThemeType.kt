package com.example.bankapp.entities.types

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.bankapp.R

enum class ThemeType{
    LIGHT,
    DARK,
    SYSTEM_DEFAULT;

    @Composable
    fun getDisplayName(): String = when (this) {
        LIGHT -> stringResource(R.string.light_theme_label)
        DARK -> stringResource(R.string.dark_theme_label)
        SYSTEM_DEFAULT -> stringResource(R.string.system_default_theme_label)
    }

    companion object {
        fun fromStringToThemeType(value: String): ThemeType {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                SYSTEM_DEFAULT
            }
        }
    }
}