package com.example.lankiemusicplayer.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun CreatePlaylistDialog(
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit
) {

    var name by remember { mutableStateOf("") }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Create Playlist")
        },

        text = {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Playlist name") },
                singleLine = true
            )

        },

        confirmButton = {

            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name)
                    }
                }
            ) {
                Text("Create")
            }

        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }

        }

    )

}