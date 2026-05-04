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
import androidx.compose.material3.Slider
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.AppScaffold
import com.example.lankiemusicplayer.components.FabMode
import com.example.lankiemusicplayer.components.SharedFab
import com.example.lankiemusicplayer.navigation.rememberNavigationActions
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AllSongsScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    scrollToUri: String? = null,
    onOpenPlayer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val decodedUri = scrollToUri?.let {
        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
    }

    val songs by remember { derivedStateOf { playerViewModel.allSongs } }
    val listState = rememberLazyListState()
    val currentUri by playerViewModel.currentSongUri
    val navActions = rememberNavigationActions(navController)

    AppScaffold(
        title = "All Songs",

        onHomeClick = navActions::goHome,
        onSearchClick = navActions::goSearch,
        onSettingsClick = {
            navController.navigate("settings")
        },

        floatingActionButton = {
            SharedFab(
                mode = FabMode.SHUFFLE,
                listState = listState,
                onShuffle = {
                    playerViewModel.playShuffled(songs)
                }
            )
        }

    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // 🔹 Auto scroll to song
            LaunchedEffect(decodedUri, songs) {
                if (decodedUri != null && songs.isNotEmpty()) {

                    val index = songs.indexOfFirst {
                        it.uri.toString() == decodedUri
                    }

                    if (index != -1) {
                        listState.animateScrollToItem(index)
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
                onNavigateToArtist = {},
                onNavigateToAllSongs = {}
            )

            // 🔹 Vertical fast scroll
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(24.dp)
                    .align(Alignment.CenterEnd)
            ) {

                val totalItems = songs.size

                if (totalItems > 0) {

                    var sliderPosition by remember { mutableFloatStateOf(0f) }

                    LaunchedEffect(listState.firstVisibleItemIndex) {
                        sliderPosition = listState.firstVisibleItemIndex.toFloat()
                    }

                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        onValueChangeFinished = {
                            val target = sliderPosition.toInt()
                                .coerceIn(0, totalItems - 1)

                            scope.launch {
                                listState.animateScrollToItem(target)
                            }
                        },
                        valueRange = 0f..(totalItems - 1).toFloat(),
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
        }
    }
}