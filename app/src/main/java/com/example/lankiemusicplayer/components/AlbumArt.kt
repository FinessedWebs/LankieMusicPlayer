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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

@Composable
fun AlbumArt(
    albumId: Long,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    // ✅ Prevent recomputation
    val artworkUri = remember(albumId) {
        ContentUris.withAppendedId(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            albumId
        )
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(artworkUri)
                .size(128) // 🔥 IMPORTANT (performance)
                .crossfade(true)
                .build(),
            contentDescription = "Album art",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        ) {

            when (painter.state) {

                is AsyncImagePainter.State.Success -> {
                    SubcomposeAsyncImageContent()
                }

                else -> {
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
        }
    }
}