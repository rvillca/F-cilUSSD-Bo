package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GooglePastelBlue,
    onPrimary = GoogleBlueDarkText,
    primaryContainer = GoogleClockSurfaceVariantDark,
    onPrimaryContainer = GooglePastelBlue,
    secondary = GoogleTonalPillDark,
    onSecondary = GoogleTonalPillText,
    secondaryContainer = GoogleClockSurfaceVariantDark,
    onSecondaryContainer = Color(0xFFE2E2E6),
    tertiary = GoogleMint,
    onTertiary = GoogleMintText,
    background = GoogleClockBgDark,
    surface = GoogleClockSurfaceDark,
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = GoogleClockSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC4C7C5),
    outline = GoogleClockOutlineDark,
    outlineVariant = Color(0xFF282A2F)
)

private val LightColorScheme = lightColorScheme(
    primary = GoogleBlueLightAccent,
    onPrimary = Color.White,
    primaryContainer = GooglePastelBlue,
    onPrimaryContainer = GoogleBlueDarkText,
    secondary = GoogleClockSurfaceVariantLight,
    onSecondary = Color(0xFF1F1F1F),
    secondaryContainer = Color(0xFFE8EEF8),
    onSecondaryContainer = Color(0xFF041E49),
    tertiary = Color(0xFF006C47),
    onTertiary = Color.White,
    background = GoogleClockBgLight,
    surface = GoogleClockSurfaceLight,
    onBackground = Color(0xFF1F1F1F),
    onSurface = Color(0xFF1F1F1F),
    surfaceVariant = GoogleClockSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF444746),
    outline = GoogleClockOutlineLight,
    outlineVariant = Color(0xFFEDEFF3)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
