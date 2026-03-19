package com.example.bankapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1B3A6B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC8D9F5),
    onPrimaryContainer = Color(0xFF0A1E3F),

    secondary = Color(0xFFB8860B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE5A0),
    onSecondaryContainer = Color(0xFF2A1F00),

    tertiary = Color(0xFF2E7D6B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF9FE5D3),
    onTertiaryContainer = Color(0xFF001511),

    background = Color(0xFFFAFBFE),
    onBackground = Color(0xFF0A1118),

    surface = Color(0xFFFDFDFE),
    onSurface = Color(0xFF0A1118),
    surfaceVariant = Color(0xFFF0F4FC),
    onSurfaceVariant = Color(0xFF515A6D),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF370B0E),

    outline = Color(0xFF8A93A5),
    outlineVariant = Color(0xFFD6DFEE),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF0F1419),
    inverseOnSurface = Color(0xFFF5F5F5),
    inversePrimary = Color(0xFFADC8FF),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE6D27A),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFD6E4FF),

    secondary = Color(0xFFC9A227),
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF2A2000),
    onSecondaryContainer = Color(0xFFFFDF8E),

    tertiary = Color(0xFF4DB89A),
    onTertiary = Color(0xFF00201A),
    tertiaryContainer = Color(0xFF1A3D34),
    onTertiaryContainer = Color(0xFFB2F0E0),

    background = Color(0xFF0B1220),
    onBackground = Color(0xFFE8EDF5),

    surface = Color(0xFF121A2F),
    onSurface = Color(0xFFE8EDF5),
    surfaceVariant = Color(0xFF1C2640),
    onSurfaceVariant = Color(0xFF94A3B8),

    error = Color(0xFFFF6B6B),
    onError = Color(0xFF410002),
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF2E3F5C),
    outlineVariant = Color(0xFF1E2D45),
    inverseSurface = Color(0xFFE8EDF5),
    inverseOnSurface = Color(0xFF0D1B2A),
    inversePrimary = Color(0xFF1B3A6B),
)

@Composable
fun BankAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.surface,
            darkIcons = !darkTheme
        )

        systemUiController.setNavigationBarColor(
            color = colorScheme.surface,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}