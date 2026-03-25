package com.example.lankiemusicplayer.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.components.MiniPlayer
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.data.MusicScanner
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AllSongsScreen(
    playerViewModel: PlayerViewModel,
    scrollToUri: String? = null,
    onOpenPlayer: () -> Unit
) {

    val decodedUri = scrollToUri?.let {
        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
    }

    val context = LocalContext.current
    val listState = rememberLazyListState()


    val songs = playerViewModel.allSongs

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // 🔹 SONG LIST (background)
        val currentUri by playerViewModel.currentSongUri

        LaunchedEffect(decodedUri, songs) {

            if (decodedUri != null && songs.isNotEmpty()) {

                val index = songs.indexOfFirst {
                    it.uri.toString() == decodedUri
                }

                if (index != -1) {
                    listState.scrollToItem(index)
                }
            }
        }

        SongList(
            songs = songs,
            viewModel = playerViewModel,
            currentPlayingUri = currentUri,
            onSongClick = { song ->
                playerViewModel.playSong(song, songs)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 110.dp
                ),
            listState = listState,

            // 🔥 REQUIRED (fixes errors)
            onNavigateToArtist = { /* optional */ },
            onNavigateToAllSongs = { /* optional */ }
        )

        FloatingActionButton(
            onClick = {
                playerViewModel.playShuffled(songs)
                Toast.makeText(
                    context,
                    "Shuffle playing ${songs.size} songs",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = 100.dp
                ),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle play"
            )
        }

    }
}