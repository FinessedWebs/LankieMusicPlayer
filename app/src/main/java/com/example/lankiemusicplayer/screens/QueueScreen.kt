package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.components.FabMode
import com.example.lankiemusicplayer.components.SharedFab
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

@Composable
fun QueueScreen(
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {

    val currentUri by viewModel.currentSongUri
    val queue = remember(currentUri) {
        viewModel.getQueue()
    }
    val shuffleEnabled by viewModel.isShuffleEnabled

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Up Next",
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(
                    onClick = { viewModel.toggleShuffle() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (shuffleEnabled)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }

                TextButton(onClick = onBack) {
                    Text("Close")
                }
            }

            Spacer(Modifier.height(16.dp))


            SongList(
                songs = queue,
                viewModel = viewModel,
                currentPlayingUri = currentUri,
                enableSwipeToRemove = true,
                onSongClick = { song ->
                    viewModel.playSong(song, queue)
                },

                // ✅ ADD THESE
                onNavigateToArtist = { /* optional */ },
                onNavigateToAllSongs = { /* optional */ }
            )


        }


        SharedFab(
            mode = FabMode.SHUFFLE,
            onShuffle = {
                viewModel.toggleShuffle()
            }
        )

    }
}