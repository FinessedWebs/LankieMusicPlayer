package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistName: String,
    viewModel: PlayerViewModel,
    navController: NavController
) {

    val songs = viewModel.getArtistSongs(artistName)
    val currentUri by viewModel.currentSongUri

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artistName) }
            )
        }
    ) { padding ->

        SongList(
            songs = songs,
            viewModel = viewModel,
            currentPlayingUri = currentUri,
            modifier = Modifier.padding(padding),
            onSongClick = {
                viewModel.playSong(it, songs)
            },

            // ✅ ADD THESE
            onNavigateToArtist = { /* already here */ },
            onNavigateToAllSongs = { song ->
                navController.navigate("allsongs/${song.uri}")
            }
        )
    }
}