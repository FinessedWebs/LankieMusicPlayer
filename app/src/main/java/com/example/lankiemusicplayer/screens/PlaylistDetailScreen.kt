package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.AddSongsDialog
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.data.MusicScanner
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    navController: NavController,
    viewModel: PlayerViewModel
) {

    val context = LocalContext.current

    val allSongs = remember {
        MusicScanner.getSongs(context)
    }

    val playlist = viewModel.getPlaylist(playlistId)

    val songs = viewModel.getPlaylistSongs(playlistId, allSongs)

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(

        topBar = {

            TopAppBar(
                title = { Text(playlist?.name ?: "") },
                actions = {

                    IconButton(
                        onClick = { showAddDialog = true }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Song")
                    }

                }
            )

        }

    ) { padding ->

        val currentUri by viewModel.currentSongUri

        SongList(
            songs = songs,
            viewModel = viewModel,
            currentPlayingUri = currentUri,
            modifier = Modifier.padding(padding),
            onSongClick = {
                viewModel.playSong(it, songs)
            },

            // ✅ ADD THESE
            onNavigateToArtist = { /* optional */ },
            onNavigateToAllSongs = { song ->
                navController.navigate("allsongs/${song.uri}")
            }
        )

    }

    if (showAddDialog) {

        AddSongsDialog(
            allSongs = allSongs,
            onAdd = { song ->
                viewModel.addSongToPlaylist(playlistId, song)
            },
            onDismiss = { showAddDialog = false }
        )

    }

}