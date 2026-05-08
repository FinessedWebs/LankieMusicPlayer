package com.example.lankiemusicplayer.ui.theme

import android.content.Context

class ThemePreferences(context: Context) {

    private val prefs =
        context.getSharedPreferences("lankie_theme_prefs", Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode", true)
    }

    fun setDarkMode(value: Boolean) {
        prefs.edit().putBoolean("dark_mode", value).apply()
    }

    fun getAccent(): ThemeAccent {
        val saved = prefs.getString("accent", ThemeAccent.Default.name)
            ?: ThemeAccent.Default.name

        return ThemeAccent.entries.firstOrNull { it.name == saved }
            ?: ThemeAccent.Default
    }

    fun setAccent(accent: ThemeAccent) {
        prefs.edit().putString("accent", accent.name).apply()
    }
}