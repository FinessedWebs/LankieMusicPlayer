package com.example.lankiemusicplayer.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.lankiemusicplayer.model.Song

@Composable
fun AddSongsDialog(
    allSongs: List<Song>,
    onAdd: (Song) -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = { Text("Add Songs") },

        text = {

            LazyColumn {

                items(allSongs) { song ->

                    TextButton(
                        onClick = {
                            onAdd(song)
                        }
                    ) {

                        Column {

                            Text(song.title)

                            Text(
                                song.artist,
                                style = MaterialTheme.typography.bodySmall
                            )

                        }

                    }

                }

            }

        },

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Done")
            }

        }

    )

}