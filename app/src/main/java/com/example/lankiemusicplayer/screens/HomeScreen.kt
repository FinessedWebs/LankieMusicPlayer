package com.example.lankiemusicplayer.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.lankiemusicplayer.components.AllSongsButton
import com.example.lankiemusicplayer.components.AppScaffold
import com.example.lankiemusicplayer.components.FabMode
import com.example.lankiemusicplayer.components.FavoritePlaylistCards
import com.example.lankiemusicplayer.components.MusicTabs
import com.example.lankiemusicplayer.components.SharedFab
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.data.MusicScanner
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.navigation.rememberNavigationActions
import com.example.lankiemusicplayer.ui.theme.ThemeAccent
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    isDarkMode: Boolean,
    selectedAccent: ThemeAccent,
    onOpenPlayer: () -> Unit
) {

    val context = LocalContext.current

    var songs by remember {
        mutableStateOf<List<Song>>(emptyList())
    }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    val navActions = rememberNavigationActions(navController)

    LaunchedEffect(songs.isEmpty()) {

        if (songs.isEmpty()) {

            val loadedSongs = withContext(Dispatchers.IO) {
                MusicScanner.getSongs(context)
            }

            songs = loadedSongs

            Log.d(
                "LankieMusic",
                "Total songs found: ${songs.size}"
            )
        }
    }

    AppScaffold(
        title = "Lankie",

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
        },

        floatingActionButton = {

            SharedFab(
                mode = FabMode.HOME,
                accent = selectedAccent,
                isDarkMode = isDarkMode,

                onSearch = {
                    navController.navigate("search")
                },

                onRefresh = {

                    songs = MusicScanner
                        .getSongs(context)
                        .sortedByDescending { it.id }
                }
            )
        }

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            FavoritePlaylistCards(
                favoriteCount =
                    playerViewModel.getFavorites(songs).size,

                playlistCount =
                    playerViewModel.getPlaylists().size,

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

                            songs =
                                playerViewModel.getRecentlyPlayed()
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

            val mostPlayed =
                playerViewModel.getMostPlayed()

            val currentUri by
            playerViewModel.currentSongUri

            SongList(
                songs = songs,

                viewModel = playerViewModel,

                currentPlayingUri = currentUri,

                showMenu = selectedTab == 0,

                enableSwipeToPlaylist =
                    selectedTab != 0,

                trailingText = { song, index ->

                    when (selectedTab) {

                        1 -> "${index + 1}"

                        2 -> mostPlayed
                            .find {
                                it.first.uri == song.uri
                            }
                            ?.second
                            ?.toString()

                        else -> null
                    }
                },

                onSongClick = { song ->

                    playerViewModel.playSong(
                        song,
                        songs
                    )
                },

                onNavigateToArtist = { artist ->

                    navController.navigate(
                        "artist_detail/$artist"
                    )
                },

                onNavigateToAllSongs = { song ->

                    val encoded =
                        URLEncoder.encode(
                            song.uri.toString(),
                            StandardCharsets.UTF_8.toString()
                        )

                    navController.navigate(
                        "allsongs?scrollTo=$encoded"
                    )
                }
            )
        }
    }
}