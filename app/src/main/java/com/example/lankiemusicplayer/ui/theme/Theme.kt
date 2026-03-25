package com.example.lankiemusicplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

private val LightColors = lightColorScheme(
    primary = AccentSand,
    background = WarmWhite,
    surface = CardWhite,
    onPrimary = Color.White,
    onBackground = PrimaryText,
    onSurface = PrimaryText
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
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val DarkColorScheme = darkColorScheme(
        primary = Color.White,
        onPrimary = Color.Black,
        surface = Color.Black,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF1E1E1E),
        onSurfaceVariant = Color.Gray
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}