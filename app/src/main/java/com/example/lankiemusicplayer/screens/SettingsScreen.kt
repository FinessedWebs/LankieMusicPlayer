package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.ui.theme.ThemeAccent

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    selectedAccent: ThemeAccent,
    onAccentChange: (ThemeAccent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark Mode")

            Switch(
                checked = isDarkMode,
                onCheckedChange = onToggleTheme
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Accent Color",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        ThemeAccent.entries.forEach { accent ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(accent.label)

                RadioButton(
                    selected = selectedAccent == accent,
                    onClick = { onAccentChange(accent) }
                )
            }
        }
    }
}