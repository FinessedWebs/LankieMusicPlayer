package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
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
    }
}