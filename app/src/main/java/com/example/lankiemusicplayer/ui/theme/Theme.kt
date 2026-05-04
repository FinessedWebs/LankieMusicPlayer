package com.example.lankiemusicplayer.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*


private val DarkColors = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,

    secondary = Color.White,
    onSecondary = Color.Black,

    background = Color.Black,
    onBackground = Color.White,

    surface = Color.Black,
    onSurface = Color.White,

    primaryContainer = Color(0xFF1A1A1A),
    onPrimaryContainer = Color.White,

    secondaryContainer = Color(0xFF1A1A1A),
    onSecondaryContainer = Color.White,

    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color.White,

    outline = Color(0xFF3A3A3A)
)

private val LightColors = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,

    secondary = Color.Black,
    onSecondary = Color.White,

    background = Color.White,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,

    primaryContainer = Color(0xFFF5F5F5),
    onPrimaryContainer = Color.Black,

    secondaryContainer = Color(0xFFF5F5F5),
    onSecondaryContainer = Color.Black,

    surfaceVariant = Color(0xFFF2F2F2),
    onSurfaceVariant = Color.Black,

    outline = Color(0xFFE0E0E0)
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentSand
)

private val AppTypography = Typography(
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold
    )
)

@Composable
fun LankieMusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}