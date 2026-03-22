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
    primary = Color(0xFF8B4513),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4A574),
    onPrimaryContainer = Color(0xFF3E2010),

    secondary = Color(0xFFA0826D),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8D5C4),
    onSecondaryContainer = Color(0xFF3E2F23),

    tertiary = Color(0xFF9B6B47),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDEC9B0),
    onTertiaryContainer = Color(0xFF35220A),

    background = Color(0xFFE8DCC8),
    onBackground = Color(0xFF2B2520),

    surface = Color(0xFFE8DCC8),
    onSurface = Color(0xFF2B2520),
    surfaceContainer = Color(0xFFD9C9B3),
    surfaceVariant = Color(0xFFD4C4B0),
    onSurfaceVariant = Color(0xFF6B6158),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF370B0E),

    outline = Color(0xFF9B8B7E),
    outlineVariant = Color(0xFFD9CFBF),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF3F3935),
    inverseOnSurface = Color(0xFFE8DCC8),
    inversePrimary = Color(0xFFD4A574),
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

/*
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D2A5C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB8D1F5),
    onPrimaryContainer = Color(0xFF001A41),

    secondary = Color(0xFF996F00),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD89D),
    onSecondaryContainer = Color(0xFF1F1500),

    tertiary = Color(0xFF1F6B5C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF82EED9),
    onTertiaryContainer = Color(0xFF00201B),

    background = Color(0xFFF8FAFD),
    onBackground = Color(0xFF07101A),

    surface = Color(0xFFFBFCFE),
    onSurface = Color(0xFF07101A),
    surfaceVariant = Color(0xFFE7F0FA),
    onSurfaceVariant = Color(0xFF3F4A62),

    error = Color(0xFFAA3830),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF7D8D6),
    onErrorContainer = Color(0xFF2A0D0B),

    outline = Color(0xFF6F7A8F),
    outlineVariant = Color(0xFFCED8ED),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF0C1520),
    inverseOnSurface = Color(0xFFF2F6FC),
    inversePrimary = Color(0xFFC4D9FF),
)
 */