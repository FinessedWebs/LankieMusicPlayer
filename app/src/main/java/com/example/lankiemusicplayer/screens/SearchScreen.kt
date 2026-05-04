package com.example.lankiemusicplayer.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.SongList
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import com.example.lankiemusicplayer.model.YouTubeSong
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: PlayerViewModel
) {

    var query by remember { mutableStateOf("") }

    val allSongs = viewModel.allSongs

    val filteredSongs = remember(query, allSongs) {
        if (query.isBlank()) {
            emptyList()
        } else {
            allSongs.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true)
            }
        }
    }

    val listState = rememberLazyListState()
    val currentUri by viewModel.currentSongUri



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search songs...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(Modifier.height(16.dp))

        LaunchedEffect(query) {
            if (query.isNotBlank() && filteredSongs.isEmpty()) {

                Log.d("YouTubeAPI", "Searching for: $query") // ✅ correct place

                viewModel.searchYouTube(query)
            }
        }

        // 🔹 Results Logic
        if (filteredSongs.isEmpty() && query.isNotBlank()) {

            Text(
                text = "No songs found locally",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Suggested from YouTube",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            if (viewModel.isYoutubeLoading.value) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(viewModel.youtubeResults) { ytSong ->

                        ListItem(
                            headlineContent = {
                                Text(
                                    ytSong.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            supportingContent = {
                                Text(
                                    ytSong.channel,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    val context = navController.context

                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(
                                            "https://www.youtube.com/watch?v=${ytSong.videoId}"
                                        )
                                    )

                                    context.startActivity(intent)
                                }
                        )
                    }
                }
            }

        } else {

            // 🔹 Normal local songs
            SongList(
                songs = filteredSongs,
                viewModel = viewModel,
                currentPlayingUri = currentUri,
                onSongClick = { song ->
                    viewModel.playSong(song, filteredSongs)
                },
                onNavigateToArtist = { },
                onNavigateToAllSongs = { },
                modifier = Modifier.fillMaxSize(),
                listState = listState
            )
        }


    }




}