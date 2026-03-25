package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.data.MusicScanner
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FavoritesScreen(
    playerViewModel: PlayerViewModel
) {

    val context = LocalContext.current

    var allSongs by remember { mutableStateOf<List<Song>>(emptyList()) }

    LaunchedEffect(Unit) {
        val loadedSongs = withContext(Dispatchers.IO) {
            MusicScanner.getSongs(context)
        }

        allSongs = loadedSongs
    }

    val favorites = playerViewModel.getFavorites(allSongs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        val currentUri by playerViewModel.currentSongUri

        SongList(
            songs = favorites,
            viewModel = playerViewModel,
            currentPlayingUri = currentUri,
            onSongClick = { song ->
                playerViewModel.playSong(song, favorites)
            },

            // ✅ ADD THESE
            onNavigateToArtist = { /* optional for now */ },
            onNavigateToAllSongs = { /* optional */ }
        )
    }
}