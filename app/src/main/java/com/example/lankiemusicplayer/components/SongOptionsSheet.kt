package com.example.lankiemusicplayer.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOptionsSheet(
    song: Song,
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit,
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAllSongs: (Song) -> Unit
) {

    val context = LocalContext.current
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // 🔹 TITLE
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // 🎵 ACTIONS

            SheetItem("Play Next", Icons.Default.PlayArrow) {
                viewModel.playNext(song)
                onDismiss()
            }

            SheetItem("Add to Queue", Icons.Default.QueueMusic) {
                viewModel.addToQueue(song)
                onDismiss()
            }

            SheetItem("Add to Playlist", Icons.Default.PlaylistAdd) {
                showPlaylistDialog = true
            }

            Divider()

            SheetItem("Go to Artist", Icons.Default.Person) {
                onDismiss()
                onNavigateToArtist(song.artist)
            }

            SheetItem("Go to All Songs", Icons.Default.LibraryMusic) {
                onDismiss()
                onNavigateToAllSongs(song)
            }

            Divider()

            SheetItem("Share", Icons.Default.Share) {

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "audio/*"
                    putExtra(Intent.EXTRA_STREAM, song.uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(
                    Intent.createChooser(intent, "Share song")
                )

                onDismiss()
            }

            SheetItem("Details", Icons.Default.Info) {
                Toast.makeText(
                    context,
                    "Title: ${song.title}\nArtist: ${song.artist}",
                    Toast.LENGTH_LONG
                ).show()
            }

            Divider()

            SheetItem(
                "Delete from device",
                Icons.Default.Delete,
                isDestructive = true
            ) {
                showDeleteDialog = true
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // 🔹 PLAYLIST DIALOG
    if (showPlaylistDialog) {

        val playlists = viewModel.getPlaylists()

        AlertDialog(
            onDismissRequest = { showPlaylistDialog = false },
            title = { Text("Add to Playlist") },
            text = {

                Column {
                    playlists.forEach { playlist ->

                        TextButton(
                            onClick = {

                                viewModel.addSongToPlaylist(
                                    playlist.id,
                                    song
                                )

                                Toast.makeText(
                                    context,
                                    "Added to ${playlist.name}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                showPlaylistDialog = false
                            }
                        ) {
                            Text(playlist.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPlaylistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 🔹 DELETE DIALOG
    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete song") },
            text = { Text("Delete this song from your device?") },

            confirmButton = {
                TextButton(
                    onClick = {

                        context.contentResolver.delete(song.uri, null, null)

                        Toast.makeText(
                            context,
                            "Song deleted",
                            Toast.LENGTH_SHORT
                        ).show()

                        showDeleteDialog = false
                        onDismiss()
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SheetItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {

    NavigationDrawerItem(
        label = { Text(text) },
        selected = false,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = text
            )
        },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedTextColor =
                if (isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
        )
    )
}