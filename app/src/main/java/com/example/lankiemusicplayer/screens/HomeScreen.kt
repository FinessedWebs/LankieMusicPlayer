package com.example.lankiemusicplayer.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lankiemusicplayer.components.FavoritePlaylistCards
import com.example.lankiemusicplayer.components.MiniPlayer
import com.example.lankiemusicplayer.components.MusicTabs
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.data.MusicScanner
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.AllSongsButton
import androidx.compose.material.icons.filled.Refresh
import com.example.lankiemusicplayer.components.FabMode
import com.example.lankiemusicplayer.components.SharedFab
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    onOpenPlayer: () -> Unit
) {
    val player by playerViewModel.playerController.controller
    val context = LocalContext.current
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) }



    LaunchedEffect(songs.isEmpty()) {
        if (songs.isEmpty()) {

            val loadedSongs = withContext(Dispatchers.IO) {
                MusicScanner.getSongs(context)
            }

            songs = loadedSongs

            Log.d("LankieMusic", "Total songs found: ${songs.size}")
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                FavoritePlaylistCards(
                    favoriteCount = playerViewModel.getFavorites(songs).size,
                    playlistCount = playerViewModel.getPlaylists().size,
                    onFavoritesClick = {
                        navController.navigate("favorites")
                    },
                    onPlaylistsClick = {
                        navController.navigate("playlists")
                    }
                )

                Spacer(Modifier.height(16.dp))

                AllSongsButton(
                    onClick = {
                        navController.navigate("allsongs")
                    }
                )

                Spacer(Modifier.height(16.dp))

                MusicTabs(
                    onTabSelected = { tab ->
                        selectedTab = tab

                        when (tab) {

                            0 -> {
                                songs = MusicScanner
                                    .getSongs(context)
                                    .sortedByDescending { it.id }
                            }

                            1 -> {
                                songs = playerViewModel.getRecentlyPlayed()
                            }

                            2 -> {
                                songs = playerViewModel
                                    .getMostPlayed()
                                    .map { it.first }
                            }
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))



                val mostPlayed = playerViewModel.getMostPlayed()

                val currentUri by playerViewModel.currentSongUri

                SongList(
                    songs = songs,
                    viewModel = playerViewModel,
                    currentPlayingUri = currentUri,
                    showMenu = selectedTab == 0,
                    enableSwipeToPlaylist = selectedTab != 0,
                    trailingText = { song, index ->

                        when (selectedTab) {

                            1 -> "${index + 1}"

                            2 -> mostPlayed
                                .find { it.first.uri == song.uri }
                                ?.second
                                ?.toString()

                            else -> null
                        }
                    },
                    onSongClick = { song ->
                        playerViewModel.playSong(song, songs)
                    },

                    // ✅ ADD THESE (IMPORTANT)
                    onNavigateToArtist = { artist ->
                        navController.navigate("artist_detail/$artist")
                    },
                    onNavigateToAllSongs = { song ->
                        val encoded = URLEncoder.encode(song.uri.toString(), StandardCharsets.UTF_8.toString())
                        navController.navigate("allsongs?scrollTo=$encoded")
                    }
                )

            }
        }


        SharedFab(
            mode = FabMode.HOME,
            onSearch = {
                navController.navigate("search")
            },
            onRefresh = {
                songs = MusicScanner.getSongs(context)
                    .sortedByDescending { it.id }
            }
        )

    }
}