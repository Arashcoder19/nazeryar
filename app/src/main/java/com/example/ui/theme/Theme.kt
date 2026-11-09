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
  primary = BlueprintBlueAccent,
  onPrimary = Color(0xFF0F172A),
  primaryContainer = Color(0xFF1E3A8A),
  onPrimaryContainer = Color(0xFFDBEAFE),
  secondary = ConstructionAmberLight,
  onSecondary = Color(0xFF78350F),
  background = BlueprintNavyDark,
  surface = SlateDarkSurface,
  surfaceVariant = Color(0xFF334155),
  onBackground = DarkTextPrimary,
  onSurface = DarkTextPrimary,
  outline = Color(0xFF64748B),
)

private val LightColorScheme = lightColorScheme(
  primary = BlueprintNavy,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFDBEAFE),
  onPrimaryContainer = Color(0xFF1E3A8A),
  secondary = ConstructionAmber,
  onSecondary = Color.White,
  tertiary = BlueprintNavyLight,
  background = SlateLightBg,
  surface = SlateLightSurface,
  surfaceVariant = SlateBorder,
  onBackground = SlateTextPrimary,
  onSurface = SlateTextPrimary,
  outline = Color(0xFFCBD5E1),
)

@Composable
fun NazerYarTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent branding colors
  content: @Composable () -> Unit,
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
    typography = AppTypography,
    content = content
  )
}
