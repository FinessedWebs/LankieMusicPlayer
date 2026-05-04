package com.example.lankiemusicplayer.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.model.Song
import com.example.lankiemusicplayer.viewmodel.PlayerViewModel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import com.google.common.collect.Multimaps.index

@Composable
fun SongList(
    songs: List<Song>,
    isCollaborationSong: (Song) -> Boolean = { false }, // 👈 ADD THIS
    modifier: Modifier = Modifier,
    trailingText: (Song, Int) -> String? = { _, _ -> null },
    currentPlayingUri: String? = null,
    viewModel: PlayerViewModel,
    onSongClick: (Song) -> Unit,
    showMenu: Boolean = true,
    enableSwipeToRemove: Boolean = false,
    enableSwipeToPlaylist: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    onNavigateToArtist: (String) -> Unit,
    onNavigateToAllSongs: (Song) -> Unit,
) {

   /* var selectedSongForPlaylist by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }*/
    var selectedSongForPlaylist by remember { mutableStateOf<Song?>(null) }
    var selectedSongForDelete by remember { mutableStateOf<Song?>(null) }


    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),

    ) {


            itemsIndexed(
                items = songs,
                key = { _, it -> it.id }, // 🔥 MUCH faster than uri string
                contentType = { _, _ -> "song" }
            ) { index, song ->

            val isSelected = song.uri.toString() == currentPlayingUri
            val context = LocalContext.current
                val isCollaboration = isCollaborationSong(song)
                val haptic = LocalHapticFeedback.current

            /*val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {

                    if (it == SwipeToDismissBoxValue.StartToEnd) {

                        haptic.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )

                        selectedSongForPlaylist = true
                    }

                    false
                }
            )*/

            val content = @androidx.compose.runtime.Composable {

                ListItem(

                    leadingContent = {

                        AlbumArt(
                            albumId = song.albumId
                        )

                    },

                    trailingContent = {

                        val text = trailingText(song, index)

                        Row {

                            if (text != null) {
                                Text(
                                    text = text,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }

                            if (showMenu) {

                                var showOptions by remember { mutableStateOf(false) }

                                IconButton(
                                    onClick = { showOptions = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Song options"
                                    )
                                }

                                if (showOptions) {
                                    SongOptionsSheet(
                                        song = song,
                                        viewModel = viewModel,
                                        onDismiss = { showOptions = false },
                                        onNavigateToArtist = onNavigateToArtist,
                                        onNavigateToAllSongs = onNavigateToAllSongs
                                    )
                                }
                            }
                        }
                    },

                    headlineContent = {
                        Text(
                            text = song.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color =
                                if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        )
                    },/*leadingContent = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },*/


                    supportingContent = {
                        Text(
                            text = song.artist,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color =
                                if (isSelected)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onSongClick(song)
                        },



                colors = ListItemDefaults.colors(
                    containerColor =
                        when {
                            isSelected ->
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)

                            isCollaboration ->
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) // 👈 LIGHT GREY

                            else ->
                                Color.Transparent
                        }
                )
                )
            }

            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->

                    when (value) {

                        SwipeToDismissBoxValue.StartToEnd -> {
                            selectedSongForPlaylist = song
                        }

                        SwipeToDismissBoxValue.EndToStart -> {
                            if (enableSwipeToRemove) {
                                selectedSongForDelete = song
                            }
                        }

                        else -> {}
                    }

                    false // prevent auto-dismiss
                }
            )

            if (enableSwipeToPlaylist || enableSwipeToRemove) {

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromEndToStart = enableSwipeToRemove,
                    enableDismissFromStartToEnd = enableSwipeToPlaylist,

                    backgroundContent = {

                        val isSwiping =
                            dismissState.targetValue != SwipeToDismissBoxValue.Settled ||
                                    dismissState.currentValue != SwipeToDismissBoxValue.Settled

                        val isEndToStart =
                            dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            contentAlignment =
                                if (isEndToStart) Alignment.CenterEnd else Alignment.CenterStart
                        ) {

                            if (isSwiping) {

                                Icon(
                                    imageVector =
                                        if (isEndToStart)
                                            Icons.Default.Delete
                                        else
                                            Icons.Default.PlaylistAdd,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                ) {
                    content()
                }

            } else {
                content()

            }


        }

    }

    selectedSongForPlaylist?.let { song ->

        val context = LocalContext.current
        val playlists = viewModel.getPlaylists()

        AlertDialog(
            onDismissRequest = { selectedSongForPlaylist = null },
            title = { Text("Add to Playlist") },
            text = {
                Column {
                    playlists.forEach { playlist ->
                        TextButton(
                            onClick = {
                                viewModel.addSongToPlaylist(playlist.id, song)

                                Toast.makeText(
                                    context,
                                    "Added to ${playlist.name}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                selectedSongForPlaylist = null
                            }
                        ) {
                            Text(playlist.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedSongForPlaylist = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    selectedSongForDelete?.let { song ->

        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { selectedSongForDelete = null },
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

                        selectedSongForDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { selectedSongForDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}