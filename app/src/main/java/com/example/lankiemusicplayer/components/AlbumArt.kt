package com.example.lankiemusicplayer.components

import android.content.ContentUris
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AlbumArt(
    albumId: Long,
    modifier: Modifier = Modifier
) {

    val artworkUri = ContentUris.withAppendedId(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        albumId
    )

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {

        AsyncImage(
            model = artworkUri,
            contentDescription = "Album art",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // fallback icon if artwork doesn't exist
        Icon(
            imageVector = Icons.Outlined.MusicNote,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}