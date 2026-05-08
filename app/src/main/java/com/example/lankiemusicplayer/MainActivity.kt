package com.example.lankiemusicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.lankiemusicplayer.navigation.AppNavigation
import com.example.lankiemusicplayer.ui.theme.LankieMusicPlayerTheme
import com.example.lankiemusicplayer.ui.theme.ThemeAccent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("LankieMusic", "App started successfully")

        requestAudioPermission()

        setContent {
            val prefs = getSharedPreferences(
                "lankie_settings",
                MODE_PRIVATE
            )
            val systemDark = isSystemInDarkTheme()

            val savedDarkMode =
                prefs.getBoolean(
                    "dark_mode",
                    systemDark
                )

            val savedAccent =
                prefs.getString(
                    "accent",
                    ThemeAccent.Default.name
                ) ?: ThemeAccent.Default.name

            val isDarkMode = remember {
                mutableStateOf(savedDarkMode)
            }

            val selectedAccent = remember {
                mutableStateOf(
                    ThemeAccent.valueOf(savedAccent)
                )
            }

            val darkTheme = isDarkMode.value



            SideEffect {
                window.statusBarColor = if (darkTheme)
                    android.graphics.Color.BLACK
                else
                    android.graphics.Color.WHITE

                WindowInsetsControllerCompat(window, window.decorView)
                    .isAppearanceLightStatusBars = !darkTheme
            }

            LankieMusicPlayerTheme(
                darkTheme = darkTheme,
                accent = selectedAccent.value
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        isDarkMode = isDarkMode.value,
                        onToggleTheme = {

                            isDarkMode.value = it

                            prefs.edit()
                                .putBoolean("dark_mode", it)
                                .apply()
                        },
                        selectedAccent = selectedAccent.value,
                        onAccentChange = {

                            selectedAccent.value = it

                            prefs.edit()
                                .putString(
                                    "accent",
                                    it.name
                                )
                                .apply()
                        }
                    )
                }
            }
        }
    }

    private fun requestAudioPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                100
            )
        }
    }
}