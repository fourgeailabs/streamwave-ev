package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StreamWaveDarkScheme = darkColorScheme(
    primary = EvPrimary,
    onPrimary = EvOnPrimary,
    primaryContainer = EvPrimaryContainer,
    onPrimaryContainer = EvOnPrimaryContainer,
    secondary = EvSecondary,
    onSecondary = EvOnSecondary,
    secondaryContainer = EvSecondaryContainer,
    onSecondaryContainer = EvOnSecondaryContainer,
    tertiary = EvTertiary,
    onTertiary = EvOnTertiary,
    tertiaryContainer = EvTertiaryContainer,
    onTertiaryContainer = EvOnTertiaryContainer,
    background = EvBackground,
    onBackground = EvOnBackground,
    surface = EvSurface,
    onSurface = EvOnSurface,
    surfaceVariant = EvSurfaceVariant,
    onSurfaceVariant = EvOnSurfaceVariant,
    outline = EvBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Automotive displays prioritize dark high-contrast themes for night and day driving safety
    MaterialTheme(
        colorScheme = StreamWaveDarkScheme,
        typography = Typography,
        content = content
    )
}
