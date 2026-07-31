package com.mjp5153.craft.cocktails.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CraftCocktailLightColorScheme = lightColorScheme(
    primary = AmberPrimary,
    onPrimary = AmberOnPrimary,
    primaryContainer = AmberContainer,
    onPrimaryContainer = AmberOnContainer,
    secondary = SandSecondary,
    secondaryContainer = SandSecondaryContainer,
    background = LightCanvas,
    onBackground = TextDark,
    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = SandSecondaryContainer,
    onSurfaceVariant = TextMuted,
    outline = SubtleBorder,
    outlineVariant = SubtleBorder
)

@Composable
fun CraftCocktailTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Keep UI light and minimal per user request
    MaterialTheme(
        colorScheme = CraftCocktailLightColorScheme,
        typography = Typography,
        content = content
    )
}
