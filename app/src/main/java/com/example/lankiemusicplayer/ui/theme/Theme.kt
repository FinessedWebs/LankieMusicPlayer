package com.example.lankiemusicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

private fun getAccent(accent: ThemeAccent, darkTheme: Boolean): Color {
    return when (accent) {
        ThemeAccent.Default -> if (darkTheme) Color.White else Color.Black
        ThemeAccent.Pink -> if (darkTheme) AccentPinkDark else AccentPink
        ThemeAccent.Orange -> if (darkTheme) AccentOrangeDark else AccentOrange
        ThemeAccent.Blue -> if (darkTheme) AccentBlueDark else AccentBlue
        ThemeAccent.Green -> if (darkTheme) AccentGreenDark else AccentGreen
        ThemeAccent.Purple -> if (darkTheme) AccentPurpleDark else AccentPurple
    }
}

private fun buildColorScheme(
    darkTheme: Boolean,
    accent: ThemeAccent
): ColorScheme {

    val primaryAccent = getAccent(accent, darkTheme)

    return if (darkTheme) {
        darkColorScheme(
            primary = primaryAccent,
            onPrimary = Color.Black,

            secondary = primaryAccent,
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
    } else {
        lightColorScheme(
            primary = primaryAccent,
            onPrimary = Color.White,

            secondary = primaryAccent,
            onSecondary = Color.White,

            background = WarmWhite,
            onBackground = PrimaryText,

            surface = CardWhite,
            onSurface = PrimaryText,

            primaryContainer = Color(0xFFF5F5F5),
            onPrimaryContainer = PrimaryText,

            secondaryContainer = Color(0xFFF5F5F5),
            onSecondaryContainer = PrimaryText,

            surfaceVariant = Color(0xFFF2F2F2),
            onSurfaceVariant = PrimaryText,

            outline = DividerSoft
        )
    }
}

/*private val AppTypography = Typography(
    titleMedium = TextStyle(fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold)
)*/

@Composable
fun LankieMusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: ThemeAccent = ThemeAccent.Default,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = buildColorScheme(darkTheme, accent),
        typography = Typography,
        content = content
    )
}