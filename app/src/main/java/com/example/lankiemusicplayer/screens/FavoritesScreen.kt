package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.AppScaffold
import com.example.lankiemusicplayer.components.FabMode
import com.example.lankiemusicplayer.components.SharedFab
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.data.MusicScanner
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.navigation.rememberNavigationActions
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FavoritesScreen(
    navController: NavController,
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
    val currentUri by playerViewModel.currentSongUri
    val navActions = rememberNavigationActions(navController)

    AppScaffold(
        title = "Favorites",

        onHomeClick = navActions::goHome,
        onSearchClick = navActions::goSearch,
        onSettingsClick = {
            navController.navigate("settings")
        },

        floatingActionButton = {
            SharedFab(
                mode = FabMode.SHUFFLE,
                onShuffle = {
                    playerViewModel.playShuffled(favorites)
                }
            )
        }

    ) {

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

            SongList(
                songs = favorites,
                viewModel = playerViewModel,
                currentPlayingUri = currentUri,
                onSongClick = { song ->
                    playerViewModel.playSong(song, favorites)
                },
                onNavigateToArtist = {},
                onNavigateToAllSongs = {}
            )
        }
    }
}