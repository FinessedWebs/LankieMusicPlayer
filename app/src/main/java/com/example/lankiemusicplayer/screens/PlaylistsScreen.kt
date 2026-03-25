package com.example.lankiemusicplayer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.CreatePlaylistDialog
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    navController: NavController,
    viewModel: PlayerViewModel
) {

    val playlists = viewModel.getPlaylists()

    var showDialog by remember { mutableStateOf(false) }


    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Playlists") },
                actions = {

                    IconButton(
                        onClick = { showDialog = true }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }

                }
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            item {

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    onClick = {
                        navController.navigate("artists")
                    }
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Icon(Icons.Default.Person, contentDescription = null)

                        Spacer(Modifier.width(16.dp))

                        Column {

                            Text(
                                "Artists",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Browse by artist")

                        }
                    }
                }
            }


            items(playlists) { playlist ->

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    onClick = {
                        navController.navigate("playlist_detail/${playlist.id}")
                    }
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Icon(Icons.Default.Folder, contentDescription = null)

                        Spacer(Modifier.width(16.dp))

                        Column {

                            Text(
                                playlist.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("${playlist.songs.size} songs")

                        }

                    }

                }

            }

        }

    }

    if (showDialog) {

        CreatePlaylistDialog(
            onCreate = {
                viewModel.createPlaylist(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )

    }



}