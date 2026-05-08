package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.AppScaffold
import com.example.lankiemusicplayer.navigation.rememberNavigationActions
import com.example.lankiemusicplayer.player.SleepTimerManager
import com.example.lankiemusicplayer.ui.theme.ThemeAccent
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

@Composable
fun AppSleepScreen(
    navController: NavController,
    selectedAccent: ThemeAccent,
    playerViewModel: PlayerViewModel
) {

    val navActions = rememberNavigationActions(navController)

    var selectedMode by remember {
        mutableStateOf(SleepMode.MINUTES)
    }

    var selectedMinutes by remember {
        mutableIntStateOf(30)
    }

    var selectedSongs by remember {
        mutableIntStateOf(5)
    }

    var customMinutes by remember {
        mutableStateOf("")
    }

    var customSongs by remember {
        mutableStateOf("")
    }

    var fullyStopPlayback by remember {
        mutableStateOf(false)
    }

    AppScaffold(
        title = "Sleep Timer",

        onHomeClick = navActions::goHome,

        onSearchClick = navActions::goSearch,

        onCookingTimeClick = {
            navController.navigate("cooking_time")
        },

        onSleepClick = {
            navController.navigate("sleep")
        },

        onSettingsClick = {
            navController.navigate("settings")
        }

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = "Music Sleep Timer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Automatically stop music after a number of minutes or songs.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Stop Condition",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    SleepMode.entries.forEach { mode ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedMode == mode,
                                    onClick = {
                                        selectedMode = mode
                                    }
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected = selectedMode == mode,
                                onClick = {
                                    selectedMode = mode
                                }
                            )

                            Spacer(Modifier.width(10.dp))

                            Icon(
                                imageVector = when (mode) {
                                    SleepMode.MINUTES -> Icons.Default.Timer
                                    SleepMode.SONGS -> Icons.Default.MusicNote
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            if (selectedMode == SleepMode.MINUTES) {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "$selectedMinutes Minutes",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        Slider(
                            value = selectedMinutes.toFloat(),
                            onValueChange = {
                                selectedMinutes = it.toInt()
                            },
                            valueRange = 5f..180f,
                            steps = 34
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = customMinutes,
                            onValueChange = { value ->
                                customMinutes = value.filter { c ->
                                    c.isDigit()
                                }

                                customMinutes.toIntOrNull()?.let {
                                    selectedMinutes = it
                                }
                            },
                            label = {
                                Text("Custom Minutes")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (selectedMode == SleepMode.SONGS) {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "$selectedSongs Songs",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        Slider(
                            value = selectedSongs.toFloat(),
                            onValueChange = {
                                selectedSongs = it.toInt()
                            },
                            valueRange = 1f..25f,
                            steps = 23
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = customSongs,
                            onValueChange = { value ->
                                customSongs = value.filter { c ->
                                    c.isDigit()
                                }

                                customSongs.toIntOrNull()?.let {
                                    selectedSongs = it
                                }
                            },
                            label = {
                                Text("Custom Song Count")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Fully Stop Playback",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Stops music, clears the queue, and ends the active playback session.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Switch(
                        checked = fullyStopPlayback,
                        onCheckedChange = {
                            fullyStopPlayback = it
                        }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            FilledTonalButton(



                    onClick = {

                        if (selectedMode == SleepMode.MINUTES) {

                            val minutesToUse =
                                customMinutes.toIntOrNull()
                                    ?: selectedMinutes

                            SleepTimerManager.startMinutesTimer(
                                minutes = minutesToUse,
                                controller = playerViewModel.playerController.getPlayer()
                            )
                        }

                        else {

                            val songsToUse =
                                customSongs.toIntOrNull()
                                    ?: selectedSongs

                            SleepTimerManager.startSongsTimer(
                                songs = songsToUse
                            )
                        }

                        navController.popBackStack()
                    }
            ) {

                Text(
                    text = "Start Sleep Timer",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

enum class SleepMode(val label: String) {
    MINUTES("Stop After Minutes"),
    SONGS("Stop After Number of Songs")
}