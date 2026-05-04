package com.example.lankiemusicplayer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.lankiemusicplayer.model.Song

@Composable
fun SongRow(
    song: Song,
    isPlaying: Boolean,
    isCollaboration: Boolean,
    onClick: () -> Unit,
    onMoreClick: () -> Unit
) {

    ListItem(
        headlineContent = {
            Text(song.title, maxLines = 1)
        },
        supportingContent = {
            Text(song.artist, maxLines = 1)
        },

        trailingContent = {
            IconButton(onClick = onMoreClick) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        },

        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

        colors = ListItemDefaults.colors(
            containerColor =
                when {
                    isPlaying ->
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)

                    isCollaboration ->
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) // 👈 stronger visible grey

                    else ->
                        MaterialTheme.colorScheme.surface
                }
        )
    )
}