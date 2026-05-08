package com.example.lankiemusicplayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lankiemusicplayer.ui.theme.SecondaryText

@Composable
fun FavoritePlaylistCards(
    favoriteCount: Int = 0,
    playlistCount: Int = 0,

    onFavoritesClick: () -> Unit,
    onPlaylistsClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ElevatedCard(
            onClick = onFavoritesClick,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(4f / 5f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 5.dp
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorites",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$favoriteCount Songs",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        ElevatedCard(
            onClick = onPlaylistsClick,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(4f / 5f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 5.dp
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                Icon(
                    imageVector = Icons.Filled.Folder,
                    contentDescription = "Playlists",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$playlistCount Lists",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AllSongsButton(
    onClick: () -> Unit
) {

    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(14.dp)
    ) {

        Icon(
            imageVector = Icons.Default.LibraryMusic,
            contentDescription = "All Songs"
        )

        Spacer(Modifier.width(8.dp))

        Text("All Songs")
    }
}