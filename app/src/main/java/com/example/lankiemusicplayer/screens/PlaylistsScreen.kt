package com.example.lankiemusicplayer.screens

import android.R.attr.padding
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lankiemusicplayer.components.AppScaffold
import com.example.lankiemusicplayer.components.CreatePlaylistDialog
import com.example.lankiemusicplayer.model.Playlist
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import com.example.lankiemusicplayer.components.SharedFab
import com.example.lankiemusicplayer.components.FabMode
import com.example.lankiemusicplayer.navigation.rememberNavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    navController: NavController,
    viewModel: PlayerViewModel
) {

    val playlists = viewModel.getPlaylists()

    var showDialog by remember { mutableStateOf(false) }

    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val navActions = rememberNavigationActions(navController)

    AppScaffold(
        title = "Playlists",

        onHomeClick = navActions::goHome,
        onSearchClick = navActions::goSearch,
        onSettingsClick = {
            navController.navigate("settings")
        },

        // ✅ THIS is the key part
        floatingActionButton = {
            SharedFab(
                mode = FabMode.HOME,

                onSearch = {
                    navController.navigate("search")
                },

                onRefresh = {
                    // optional: reload playlists if needed
                }
            )
        }

    ) {

        // 🔥 THIS replaces Scaffold content

        LazyColumn(
            modifier = Modifier
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
                        .padding(12.dp)
                        .combinedClickable(
                            onClick = {
                                navController.navigate("playlist_detail/${playlist.id}")
                            },
                            onLongClick = {
                                selectedPlaylist = playlist
                            }
                        )
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

    // 🔹 CREATE PLAYLIST
    if (showDialog) {
        CreatePlaylistDialog(
            onCreate = {
                viewModel.createPlaylist(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    // 🔹 ACTION DIALOG
    selectedPlaylist?.let { playlist ->

        AlertDialog(
            onDismissRequest = { selectedPlaylist = null },
            title = { Text(playlist.name) },

            text = {
                Column {

                    TextButton(
                        onClick = {
                            navController.navigate("playlist_detail/${playlist.id}")
                            selectedPlaylist = null
                        }
                    ) {
                        Text("Add Songs")
                    }

                    TextButton(
                        onClick = {
                            showDeleteConfirm = true
                        }
                    ) {
                        Text(
                            "Delete Playlist",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },

            confirmButton = {
                TextButton(onClick = { selectedPlaylist = null }) {
                    Text("Close")
                }
            }
        )
    }

    // 🔹 DELETE CONFIRM
    if (showDeleteConfirm && selectedPlaylist != null) {

        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
                selectedPlaylist = null
            },
            title = { Text("Delete Playlist") },
            text = {
                Text("Are you sure you want to delete \"${selectedPlaylist!!.name}\"?")
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlaylist(selectedPlaylist!!.id)
                        showDeleteConfirm = false
                        selectedPlaylist = null
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}