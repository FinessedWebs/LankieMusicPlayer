package com.example.lankiemusicplayer.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MiniPlayer(
    onOpenPlayer: () -> Unit,
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val title by viewModel.currentTitle
    val artist by viewModel.currentArtist
    val progress by viewModel.progress
    val isPlaying by viewModel.isPlaying
    val remainingMs by remember { derivedStateOf { viewModel.remainingMs } }

    val player = viewModel.playerController.getPlayer()

    ElevatedCard(
        onClick = onOpenPlayer,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    icon = Icons.Filled.Album,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        artist,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = { viewModel.togglePlayPause() }) {
                    AppIcon(
                        icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // ✅ CORRECT: Use actual remaining time in milliseconds
            LinearWavyProgressIndicator(
                progress = { progress },
                amplitude = { _ ->
                    // ✅ Wave only in last 10 seconds
                    if (remainingMs <= 10_000L) 1f else 0f
                }
            )
        }
    }
}