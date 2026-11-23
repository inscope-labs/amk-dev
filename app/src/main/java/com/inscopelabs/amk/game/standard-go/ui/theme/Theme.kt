package com.inscopelabs.amk.game.standardgo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightGoColors =
  lightColorScheme(
    primary = JadeAccent,
    onPrimary = Color.White,
    primaryContainer = JadeLight,
    onPrimaryContainer = AshCharcoal,
    secondary = KayaWoodDark,
    onSecondary = Color.White,
    secondaryContainer = ParchmentDeep,
    onSecondaryContainer = AshCharcoal,
    tertiary = GoldAccent,
    background = Parchment,
    onBackground = AshCharcoal,
    surface = ParchmentCard,
    onSurface = AshCharcoal,
    surfaceVariant = ParchmentDeep,
    onSurfaceVariant = AshMuted,
    outline = AshBorder,
    error = CrimsonWarn,
  )

private val DarkGoColors =
  darkColorScheme(
    primary = JadeLight,
    onPrimary = AshCharcoal,
    primaryContainer = JadeAccent,
    onPrimaryContainer = Color.White,
    secondary = KayaWood,
    onSecondary = AshCharcoal,
    secondaryContainer = AshSurface,
    onSecondaryContainer = Parchment,
    tertiary = GoldAccent,
    background = AshCharcoal,
    onBackground = Parchment,
    surface = AshSurface,
    onSurface = Parchment,
    surfaceVariant = Color(0xFF36332D),
    onSurfaceVariant = Color(0xFFC2B9AA),
    outline = Color(0xFF524C42),
    error = Color(0xFFE06D6D),
  )

@Composable
fun StandardGoTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colors = if (darkTheme) DarkGoColors else LightGoColors
  MaterialTheme(
    colorScheme = colors,
    typography = StandardGoTypography,
    content = content,
  )
}
